package org.graalvm.vm.x86.isa;

import java.util.Arrays;

import org.graalvm.vm.x86.isa.instruction.Syscall;
import org.graalvm.vm.x86.isa.instruction.Call.CallRelative;
import org.graalvm.vm.x86.isa.instruction.Dec.Decl;
import org.graalvm.vm.x86.isa.instruction.Dec.Decq;
import org.graalvm.vm.x86.isa.instruction.Dec.Decw;
import org.graalvm.vm.x86.isa.instruction.Inc.Incl;
import org.graalvm.vm.x86.isa.instruction.Inc.Incq;
import org.graalvm.vm.x86.isa.instruction.Inc.Incw;
import org.graalvm.vm.x86.isa.instruction.Jcc.Ja;
import org.graalvm.vm.x86.isa.instruction.Jcc.Jae;
import org.graalvm.vm.x86.isa.instruction.Jcc.Jb;
import org.graalvm.vm.x86.isa.instruction.Jcc.Jbe;
import org.graalvm.vm.x86.isa.instruction.Jcc.Jrcxz;
import org.graalvm.vm.x86.isa.instruction.Jcc.Je;
import org.graalvm.vm.x86.isa.instruction.Jcc.Jg;
import org.graalvm.vm.x86.isa.instruction.Jcc.Jge;
import org.graalvm.vm.x86.isa.instruction.Jcc.Jl;
import org.graalvm.vm.x86.isa.instruction.Jcc.Jle;
import org.graalvm.vm.x86.isa.instruction.Jcc.Jne;
import org.graalvm.vm.x86.isa.instruction.Jcc.Jno;
import org.graalvm.vm.x86.isa.instruction.Jcc.Jnp;
import org.graalvm.vm.x86.isa.instruction.Jcc.Jns;
import org.graalvm.vm.x86.isa.instruction.Jcc.Jo;
import org.graalvm.vm.x86.isa.instruction.Jcc.Jp;
import org.graalvm.vm.x86.isa.instruction.Jcc.Js;
import org.graalvm.vm.x86.isa.instruction.Lods.Lodsb;
import org.graalvm.vm.x86.isa.instruction.Mov.Movl;
import org.graalvm.vm.x86.isa.instruction.Mov.Movq;
import org.graalvm.vm.x86.isa.instruction.Mov.Movw;
import org.graalvm.vm.x86.isa.instruction.Ret;
import org.graalvm.vm.x86.isa.instruction.Sub.Subl;
import org.graalvm.vm.x86.isa.instruction.Sub.Subq;
import org.graalvm.vm.x86.isa.instruction.Sub.Subw;
import org.graalvm.vm.x86.isa.instruction.Test.Testb;
import org.graalvm.vm.x86.isa.instruction.Xor.Xorb;
import org.graalvm.vm.x86.isa.instruction.Xor.Xorw;
import org.graalvm.vm.x86.isa.instruction.Xor.Xorl;

public class AMD64InstructionDecoder {
    private static final Register[] REG16 = {Register.AX, Register.CX, Register.DX, Register.BX, Register.SP, Register.BP, Register.SI, Register.DI};
    private static final Register[] REG32 = {Register.EAX, Register.ECX, Register.EDX, Register.EBX, Register.ESP, Register.EBP, Register.ESI, Register.EDI};

