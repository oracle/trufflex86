package org.graalvm.vm.x86.emu;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.memory.hardware.linux.MemoryMap;
import org.graalvm.vm.memory.hardware.linux.MemorySegment;
import org.graalvm.vm.memory.util.HexFormatter;
import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.x86.isa.CpuState;
import org.graalvm.vm.x86.node.debug.trace.BrkRecord;
import org.graalvm.vm.x86.node.debug.trace.CallArgsRecord;
import org.graalvm.vm.x86.node.debug.trace.ExecutionTraceReader;
import org.graalvm.vm.x86.node.debug.trace.LocationRecord;
import org.graalvm.vm.x86.node.debug.trace.MemoryEventRecord;
import org.graalvm.vm.x86.node.debug.trace.MmapRecord;
import org.graalvm.vm.x86.node.debug.trace.MprotectRecord;
import org.graalvm.vm.x86.node.debug.trace.MunmapRecord;
import org.graalvm.vm.x86.node.debug.trace.Record;
import org.graalvm.vm.x86.node.debug.trace.StepRecord;
import org.graalvm.vm.x86.node.debug.trace.SystemLogRecord;
import org.graalvm.vm.x86.posix.ProcessExitException;

import com.everyware.posix.api.PosixException;
import com.everyware.posix.api.mem.Mman;
import com.everyware.util.BitTest;
import com.everyware.util.log.Levels;
import com.everyware.util.log.Trace;

public class Verify86 {
    private static final Logger log = Trace.create(Verify86.class);

    private static final long SYSCALL = 0x050F;
    private static final long CPUID = 0xA20F;
    private static final long RDTSC = 0x310F;
    private static final long REP_STOSB = 0xAAF3;
    private static final long REP_STOSD = 0xABF3;
    private static final long REP_STOSQ = 0xAB48F3;
    private static final long REP_CMPSB = 0xA6F3;
    private static final long REP_MOVSB = 0xA4F3;
    private static final long REP_MOVSQ = 0xA548F3;
    private static final long REPNZ_SCASB = 0xAEF2;

    private static final Set<String> MNEMONIC_MEM_CHECK;
    private static final Set<String> MNEMONIC_MEM_IGNORE;

    private final Ptrace ptrace;
    private Registers regs;
    private final PtraceVirtualMemory memory;

    private final ExecutionTraceReader trace;

    private Record currentRecord;

    private long currentBrk;
    private boolean transfer;
    private StepRecord lastStep;

    static {
        MNEMONIC_MEM_CHECK = new HashSet<>();
        MNEMONIC_MEM_CHECK.add("syscall");
        MNEMONIC_MEM_CHECK.add("cmp");
        MNEMONIC_MEM_CHECK.add("test");
        MNEMONIC_MEM_CHECK.add("call");
        MNEMONIC_MEM_CHECK.add("jmp");
        MNEMONIC_MEM_CHECK.add("push");
        MNEMONIC_MEM_CHECK.add("fxrstor");

        MNEMONIC_MEM_IGNORE = new HashSet<>();
        MNEMONIC_MEM_IGNORE.add("adc");
        MNEMONIC_MEM_IGNORE.add("add");
        MNEMONIC_MEM_IGNORE.add("sub");
        MNEMONIC_MEM_IGNORE.add("inc");
        MNEMONIC_MEM_IGNORE.add("dec");
        MNEMONIC_MEM_IGNORE.add("xchg");
        MNEMONIC_MEM_IGNORE.add("cmpxchg");
        MNEMONIC_MEM_IGNORE.add("xadd");
        MNEMONIC_MEM_IGNORE.add("and");
        MNEMONIC_MEM_IGNORE.add("or");
        MNEMONIC_MEM_IGNORE.add("xor");
        MNEMONIC_MEM_IGNORE.add("not");
        MNEMONIC_MEM_IGNORE.add("sar");
        MNEMONIC_MEM_IGNORE.add("shr");
        MNEMONIC_MEM_IGNORE.add("shl");
        MNEMONIC_MEM_IGNORE.add("mul");
        MNEMONIC_MEM_IGNORE.add("imul");
        MNEMONIC_MEM_IGNORE.add("div");
        MNEMONIC_MEM_IGNORE.add("idiv");
    }

