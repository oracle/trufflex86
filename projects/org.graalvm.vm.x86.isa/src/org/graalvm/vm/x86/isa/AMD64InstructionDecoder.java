package org.graalvm.vm.x86.isa;

import org.graalvm.vm.x86.isa.instruction.Syscall;
import org.graalvm.vm.x86.isa.instruction.Inc.Incl;
import org.graalvm.vm.x86.isa.instruction.Inc.Incw;
import org.graalvm.vm.x86.isa.instruction.Mov.Movl;
import org.graalvm.vm.x86.isa.instruction.Mov.Movq;
import org.graalvm.vm.x86.isa.instruction.Mov.Movw;
import org.graalvm.vm.x86.isa.instruction.Xor.Xorb;
import org.graalvm.vm.x86.isa.instruction.Xor.Xorw;
import org.graalvm.vm.x86.isa.instruction.Xor.Xorl;

public class AMD64InstructionDecoder {
    private static final Register[] REG16 = {Register.AX, Register.CX, Register.DX, Register.BX, Register.SP, Register.BP, Register.SI, Register.DI};
    private static final Register[] REG32 = {Register.EAX, Register.ECX, Register.EDX, Register.EBX, Register.ESP, Register.EBP, Register.ESI, Register.EDI};

    public static AMD64Instruction decode(long pc, CodeReader code) {
        byte op = code.read8();
        boolean sizeOverride = false;
        AMD64RexPrefix rex = null;
        switch (op) {
            case AMD64InstructionPrefix.OPERAND_SIZE_OVERRIDE:
                sizeOverride = true;
                op = code.read8();
                break;
        }
        if (AMD64RexPrefix.isREX(op)) {
            rex = new AMD64RexPrefix(op);
            op = code.read8();
        }
        switch (op) {
            case AMD64Opcode.INC_RM: {
                assert rex == null;
                Args args = new Args(code);
                if (sizeOverride) {
                    return new Incw(pc, args.getOp(new byte[]{AMD64InstructionPrefix.OPERAND_SIZE_OVERRIDE, op}), args.getOperandDecoder());
                } else {
                    return new Incl(pc, args.getOp(new byte[]{op}), args.getOperandDecoder());
                }
            }
            case AMD64Opcode.MOV_RM_R: {
                assert rex == null;
                Args args = new Args(code);
                if (sizeOverride) {
                    return new Movw(pc, args.getOp(new byte[]{AMD64InstructionPrefix.OPERAND_SIZE_OVERRIDE, op}), args.getOperandDecoder());
                } else {
                    return new Movl(pc, args.getOp(new byte[]{op}), args.getOperandDecoder());
                }
            }
            case AMD64Opcode.MOV_RM_I: {
                Args args = new Args(code);
                if (rex != null && rex.w) {
                    int imm = code.read32();
                    return new Movq(pc, args.getOp(new byte[]{rex.getPrefix(), op}), args.getOperandDecoder(), imm);
                }
                System.out.println("REX: " + rex);
                throw new IllegalArgumentException();
            }
            case AMD64Opcode.MOV_R_I + 0:
            case AMD64Opcode.MOV_R_I + 1:
            case AMD64Opcode.MOV_R_I + 2:
            case AMD64Opcode.MOV_R_I + 3:
            case AMD64Opcode.MOV_R_I + 4:
            case AMD64Opcode.MOV_R_I + 5:
            case AMD64Opcode.MOV_R_I + 6:
            case AMD64Opcode.MOV_R_I + 7: {
                assert rex == null;
                if (sizeOverride) {
                    short imm = code.read16();
                    Register reg = getRegister16(op);
                    return new Movw(pc, new byte[]{op, (byte) imm, (byte) (imm >> 8)}, new RegisterOperand(reg), imm);
                } else {
                    int imm = code.read32();
                    Register reg = getRegister32(op);
                    return new Movl(pc, new byte[]{op, (byte) imm, (byte) (imm >> 8), (byte) (imm >> 16), (byte) (imm >> 24)}, new RegisterOperand(reg), imm);
                }
            }
            case AMD64Opcode.XOR_RM_R: {
                assert rex == null;
                Args args = new Args(code);
                if (sizeOverride) {
                    return new Xorw(pc, args.getOp(new byte[]{AMD64InstructionPrefix.OPERAND_SIZE_OVERRIDE, op}), args.getOperandDecoder());
                } else {
                    return new Xorl(pc, args.getOp(new byte[]{op}), args.getOperandDecoder());
                }
            }
            case AMD64Opcode.XOR_RM8_R8: {
                assert rex == null;
                Args args = new Args(code);
                return new Xorb(pc, args.getOp(new byte[]{op}), args.getOperandDecoder());
            }
            case AMD64Opcode.ESCAPE: {
                byte op2 = code.read8();
                switch (op2) {
                    case AMD64Opcode.SYSCALL:
                        return new Syscall(pc, new byte[]{AMD64Opcode.ESCAPE, op, op2});
                    default:
                        return new IllegalInstruction(pc, new byte[]{AMD64Opcode.ESCAPE, op, op2});
                }
            }
            default:
                return new IllegalInstruction(pc, new byte[]{op});
        }
    }

    private static Register getRegister16(byte op) {
        int reg = op & 0x7;
        return REG16[reg];
    }

    private static Register getRegister32(byte op) {
        int reg = op & 0x7;
        return REG32[reg];
    }

    private static class Args {
        public final ModRM modrm;
        public final SIB sib;
        public final long displacement;

        public final byte[] bytes;

        public Args(CodeReader code) {
            modrm = new ModRM(code.read8());
            if (modrm.hasSIB()) {
                sib = new SIB(code.read8());
            } else {
                sib = null;
            }
            switch (modrm.getDisplacementSize()) {
                case 1:
                    displacement = code.read8();
                    if (sib == null) {
                        bytes = new byte[]{modrm.getModRM(), (byte) displacement};
                    } else {
                        bytes = new byte[]{modrm.getModRM(), sib.getSIB(), (byte) displacement};
                    }
                    break;
                case 2:
                    displacement = code.read16();
                    if (sib == null) {
                        bytes = new byte[]{modrm.getModRM(), (byte) displacement, (byte) (displacement >> 8)};
                    } else {
                        bytes = new byte[]{modrm.getModRM(), sib.getSIB(), (byte) displacement, (byte) (displacement >> 8)};
                    }
                    break;
                case 4:
                    displacement = code.read32();
                    if (sib == null) {
                        bytes = new byte[]{modrm.getModRM(), (byte) displacement, (byte) (displacement >> 8), (byte) (displacement >> 16), (byte) (displacement >> 24)};
                    } else {
                        bytes = new byte[]{modrm.getModRM(), sib.getSIB(), (byte) displacement, (byte) (displacement >> 8), (byte) (displacement >> 16), (byte) (displacement >> 24)};
                    }
                    break;
                default:
                    displacement = 0;
                    if (sib == null) {
                        bytes = new byte[]{modrm.getModRM()};
                    } else {
                        bytes = new byte[]{modrm.getModRM(), sib.getSIB()};
                    }
                    break;
            }

        }

        public OperandDecoder getOperandDecoder() {
            return new OperandDecoder(modrm, sib, displacement);
        }

        public byte[] getOp(byte[] prefix) {
            byte[] result = new byte[prefix.length + bytes.length];
            System.arraycopy(prefix, 0, result, 0, prefix.length);
            System.arraycopy(bytes, 0, result, prefix.length, bytes.length);
            return result;
        }
    }
}
