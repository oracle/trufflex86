package org.graalvm.vm.x86.isa;

import org.graalvm.vm.memory.util.HexFormatter;

public class IllegalInstructionException extends RuntimeException {
    private static final long serialVersionUID = -2754913616070473400L;

    private final long pc;
    private final byte[] instruction;

    public IllegalInstructionException(long pc, byte[] instruction, String message) {
        super("[pc=" + HexFormatter.tohex(pc, 16) + ": " + getBinary(instruction) + "] " + message);
        this.pc = pc;
        this.instruction = instruction;
    }

    public long getPC() {
        return pc;
    }

    public byte[] getInstruction() {
        return instruction;
    }

    private static String getBinary(byte[] b) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            if (i > 0) {
                buf.append(' ');
            }
            buf.append(HexFormatter.tohex(Byte.toUnsignedInt(b[i]), 2));
        }
        return buf.toString();
    }
}