    public Verify86(Ptrace ptrace, PtraceVirtualMemory memory, ExecutionTraceReader trace) throws PosixException {
        this.ptrace = ptrace;
        this.memory = memory;
        this.trace = trace;
        regs = ptrace.getRegisters();
        currentBrk = 0;
        transfer = true;
        lastStep = null;
    }

    public void close() throws IOException {
        if (trace != null) {
            trace.close();
        }
    }

    private void check(String msg, long ref, long act) {
        if (ref != act) {
            throw new AssertionError("Register mismatch at 0x" + HexFormatter.tohex(regs.rip, 16) + ": " + msg + " ref=0x" + HexFormatter.tohex(ref, 16) + ", act=0x" + HexFormatter.tohex(act, 16));
        }
    }

    private void check(String msg, Vector128 ref, Vector128 act) {
        if (!ref.equals(act)) {
            throw new AssertionError("Register mismatch at 0x" + HexFormatter.tohex(regs.rip, 16) + ": " + msg + " ref=" + ref + ", act=" + act);
        }
    }

    public void step() throws ProcessExitException, PosixException {
        StepRecord record = (StepRecord) currentRecord;
        CpuState state = record.getState().getState();
        LocationRecord loc = record.getLocation();
        byte[] mcode = loc.getMachinecode();
        regs = ptrace.getRegisters();
        if (transfer) {
            // transfer registers from trace to host cpu
            regs.rip = state.rip;
            regs.rax = state.rax;
            regs.rbx = state.rbx;
            regs.rcx = state.rcx;
            regs.rdx = state.rdx;
            regs.rsi = state.rsi;
            regs.rdi = state.rdi;
            regs.rbp = state.rbp;
            regs.rsp = state.rsp;
            regs.r8 = state.r8;
            regs.r9 = state.r9;
            regs.r10 = state.r10;
            regs.r11 = state.r11;
            regs.r12 = state.r12;
            regs.r13 = state.r13;
            regs.r14 = state.r14;
            regs.r15 = state.r15;
            regs.fs_base = state.fs;
            regs.gs_base = state.gs;
            regs.rflags = state.getRFL();
            regs.mxcsr = 0x1f80;
            regs.fcwd = 0x037f;
            for (int i = 0; i < 8; i++) {
                regs.setST(i, Vector128.ZERO);
            }
            for (int i = 0; i < 16; i++) {
                regs.setXMM(i, state.xmm[i]);
            }
            ptrace.setRegisters(regs);
            transfer = false;
        } else {
            // check
            check("rip", state.rip, regs.rip);
            check("rax", state.rax, regs.rax);
            check("rbx", state.rbx, regs.rbx);
            check("rcx", state.rcx, regs.rcx);
            check("rdx", state.rdx, regs.rdx);
            check("rsi", state.rsi, regs.rsi);
            check("rdi", state.rdi, regs.rdi);
            check("rbp", state.rbp, regs.rbp);
            check("rsp", state.rsp, regs.rsp);
            check("r8", state.r8, regs.r8);
            check("r9", state.r9, regs.r9);
            check("r10", state.r10, regs.r10);
            check("r11", state.r11, regs.r11);
            check("r12", state.r12, regs.r12);
            check("r13", state.r13, regs.r13);
            check("r14", state.r14, regs.r14);
            check("r15", state.r15, regs.r15);
            check("fs", state.fs, regs.fs_base);
            check("gs", state.gs, regs.gs_base);
            // check("mxcsr", 0x1f80, regs.mxcsr);
            // check("fcwd", 0x037f, regs.fcwd);
            for (int i = 0; i < 16; i++) {
                check("xmm" + i, state.xmm[i], regs.getXMM(i));
            }
            for (int i = 0; i < 8; i++) {
                check("mm" + i, Vector128.ZERO, regs.getST(i));
            }
            if (regs.mxcsr != 0x1f80) {
                regs.mxcsr = 0x1f80;
                ptrace.setRegisters(regs);
            }
            if (regs.fcwd != 0x037f) {
                regs.fcwd = 0x037f;
                ptrace.setRegisters(regs);
            }
        }
        for (int i = 0; i < mcode.length; i++) {
            byte val = memory.getI8(regs.rip + i);
            if (val != mcode[i]) {
                throw new AssertionError("machine code mismatch at 0x" + HexFormatter.tohex(regs.rip + i, 16));
            }
        }
        long insn = ptrace.read(regs.rip);
        if ((insn & 0xFFFF) == SYSCALL) {
            transfer = true;
            regs.rip += 2;
            ptrace.setRegisters(regs);
        } else if ((insn & 0xFFFF) == CPUID) {
            transfer = true;
            regs.rip += 2;
            ptrace.setRegisters(regs);
        } else if ((insn & 0xFFFF) == RDTSC) {
            transfer = true;
            regs.rip += 2;
            ptrace.setRegisters(regs);
        } else if ((insn & 0xFFFF) == REP_STOSB || ((insn & 0xFFFF) == REP_STOSD) || ((insn & 0xFFFFFF) == REP_STOSQ) || ((insn & 0xFFFF) == REP_CMPSB) || ((insn & 0xFFFF) == REP_MOVSB) ||
                        ((insn & 0xFFFFFF) == REP_MOVSQ) ||
                        ((insn & 0xFFFF) == REPNZ_SCASB)) {
            // step next
            long rip = regs.rip;
            do {
                if (!ptrace.step()) {
                    throw new ProcessExitException(-1);
                }
                regs = ptrace.getRegisters();
            } while (regs.rip == rip);
        } else {
            if (!ptrace.step()) {
                throw new ProcessExitException(-1);
            }
        }
    }

