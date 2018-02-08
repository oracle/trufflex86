package org.graalvm.vm.x86.isa;

public class OperandDecoder {
    public static final int R8 = 0;
    public static final int R16 = 1;
    public static final int R32 = 2;
    public static final int R64 = 3;

    private final ModRM modrm;
    private final SIB sib;
    private final long displacement;
    private final AMD64RexPrefix rex;

    public OperandDecoder(ModRM modrm, SIB sib, long displacement) {
        this.modrm = modrm;
        this.sib = sib;
        this.displacement = displacement;
        this.rex = null;
    }

    public OperandDecoder(ModRM modrm, SIB sib, long displacement, AMD64RexPrefix rex) {
        this.modrm = modrm;
        this.sib = sib;
        this.displacement = displacement;
        this.rex = rex;
    }

    public Operand getOperand1(int type) {
        if (rex != null) {
            // TODO!
            if (modrm.hasSIB()) {
                if (modrm.hasDisplacement()) {
                    return new MemoryOperand(sib.getBase(rex.b), sib.getIndex(rex.x), sib.getShift(), displacement);
                }
            }
        }
        if (modrm.hasSIB()) {
            if (modrm.hasDisplacement()) {
                return new MemoryOperand(sib.getBase(), sib.getIndex(), sib.getShift(), displacement);
            } else {
                return new MemoryOperand(sib.getBase(), sib.getIndex(), sib.getShift());
            }
        } else {
            return modrm.getOperand1(ModRM.A32, type);
        }
    }

    public Operand getOperand2(int type) {
        return new RegisterOperand(modrm.getOperand2(type));
    }
}