    public static AMD64Instruction decode(long pc, CodeReader code) {
        byte[] instruction = new byte[16];
        int instructionLength = 0;
        byte op = code.read8();
        instruction[instructionLength++] = op;
        boolean sizeOverride = false;
        AMD64RexPrefix rex = null;
        switch (op) {
            case AMD64InstructionPrefix.OPERAND_SIZE_OVERRIDE:
                sizeOverride = true;
                op = code.read8();
                instruction[instructionLength++] = op;
                break;
        }
        if (AMD64RexPrefix.isREX(op)) {
            rex = new AMD64RexPrefix(op);
            op = code.read8();
            instruction[instructionLength++] = op;
        }
        switch (op) {
            case AMD64Opcode.CALL_REL: {
                int rel32 = code.read32();
                instruction[instructionLength++] = (byte) rel32;
                instruction[instructionLength++] = (byte) (rel32 >> 8);
                instruction[instructionLength++] = (byte) (rel32 >> 16);
                instruction[instructionLength++] = (byte) (rel32 >> 24);
                return new CallRelative(pc, Arrays.copyOf(instruction, instructionLength), new ImmediateOperand(rel32));
            }
            case AMD64Opcode.INC_RM: { // or: DEC_RM
                Args args = new Args(code);
                switch (args.modrm.getReg()) {
                    case 0: // INC
                        if (rex != null) {
                            assert !rex.r && !rex.b && !rex.x;
                            if (rex.w) {
                                return new Incq(pc, args.getOp(instruction, instructionLength), args.getOperandDecoder());
                            } else {
                                throw new AssertionError("rex + mov rm/r: not implemented");
                            }
                        }
                        if (sizeOverride) {
                            return new Incw(pc, args.getOp(instruction, instructionLength), args.getOperandDecoder());
                        } else {
                            return new Incl(pc, args.getOp(instruction, instructionLength), args.getOperandDecoder());
                        }
                    case 1: // DEC
                        if (rex != null) {
                            assert !rex.r && !rex.b && !rex.x;
                            if (rex.w) {
                                return new Decq(pc, args.getOp(instruction, instructionLength), args.getOperandDecoder());
                            } else {
                                throw new AssertionError("rex + mov rm/r: not implemented");
                            }
                        }
                        if (sizeOverride) {
                            return new Decw(pc, args.getOp(instruction, instructionLength), args.getOperandDecoder());
                        } else {
                            return new Decl(pc, args.getOp(instruction, instructionLength), args.getOperandDecoder());
                        }
                    default:
                        throw new AssertionError();
                }
            }
            case AMD64Opcode.JA: {
                byte rel8 = code.read8();
                instruction[instructionLength++] = rel8;
                return new Ja(pc, Arrays.copyOf(instruction, instructionLength), rel8);
            }
            case AMD64Opcode.JAE: {
                byte rel8 = code.read8();
                instruction[instructionLength++] = rel8;
                return new Jae(pc, Arrays.copyOf(instruction, instructionLength), rel8);
            }
            case AMD64Opcode.JB: {
                byte rel8 = code.read8();
                instruction[instructionLength++] = rel8;
                return new Jb(pc, Arrays.copyOf(instruction, instructionLength), rel8);
            }
            case AMD64Opcode.JBE: {
                byte rel8 = code.read8();
                instruction[instructionLength++] = rel8;
                return new Jbe(pc, Arrays.copyOf(instruction, instructionLength), rel8);
            }
            case AMD64Opcode.JRCXZ: {
                byte rel8 = code.read8();
                instruction[instructionLength++] = rel8;
                return new Jrcxz(pc, Arrays.copyOf(instruction, instructionLength), rel8);
            }
            case AMD64Opcode.JE: {
                byte rel8 = code.read8();
                instruction[instructionLength++] = rel8;
                return new Je(pc, Arrays.copyOf(instruction, instructionLength), rel8);
            }
            case AMD64Opcode.JG: {
                byte rel8 = code.read8();
                instruction[instructionLength++] = rel8;
                return new Jg(pc, Arrays.copyOf(instruction, instructionLength), rel8);
            }
            case AMD64Opcode.JGE: {
                byte rel8 = code.read8();
                instruction[instructionLength++] = rel8;
                return new Jge(pc, Arrays.copyOf(instruction, instructionLength), rel8);
            }
            case AMD64Opcode.JL: {
                byte rel8 = code.read8();
                instruction[instructionLength++] = rel8;
                return new Jl(pc, Arrays.copyOf(instruction, instructionLength), rel8);
            }
            case AMD64Opcode.JLE: {
                byte rel8 = code.read8();
                instruction[instructionLength++] = rel8;
                return new Jle(pc, Arrays.copyOf(instruction, instructionLength), rel8);
            }
            case AMD64Opcode.JNE: {
                byte rel8 = code.read8();
                instruction[instructionLength++] = rel8;
                return new Jne(pc, Arrays.copyOf(instruction, instructionLength), rel8);
            }
            case AMD64Opcode.JNO: {
                byte rel8 = code.read8();
                instruction[instructionLength++] = rel8;
                return new Jno(pc, Arrays.copyOf(instruction, instructionLength), rel8);
            }
            case AMD64Opcode.JNP: {
                byte rel8 = code.read8();
                instruction[instructionLength++] = rel8;
                return new Jnp(pc, Arrays.copyOf(instruction, instructionLength), rel8);
            }
            case AMD64Opcode.JNS: {
                byte rel8 = code.read8();
                instruction[instructionLength++] = rel8;
                return new Jns(pc, Arrays.copyOf(instruction, instructionLength), rel8);
            }
            case AMD64Opcode.JO: {
                byte rel8 = code.read8();
                instruction[instructionLength++] = rel8;
                return new Jo(pc, Arrays.copyOf(instruction, instructionLength), rel8);
            }
            case AMD64Opcode.JP: {
                byte rel8 = code.read8();
                instruction[instructionLength++] = rel8;
                return new Jp(pc, Arrays.copyOf(instruction, instructionLength), rel8);
            }
            case AMD64Opcode.JS: {
                byte rel8 = code.read8();
                instruction[instructionLength++] = rel8;
                return new Js(pc, Arrays.copyOf(instruction, instructionLength), rel8);
            }
            case AMD64Opcode.LODSB:
                return new Lodsb(pc, Arrays.copyOf(instruction, instructionLength));
            case AMD64Opcode.MOV_RM_R: {
                Args args = new Args(code);
                if (rex != null) {
                    assert !rex.r && !rex.b && !rex.x;
                    if (rex.w) {
                        return new Movq(pc, args.getOp(instruction, instructionLength), args.getOperandDecoder());
                    } else {
                        throw new AssertionError("rex + mov rm/r: not implemented");
                    }
                }
                if (sizeOverride) {
                    return new Movw(pc, args.getOp(instruction, instructionLength), args.getOperandDecoder());
                } else {
                    return new Movl(pc, args.getOp(instruction, instructionLength), args.getOperandDecoder());
                }
            }
            case AMD64Opcode.MOV_RM_I: {
                Args args = new Args(code);
                if (rex != null && rex.w) {
                    int imm = code.read32();
                    return new Movq(pc, args.getOp2(instruction, instructionLength, new byte[]{(byte) imm, (byte) (imm >> 8), (byte) (imm >> 16), (byte) (imm >> 24)}, 4), args.getOperandDecoder(),
                                    imm);
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
                    instruction[instructionLength++] = (byte) imm;
                    instruction[instructionLength++] = (byte) (imm >> 8);
                    Register reg = getRegister16(op);
                    return new Movw(pc, Arrays.copyOf(instruction, instructionLength), new RegisterOperand(reg), imm);
                } else {
                    int imm = code.read32();
                    instruction[instructionLength++] = (byte) imm;
                    instruction[instructionLength++] = (byte) (imm >> 8);
                    instruction[instructionLength++] = (byte) (imm >> 16);
                    instruction[instructionLength++] = (byte) (imm >> 24);
                    Register reg = getRegister32(op);
                    return new Movl(pc, Arrays.copyOf(instruction, instructionLength), new RegisterOperand(reg), imm);
                }
            }
            case AMD64Opcode.RET_NEAR:
                return new Ret(pc, Arrays.copyOf(instruction, instructionLength));
            case AMD64Opcode.SUB_RM_R: {
                Args args = new Args(code);
                if (rex != null && rex.w) {
                    assert !rex.r && !rex.b && !rex.x;
                    return new Subq(pc, args.getOp(instruction, instructionLength), args.getOperandDecoder());
                }
                assert rex == null;
                if (sizeOverride) {
                    return new Subw(pc, args.getOp(instruction, instructionLength), args.getOperandDecoder());
                } else {
                    return new Subl(pc, args.getOp(instruction, instructionLength), args.getOperandDecoder());
                }
            }
            case AMD64Opcode.TEST_RM_R: {
                assert rex == null;
                Args args = new Args(code);
                return new Testb(pc, args.getOp(instruction, instructionLength), args.getOperandDecoder());
            }
            case AMD64Opcode.XOR_RM_R: {
                assert rex == null;
                Args args = new Args(code);
                if (sizeOverride) {
                    return new Xorw(pc, args.getOp(instruction, instructionLength), args.getOperandDecoder());
                } else {
                    return new Xorl(pc, args.getOp(instruction, instructionLength), args.getOperandDecoder());
                }
            }
            case AMD64Opcode.XOR_RM8_R8: {
                assert rex == null;
                Args args = new Args(code);
                return new Xorb(pc, args.getOp(instruction, instructionLength), args.getOperandDecoder());
            }
            case AMD64Opcode.ESCAPE: {
                byte op2 = code.read8();
                instruction[instructionLength++] = op2;
                switch (op2) {
                    case AMD64Opcode.SYSCALL:
                        return new Syscall(pc, Arrays.copyOf(instruction, instructionLength));
                    default:
                        return new IllegalInstruction(pc, Arrays.copyOf(instruction, instructionLength));
                }
            }
            default:
                return new IllegalInstruction(pc, Arrays.copyOf(instruction, instructionLength));
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

        public byte[] getOp(byte[] prefix, int prefixLength) {
            byte[] result = new byte[prefixLength + bytes.length];
            System.arraycopy(prefix, 0, result, 0, prefixLength);
            System.arraycopy(bytes, 0, result, prefixLength, bytes.length);
            return result;
        }

        public byte[] getOp2(byte[] prefix, int prefixLength, byte[] suffix, int suffixLength) {
            byte[] result = new byte[prefixLength + bytes.length + suffixLength];
            System.arraycopy(prefix, 0, result, 0, prefixLength);
            System.arraycopy(bytes, 0, result, prefixLength, bytes.length);
            System.arraycopy(suffix, 0, result, prefixLength + bytes.length, suffixLength);
            return result;
        }
    }
}