    private void checkRead(MemoryEventRecord evt) {
        switch (evt.getSize()) {
            case 1:
                if (memory.getI8(evt.getAddress()) != (byte) evt.getValue()) {
                    throw new AssertionError("memory data mismatch at 0x" + HexFormatter.tohex(evt.getAddress(), 16) + ": 0x" + HexFormatter.tohex(memory.getI8(evt.getAddress()), 2) +
                                    " vs 0x" + HexFormatter.tohex((byte) evt.getValue(), 2));
                }
                break;
            case 2:
                if (memory.getI16(evt.getAddress()) != (short) evt.getValue()) {
                    throw new AssertionError("memory data mismatch at 0x" + HexFormatter.tohex(evt.getAddress(), 16) + ": 0x" + HexFormatter.tohex(memory.getI16(evt.getAddress()), 4) +
                                    " vs 0x" + HexFormatter.tohex((short) evt.getValue(), 4));
                }
                break;
            case 4:
                if (memory.getI32(evt.getAddress()) != (int) evt.getValue()) {
                    throw new AssertionError("memory data mismatch at 0x" + HexFormatter.tohex(evt.getAddress(), 16) + ": 0x" + HexFormatter.tohex(memory.getI32(evt.getAddress()), 8) +
                                    " vs 0x" + HexFormatter.tohex((int) evt.getValue(), 8));
                }
                break;
            case 8:
                if (memory.getI64(evt.getAddress()) != evt.getValue()) {
                    throw new AssertionError(
                                    "memory data mismatch at 0x" + HexFormatter.tohex(evt.getAddress(), 16) + ": 0x" + HexFormatter.tohex(memory.getI64(evt.getAddress()), 16) +
                                                    " vs 0x" + HexFormatter.tohex(evt.getValue(), 16));
                }
                break;
            case 16:
                if (!memory.getI128(evt.getAddress()).equals(evt.getVector())) {
                    throw new AssertionError(
                                    "memory data mismatch at 0x" + HexFormatter.tohex(evt.getAddress(), 16) + ": " + memory.getI128(evt.getAddress()) + " vs " + evt.getVector());
                }
                break;
        }
    }

