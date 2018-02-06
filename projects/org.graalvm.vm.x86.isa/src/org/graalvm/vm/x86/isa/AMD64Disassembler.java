package org.graalvm.vm.x86.isa;

import java.util.HashMap;
import java.util.Map;

import com.everyware.posix.elf.Elf;
import com.everyware.posix.elf.ProgramHeader;
import com.everyware.posix.elf.Symbol;

public class AMD64Disassembler {
    public static String disassemble(Elf elf) {
        Map<Long, Symbol> symbols = new HashMap<>();
        for (Symbol sym : elf.getSymbolTable().getSymbols()) {
            if (sym.getSectionIndex() != Symbol.SHN_UNDEF) {
                symbols.put(sym.getValue(), sym);
            }
        }
        StringBuilder buf = new StringBuilder();
        for (ProgramHeader hdr : elf.getProgramHeaders()) {
            if (hdr.getType() == Elf.PT_LOAD) {
                if (!hdr.getFlag(Elf.PF_X)) {
                    // not executable
                    continue;
                }
                assert (int) hdr.getMemorySize() == hdr.getMemorySize();
                CodeSegmentReader reader = new CodeSegmentReader(hdr);
                while (reader.isAvailable()) {
                    long pc = reader.getPC();
                    AMD64Instruction insn = AMD64InstructionDecoder.decode(pc, reader);
                    Symbol sym = symbols.get(pc);
                    if (sym != null) {
                        buf.append(sym.getName()).append(":\n");
                    }
                    buf.append(String.format("%016x: %s\n", pc, insn));
                }
            }
        }
        return buf.toString();
    }
}
