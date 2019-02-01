/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.graalvm.vm.x86.isa;

public enum Register {
    AL(0),
    AH(0),
    CL(1),
    CH(1),
    DL(2),
    DH(2),
    BL(3),
    BH(3),
    SPL(4),
    BPL(5),
    SIL(6),
    DIL(7),
    AX(0),
    CX(1),
    DX(2),
    BX(3),
    SP(4),
    BP(5),
    SI(6),
    DI(7),
    EAX(0),
    ECX(1),
    EDX(2),
    EBX(3),
    ESP(4),
    EBP(5),
    ESI(6),
    EDI(7),
    RAX(0),
    RCX(1),
    RDX(2),
    RBX(3),
    RSP(4),
    RBP(5),
    RSI(6),
    RDI(7),
    R8(8),
    R9(9),
    R10(10),
    R11(11),
    R12(12),
    R13(13),
    R14(14),
    R15(15),
    R8D(8),
    R9D(9),
    R10D(10),
    R11D(11),
    R12D(12),
    R13D(13),
    R14D(14),
    R15D(15),
    R8W(8),
    R9W(9),
    R10W(10),
    R11W(11),
    R12W(12),
    R13W(13),
    R14W(14),
    R15W(15),
    R8B(8),
    R9B(9),
    R10B(10),
    R11B(11),
    R12B(12),
    R13B(13),
    R14B(14),
    R15B(15),
    IP(31),
    EIP(31),
    RIP(31);

    private Register(int id) {
        this.id = id;
    }

    private final int id;

    public static Register[] REGISTERS = {
                    RAX, RCX, RDX, RBX, RSP, RBP, RSI, RDI,
                    R8, R9, R10, R11, R12, R13, R14, R15
    };

    static {
        for (int i = 0; i < REGISTERS.length; i++) {
            assert REGISTERS[i].getID() == i;
        }
    }

    public int getID() {
        return id;
    }

    public Register get32bit() {
        return getSize(4);
    }

    public Register get64bit() {
        return getSize(8);
    }

    public Register getRegister() {
        switch (this) {
            case AL:
            case AH:
            case AX:
            case EAX:
            case RAX:
                return RAX;
            case CL:
            case CH:
            case CX:
            case ECX:
            case RCX:
                return RCX;
            case DL:
            case DH:
            case DX:
            case EDX:
            case RDX:
                return RDX;
            case BL:
            case BH:
            case BX:
            case EBX:
            case RBX:
                return RBX;
            case SPL:
            case SP:
            case ESP:
            case RSP:
                return RSP;
            case BPL:
            case BP:
            case EBP:
            case RBP:
                return RBP;
            case SIL:
            case SI:
            case ESI:
            case RSI:
                return RSI;
            case DIL:
            case DI:
            case EDI:
            case RDI:
                return RDI;
            case R8:
            case R8D:
            case R8W:
            case R8B:
                return R8;
            case R9:
            case R9D:
            case R9W:
            case R9B:
                return R9;
            case R10:
            case R10D:
            case R10W:
            case R10B:
                return R10;
            case R11:
            case R11D:
            case R11W:
            case R11B:
                return R11;
            case R12:
            case R12D:
            case R12W:
            case R12B:
                return R12;
            case R13:
            case R13D:
            case R13W:
            case R13B:
                return R13;
            case R14:
            case R14D:
            case R14W:
            case R14B:
                return R14;
            case R15:
            case R15D:
            case R15W:
            case R15B:
                return R15;
            case IP:
            case EIP:
            case RIP:
                return RIP;
        }
        throw new AssertionError();
    }

    public static Register get(int id) {
        return REGISTERS[id];
    }

    public int getSize() {
        switch (this) {
            case AH:
            case AL:
            case BH:
            case BL:
            case CH:
            case CL:
            case DH:
            case DL:
            case SIL:
            case DIL:
            case BPL:
            case SPL:
            case R8B:
            case R9B:
            case R10B:
            case R11B:
            case R12B:
            case R13B:
            case R14B:
            case R15B:
                return 1;
            case AX:
            case BX:
            case CX:
            case DX:
            case SI:
            case DI:
            case BP:
            case SP:
            case R8W:
            case R9W:
            case R10W:
            case R11W:
            case R12W:
            case R13W:
            case R14W:
            case R15W:
                return 2;
            case EAX:
            case EBX:
            case ECX:
            case EDX:
            case ESI:
            case EDI:
            case EBP:
            case ESP:
            case R8D:
            case R9D:
            case R10D:
            case R11D:
            case R12D:
            case R13D:
            case R14D:
            case R15D:
                return 4;
            case RAX:
            case RBX:
            case RCX:
            case RDX:
            case RSI:
            case RDI:
            case RBP:
            case RSP:
            case R8:
            case R9:
            case R10:
            case R11:
            case R12:
            case R13:
            case R14:
            case R15:
            case RIP:
                return 8;
            default:
                // unreachable
                throw new AssertionError();
        }
    }

