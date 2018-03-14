package org.graalvm.vm.x86.isa;

public class IllegalInstructionException extends RuntimeException {
    private static final long serialVersionUID = -2754913616070473400L;

    private final long pc;
    private final byte[] instruction;

    public IllegalInstructionException(long pc, byte[] instruction, String message) {
        super(message);
        this.pc = pc;
        this.instruction = instruction;
    }

    public long getPC() {
        return pc;
    }

    public byte[] getInstruction() {
        return instruction;
    }
}
