package org.graalvm.vm.x86.node.init;

import org.graalvm.vm.memory.ByteMemory;
import org.graalvm.vm.memory.Memory;
import org.graalvm.vm.memory.MemoryPage;
import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.x86.AMD64;
import org.graalvm.vm.x86.AMD64Context;
import org.graalvm.vm.x86.AMD64Register;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.node.AMD64Node;
import org.graalvm.vm.x86.node.RegisterWriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;

public class InitializerNode extends AMD64Node {
    private final String programName;

    @Child private LoaderNode setup;
    @Children private RegisterWriteNode[] registers;

    public InitializerNode(ArchitecturalState state, String programName) {
        this.setup = new LoaderNode(state);
        this.programName = programName;
        registers = new RegisterWriteNode[16];
        for (int i = 0; i < 16; i++) {
            AMD64Register reg = state.getRegisters().getRegister(Register.get(i));
            registers[i] = reg.createWrite();
        }
    }

    @ExplodeLoop
    public void execute(VirtualFrame frame) {
        for (RegisterWriteNode register : registers) {
            register.executeI64(frame, 0);
        }

        AMD64Context ctx = getContextReference().get();
        VirtualMemory memory = ctx.getMemory();
        long stackbase = memory.pageStart(AMD64.STACK_BASE);
        long stacksize = memory.roundToPageSize(AMD64.STACK_SIZE);
        Memory stackMemory = new ByteMemory(stacksize);
        MemoryPage stack = new MemoryPage(stackMemory, stackbase, stacksize, "[stack]");
        memory.add(stack);
        long sp = AMD64.STACK_ADDRESS - 16;
        assert (sp & 0xf) == 0;
        registers[Register.RSP.getID()].executeI64(frame, sp);

        String[] args = ctx.getArguments();
        setup.execute(frame, programName, args);
    }
}