    public Register getSize(int size) {
        switch (this) {
            case AL:
            case AX:
            case EAX:
            case RAX:
                switch (size) {
                    case 1:
                        return AL;
                    case 2:
                        return AX;
                    case 4:
                        return EAX;
                    case 8:
                        return RAX;
                    default:
                        throw new IllegalArgumentException();
                }
            case BL:
            case BX:
            case EBX:
            case RBX:
                switch (size) {
                    case 1:
                        return BL;
                    case 2:
                        return BX;
                    case 4:
                        return EBX;
                    case 8:
                        return RBX;
                    default:
                        throw new IllegalArgumentException();
                }
            case CL:
            case CX:
            case ECX:
            case RCX:
                switch (size) {
                    case 1:
                        return CL;
                    case 2:
                        return CX;
                    case 4:
                        return ECX;
                    case 8:
                        return RCX;
                    default:
                        throw new IllegalArgumentException();
                }
            case DL:
            case DX:
            case EDX:
            case RDX:
                switch (size) {
                    case 1:
                        return DL;
                    case 2:
                        return DX;
                    case 4:
                        return EDX;
                    case 8:
                        return RDX;
                    default:
                        throw new IllegalArgumentException();
                }
            case SIL:
            case SI:
            case ESI:
            case RSI:
                switch (size) {
                    case 1:
                        return SIL;
                    case 2:
                        return SI;
                    case 4:
                        return ESI;
                    case 8:
                        return RSI;
                    default:
                        throw new IllegalArgumentException();
                }
            case DIL:
            case DI:
            case EDI:
            case RDI:
                switch (size) {
                    case 1:
                        return DIL;
                    case 2:
                        return DI;
                    case 4:
                        return EDI;
                    case 8:
                        return RDI;
                    default:
                        throw new IllegalArgumentException();
                }
            case BPL:
            case BP:
            case EBP:
            case RBP:
                switch (size) {
                    case 1:
                        return BPL;
                    case 2:
                        return BP;
                    case 4:
                        return EBP;
                    case 8:
                        return RBP;
                    default:
                        throw new IllegalArgumentException();
                }
            case SPL:
            case SP:
            case ESP:
            case RSP:
                switch (size) {
                    case 1:
                        return SPL;
                    case 2:
                        return SP;
                    case 4:
                        return ESP;
                    case 8:
                        return RSP;
                    default:
                        throw new IllegalArgumentException();
                }
            case R8B:
            case R8W:
            case R8D:
            case R8:
                switch (size) {
                    case 1:
                        return R8B;
                    case 2:
                        return R8W;
                    case 4:
                        return R8D;
                    case 8:
                        return R8;
                    default:
                        throw new IllegalArgumentException();
                }
            case R9B:
            case R9W:
            case R9D:
            case R9:
                switch (size) {
                    case 1:
                        return R9B;
                    case 2:
                        return R9W;
                    case 4:
                        return R9D;
                    case 8:
                        return R9;
                    default:
                        throw new IllegalArgumentException();
                }
            case R10B:
            case R10W:
            case R10D:
            case R10:
                switch (size) {
                    case 1:
                        return R10B;
                    case 2:
                        return R10W;
                    case 4:
                        return R10D;
                    case 8:
                        return R10;
                    default:
                        throw new IllegalArgumentException();
                }
            case R11B:
            case R11W:
            case R11D:
            case R11:
                switch (size) {
                    case 1:
                        return R11B;
                    case 2:
                        return R11W;
                    case 4:
                        return R11D;
                    case 8:
                        return R11;
                    default:
                        throw new IllegalArgumentException();
                }
            case R12B:
            case R12W:
            case R12D:
            case R12:
                switch (size) {
                    case 1:
                        return R12B;
                    case 2:
                        return R12W;
                    case 4:
                        return R12D;
                    case 8:
                        return R12;
                    default:
                        throw new IllegalArgumentException();
                }
            case R13B:
            case R13W:
            case R13D:
            case R13:
                switch (size) {
                    case 1:
                        return R13B;
                    case 2:
                        return R13W;
                    case 4:
                        return R13D;
                    case 8:
                        return R13;
                    default:
                        throw new IllegalArgumentException();
                }
            case R14B:
            case R14W:
            case R14D:
            case R14:
                switch (size) {
                    case 1:
                        return R14B;
                    case 2:
                        return R14W;
                    case 4:
                        return R14D;
                    case 8:
                        return R14;
                    default:
                        throw new IllegalArgumentException();
                }
            case R15B:
            case R15W:
            case R15D:
            case R15:
                switch (size) {
                    case 1:
                        return R15B;
                    case 2:
                        return R15W;
                    case 4:
                        return R15D;
                    case 8:
                        return R15;
                    default:
                        throw new IllegalArgumentException();
                }
            case RIP:
                switch (size) {
                    case 2:
                        return IP;
                    case 4:
                        return EIP;
                    case 8:
                        return RIP;
                    default:
                        throw new IllegalArgumentException();
                }
            default:
                // unreachable
                throw new AssertionError("Register type: " + this + "; size: " + size);
        }
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