    // NOTE: to use memory watches, set memwatchAddress to the memory address and memwatchValue to
    // the expected value at that address
    private boolean check = false;
    private long memwatchAddress = 0;
    private long memwatchValue = 0;

    public int execute() throws PosixException, IOException {
        try {
            while (true) {
                currentRecord = trace.read();
                if (currentRecord == null) {
                    return 0;
                }
                if (currentRecord instanceof MemoryEventRecord) {
                    MemoryEventRecord evt = (MemoryEventRecord) currentRecord;
                    String mnemonic = null;
                    if (lastStep != null) {
                        mnemonic = lastStep.getLocation().getMnemonic();
                    }
                    if ((evt.getAddress() & 0xFFFFFFFFFFFFFFF0L) == (memwatchAddress & 0xFFFFFFFFFFFFFFF0L)) {
                        if (!check && evt.getAddress() == memwatchAddress && evt.getValue() == memwatchValue) {
                            log.info("[MEM] found: " + evt);
                            check = true;
                        } else {
                            log.info("[MEM] " + evt);
                        }
                        // checkRead(evt);
                    }
                    if (evt.isWrite()) {
                        if (lastStep == null || mnemonic.equals("syscall")) {
                            // write (initialization / syscall)
                            switch (evt.getSize()) {
                                case 1:
                                    memory.setI8(evt.getAddress(), (byte) evt.getValue());
                                    break;
                                case 2:
                                    memory.setI16(evt.getAddress(), (short) evt.getValue());
                                    break;
                                case 4:
                                    memory.setI32(evt.getAddress(), (int) evt.getValue());
                                    break;
                                case 8:
                                    memory.setI64(evt.getAddress(), evt.getValue());
                                    break;
                                case 16:
                                    memory.setI128(evt.getAddress(), evt.getVector());
                                    break;
                                default:
                                    throw new AssertionError("unknown word size: " + evt.getSize());
                            }
                        } else {
                            // read (normal instruction)
                            checkRead(evt);
                        }
                    } else {
                        if (mnemonic != null && MNEMONIC_MEM_CHECK.contains(mnemonic)) {
                            // read (syscall)
                            checkRead(evt);
                        } else {
                            if (lastStep == null) {
                                // read (no previous step)
                                checkRead(evt);
                            } else {
                                String[] cmd = lastStep.getLocation().getAssembly();
                                if (cmd.length == 1) {
                                    checkRead(evt);
                                } else if (cmd.length > 1 && cmd[1].contains("[")) {
                                    if (!MNEMONIC_MEM_IGNORE.contains(mnemonic)) {
                                        log.info("[MEM] ignoring " + evt + ": " + lastStep.getLocation().getDisassembly());
                                    }
                                    // don't check (instruction probably contains a store operation)
                                } else {
                                    // read (normal instruction)
                                    checkRead(evt);
                                }
                            }
                        }
                    }
                } else if (currentRecord instanceof MmapRecord) {
                    MmapRecord mmap = (MmapRecord) currentRecord;
                    log.info("[MEMORY] " + mmap);
                    if (mmap.getResult() != -1) {
                        long base = mmap.getResult();
                        long addr = base & VirtualMemory.PAGE_MASK;
                        long diff = base - addr;
                        long length = mmap.getLength() - diff;
                        ptrace.mmap(addr, length, BitTest.test(mmap.getProtection(), Mman.PROT_READ), BitTest.test(mmap.getProtection(), Mman.PROT_WRITE),
                                        BitTest.test(mmap.getProtection(), Mman.PROT_EXEC), true, true, false, -1, 0);
                        byte[] data = mmap.getData();
                        if (data != null && data.length > 0) {
                            log.info("mmap length: " + mmap.getLength() + ", data length: " + data.length);
                            int min = (int) Math.min(data.length, mmap.getLength());
                            for (int i = 0; i < min; i++) {
                                memory.setI8(base + i, data[i]);
                            }
                        }
                    }
                } else if (currentRecord instanceof MprotectRecord) {
                    MprotectRecord mprotect = (MprotectRecord) currentRecord;
                    log.info("[MEMORY] " + mprotect);
                    ptrace.mprotect(mprotect.getAddress(), mprotect.getLength(), BitTest.test(mprotect.getProtection(), Mman.PROT_READ), BitTest.test(mprotect.getProtection(), Mman.PROT_WRITE),
                                    BitTest.test(mprotect.getProtection(), Mman.PROT_EXEC));
                } else if (currentRecord instanceof MunmapRecord) {
                    MunmapRecord munmap = (MunmapRecord) currentRecord;
                    log.info("[MEMORY] " + munmap);
                    ptrace.munmap(munmap.getAddress(), munmap.getLength());
                } else if (currentRecord instanceof BrkRecord) {
                    BrkRecord brk = (BrkRecord) currentRecord;
                    log.info("[MEMORY] " + brk);
                    if (brk.getBrk() == 0) {
                        if (currentBrk == 0) {
                            currentBrk = brk.getResult();
                        } else if (currentBrk != brk.getResult()) {
                            throw new AssertionError("brk mismatch: " + HexFormatter.tohex(currentBrk, 16) + " vs " + HexFormatter.tohex(brk.getResult(), 16));
                        }
                    } else if (currentBrk < brk.getResult()) {
                        long addr = currentBrk & VirtualMemory.PAGE_MASK;
                        long len = memory.roundToPageSize(brk.getResult() - addr);
                        ptrace.mmap(addr, len, true, true, false, true, true, false, -1, 0);
                        currentBrk = brk.getResult();
                    }
                } else if (currentRecord instanceof StepRecord) {
                    // log.info("[STEP] " + currentRecord);
                    step();
                    lastStep = (StepRecord) currentRecord;
                    if (check) {
                        if (memory.getI64(memwatchAddress) != memwatchValue) {
                            log.severe("[MEMWATCH] 0x" + HexFormatter.tohex(memwatchAddress, 16) + " was overwritten with 0x" + HexFormatter.tohex(memory.getI64(0x00007fff6c8441e8L), 16));
                            log.severe("[MEMWATCH] Instruction: " + currentRecord);
                            check = false;
                        }
                    }
                } else if (currentRecord instanceof SystemLogRecord) {
                    log.info("[LOG] " + currentRecord);
                } else if (currentRecord instanceof CallArgsRecord) {
                    // ignore
                } else {
                    throw new AssertionError("unknown record type: " + (currentRecord == null ? "null" : currentRecord.getClass().getCanonicalName()));
                }
            }
        } catch (ProcessExitException e) {
            return e.getCode();
        } catch (Throwable t) {
            if (lastStep != null) {
                log.info("Exception: " + t);
                if (lastStep != null) {
                    log.info("[STEP] " + lastStep);
                }
            }
            throw t;
        }
    }

