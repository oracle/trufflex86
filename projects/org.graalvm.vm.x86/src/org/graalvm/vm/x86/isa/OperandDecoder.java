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
                    if (sib.index == 0b100 && !rex.b) { // rsp not used
                        return new MemoryOperand(sib.getBase(rex.b), displacement);
                    } else {
                        return new MemoryOperand(sib.getBase(rex.b), sib.getIndex(rex.x), sib.getShift(), displacement);
                    }
                } else {
                    return new MemoryOperand(sib.getBase(rex.b), sib.getIndex(rex.x), sib.getShift());
                }
            } else if (modrm.hasDisplacement()) {
                RegisterOperand op = (RegisterOperand) modrm.getOperand1(ModRM.A64, type);
                Register reg = Register.RIP;
                if (op != null) {
                    reg = op.getRegister();
                    reg = getRegister(reg, rex.b);
                }
                return new MemoryOperand(reg, displacement);
            } else {
                Operand op = modrm.getOperand1(ModRM.A64, type);
                if (op instanceof RegisterOperand) {
                    Register reg = ((RegisterOperand) op).getRegister();
                    return new RegisterOperand(getRegister(reg, rex.b));
                } else {
                    return op;
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
            if (modrm.hasDisplacement()) {
                RegisterOperand op = (RegisterOperand) modrm.getOperand1(ModRM.A64, type);
                Register reg = Register.RIP;
                if (op != null) {
                    reg = op.getRegister();
                }
                return new MemoryOperand(reg, displacement);
            } else {
                return modrm.getOperand1(ModRM.A64, type);
            }
        }
    }

    private static Register getRegister(Register reg, boolean r) {
        if (r) {
            return Register.get(reg.getID() + 8);
        } else {
            return reg;
        }
    }

    public Operand getOperand2(int type) {
        if (rex != null && rex.r) {
            Register reg = modrm.getOperand2(type);
            return new RegisterOperand(getRegister(reg, rex.r));
        } else {
            return new RegisterOperand(modrm.getOperand2(type));
        }
    }
}
