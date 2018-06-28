package org.graalvm.vm.x86;

import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.x86.node.MemoryReadNode;
import org.graalvm.vm.x86.node.MemoryWriteNode;
import org.graalvm.vm.x86.node.flow.TraceRegistry;

import com.oracle.truffle.api.frame.FrameSlot;

public class ArchitecturalState {
    private final RegisterAccessFactory registerAccess;
    private final VirtualMemory memory;
    private final FrameSlot instructionCount;
    private final TraceRegistry traces;

    public ArchitecturalState(AMD64Context context) {
        registerAccess = new RegisterAccessFactory(context.getGPRs(), context.getZMMs(), context.getXMMs(), context.getXMMF32(), context.getXMMF64(), context.getXMMType(), context.getFS(),
                        context.getGS(), context.getPC(), context.getCF(), context.getPF(), context.getAF(), context.getZF(), context.getSF(), context.getDF(), context.getOF());
        memory = context.getMemory();
        instructionCount = context.getInstructionCount();
        traces = context.getTraceRegistry();
    }

    public RegisterAccessFactory getRegisters() {
        return registerAccess;
    }

    public VirtualMemory getMemory() {
        return memory;
    }

    public MemoryReadNode createMemoryRead() {
        return new MemoryReadNode(memory);
    }

    public MemoryWriteNode createMemoryWrite() {
        return new MemoryWriteNode(memory);
    }

    public FrameSlot getInstructionCount() {
        return instructionCount;
    }

    public TraceRegistry getTraceRegistry() {
        return traces;
    }
}