    private static void run(Ptrace ptrace, ExecutionTraceReader trace) throws Exception {
        PtraceVirtualMemory mem = new PtraceVirtualMemory(ptrace);
        Verify86 verify = new Verify86(ptrace, mem, trace);
        verify.execute();
    }

    public static void main(String[] args) throws Exception {
        Trace.setupConsoleApplication(Levels.INFO);
        System.loadLibrary("emu86");

        if (args.length != 1) {
            System.out.println("Usage: verify86 trace.trc");
            System.exit(1);
        }

        String trcfile = args[0];

        log.info("Verifying execution trace " + trcfile);

        int pid = -1;
        try (Ptrace ptrace = new Ptrace()) {
            pid = ptrace.getPid();

            try (InputStream trcin = new BufferedInputStream(new FileInputStream(trcfile));
                            ExecutionTraceReader trace = new ExecutionTraceReader(trcin)) {
                run(ptrace, trace);
            } catch (Exception | AssertionError e) {
                e.printStackTrace();
                Registers regs = ptrace.getRegisters();
                System.out.println("Register dump:");
                System.out.println(regs);
                System.out.println("Memory map for process " + pid + ":");
                MemoryMap map = new MemoryMap(pid);
                for (MemorySegment s : map.getSegments()) {
                    System.out.println(s);
                }
            }
        }
    }
}
