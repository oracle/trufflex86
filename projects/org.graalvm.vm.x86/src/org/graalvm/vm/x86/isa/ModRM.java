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

public class ModRM {
    public static final int A8 = 0;
    public static final int A16 = 1;
    public static final int A32 = 2;
    public static final int A64 = 3;

    public static final int R8 = 0;
    public static final int R16 = 1;
    public static final int R32 = 2;
    public static final int R64 = 3;

    private final byte modrm;

    private final int mod;
    private final int reg;
    private final int rm;

    private static final Operand[] OP1_R8_REX = {
                    new RegisterOperand(Register.AL),
                    new RegisterOperand(Register.CL),
                    new RegisterOperand(Register.DL),
                    new RegisterOperand(Register.BL),
                    new RegisterOperand(Register.SPL),
                    new RegisterOperand(Register.BPL),
                    new RegisterOperand(Register.SIL),
                    new RegisterOperand(Register.DIL)
    };

    private static final Operand[][] OP1_R = {
                    // R8
                    {
                                    new RegisterOperand(Register.AL),
                                    new RegisterOperand(Register.CL),
                                    new RegisterOperand(Register.DL),
                                    new RegisterOperand(Register.BL),
                                    new RegisterOperand(Register.AH),
                                    new RegisterOperand(Register.CH),
                                    new RegisterOperand(Register.DH),
                                    new RegisterOperand(Register.BH)
                    },
                    // R16
                    {
                                    new RegisterOperand(Register.AX),
                                    new RegisterOperand(Register.CX),
                                    new RegisterOperand(Register.DX),
                                    new RegisterOperand(Register.BX),
                                    new RegisterOperand(Register.SP),
                                    new RegisterOperand(Register.BP),
                                    new RegisterOperand(Register.SI),
                                    new RegisterOperand(Register.DI)
                    },
                    // R32
                    {
                                    new RegisterOperand(Register.EAX),
                                    new RegisterOperand(Register.ECX),
                                    new RegisterOperand(Register.EDX),
                                    new RegisterOperand(Register.EBX),
                                    new RegisterOperand(Register.ESP),
                                    new RegisterOperand(Register.EBP),
                                    new RegisterOperand(Register.ESI),
                                    new RegisterOperand(Register.EDI)
                    },
                    // R64
                    {
                                    new RegisterOperand(Register.RAX),
                                    new RegisterOperand(Register.RCX),
                                    new RegisterOperand(Register.RDX),
                                    new RegisterOperand(Register.RBX),
                                    new RegisterOperand(Register.RSP),
                                    new RegisterOperand(Register.RBP),
                                    new RegisterOperand(Register.RSI),
                                    new RegisterOperand(Register.RDI)
                    }
    };

    private static final Operand[][][] OP1 = {
                    // R8
                    {},
                    // R16
                    {},
                    // R32
                    {
                                    // mod=0
                                    {
                                                    new MemoryOperand(null, Register.EAX),
                                                    new MemoryOperand(null, Register.ECX),
                                                    new MemoryOperand(null, Register.EDX),
                                                    new MemoryOperand(null, Register.EBX),
                                                    null, // SIB
                                                    null, // disp32
                                                    new MemoryOperand(null, Register.ESI),
                                                    new MemoryOperand(null, Register.EDI)
                                    },
                                    // mod=1
                                    {
                                                    new RegisterOperand(Register.EAX),
                                                    new RegisterOperand(Register.ECX),
                                                    new RegisterOperand(Register.EDX),
                                                    new RegisterOperand(Register.EBX),
                                                    null,
                                                    new RegisterOperand(Register.EBP),
                                                    new RegisterOperand(Register.ESI),
                                                    new RegisterOperand(Register.EDI),
                                    },
                                    // mod=2
                                    {
                                                    new RegisterOperand(Register.EAX),
                                                    new RegisterOperand(Register.ECX),
                                                    new RegisterOperand(Register.EDX),
                                                    new RegisterOperand(Register.EBX),
                                                    null,
                                                    new RegisterOperand(Register.EBP),
                                                    new RegisterOperand(Register.ESI),
                                                    new RegisterOperand(Register.EDI),
                                    },
                                    // mod=3
                                    {
                                                    new RegisterOperand(Register.EAX),
                                                    new RegisterOperand(Register.ECX),
                                                    new RegisterOperand(Register.EDX),
                                                    new RegisterOperand(Register.EBX),
                                                    new RegisterOperand(Register.ESP),
                                                    new RegisterOperand(Register.EBP),
                                                    new RegisterOperand(Register.ESI),
                                                    new RegisterOperand(Register.EDI),
                                    }
                    },
                    // R64
                    {
                                    // mod=0
                                    {
                                                    new MemoryOperand(null, Register.RAX),
                                                    new MemoryOperand(null, Register.RCX),
                                                    new MemoryOperand(null, Register.RDX),
                                                    new MemoryOperand(null, Register.RBX),
                                                    null, // SIB
                                                    null, // RIP+disp32
                                                    new MemoryOperand(null, Register.RSI),
                                                    new MemoryOperand(null, Register.RDI)
                                    },
                                    // mod=1
                                    {
                                                    new RegisterOperand(Register.RAX),
                                                    new RegisterOperand(Register.RCX),
                                                    new RegisterOperand(Register.RDX),
                                                    new RegisterOperand(Register.RBX),
                                                    null,
                                                    new RegisterOperand(Register.RBP),
                                                    new RegisterOperand(Register.RSI),
                                                    new RegisterOperand(Register.RDI),
                                    },
                                    // mod=2
                                    {
                                                    new RegisterOperand(Register.RAX),
                                                    new RegisterOperand(Register.RCX),
                                                    new RegisterOperand(Register.RDX),
                                                    new RegisterOperand(Register.RBX),
                                                    null,
                                                    new RegisterOperand(Register.RBP),
                                                    new RegisterOperand(Register.RSI),
                                                    new RegisterOperand(Register.RDI),
                                    },
                                    // mod=3
                                    {
                                                    new RegisterOperand(Register.RAX),
                                                    new RegisterOperand(Register.RCX),
                                                    new RegisterOperand(Register.RDX),
                                                    new RegisterOperand(Register.RBX),
                                                    new RegisterOperand(Register.RSP),
                                                    new RegisterOperand(Register.RBP),
                                                    new RegisterOperand(Register.RSI),
                                                    new RegisterOperand(Register.RDI),
                                    }
                    }
    };

    private static final Register[][] OP2 = {
                    // R8
                    {Register.AL, Register.CL, Register.DL, Register.BL, Register.AH, Register.CH, Register.DH, Register.BH},
                    // R16
                    {Register.AX, Register.CX, Register.DX, Register.BX, Register.SP, Register.BP, Register.SI, Register.DI},
                    // R32
                    {Register.EAX, Register.ECX, Register.EDX, Register.EBX, Register.ESP, Register.EBP, Register.ESI, Register.EDI},
                    // R64
                    {Register.RAX, Register.RCX, Register.RDX, Register.RBX, Register.RSP, Register.RBP, Register.RSI, Register.RDI}
    };

    private static final Register[][] OP2REX = {
                    // R8
                    {Register.AL, Register.CL, Register.DL, Register.BL, Register.SPL, Register.BPL, Register.SIL, Register.DIL},
                    // R16
                    {Register.AX, Register.CX, Register.DX, Register.BX, Register.SP, Register.BP, Register.SI, Register.DI},
                    // R32
                    {Register.EAX, Register.ECX, Register.EDX, Register.EBX, Register.ESP, Register.EBP, Register.ESI, Register.EDI},
                    // R64
                    {Register.RAX, Register.RCX, Register.RDX, Register.RBX, Register.RSP, Register.RBP, Register.RSI, Register.RDI}
    };

    public ModRM(byte modrm) {
        this.modrm = modrm;
        mod = (modrm >> 6) & 0x03;
        reg = (modrm >> 3) & 0x07;
        rm = modrm & 0x07;
    }

    public byte getModRM() {
        return modrm;
    }

    public int getMod() {
        return mod;
    }

    public int getRM() {
        return rm;
    }

    public int getReg() {
        return reg;
    }

    public Operand getOperand1REX(int type, int size) {
        if (mod == 0b11 && size == R8) {
            return OP1_R8_REX[rm];
        } else {
            return getOperand1(type, size);
        }
    }

    public Operand getOperand1(int type, int size) {
        if (mod == 0b11) {
            return OP1_R[size][rm];
        }
        switch (type) {
            case A8:
            case A16:
            case A32:
            case A64:
                return OP1[type][mod][rm];
            default:
                throw new IllegalArgumentException();
        }
    }

    public Register getOperand2(int type) {
        return OP2[type][reg];
    }

    public Register getOperand2REX(int type) {
        return OP2REX[type][reg];
    }

    public boolean hasSIB() {
        return mod != 0b11 && rm == 0b100;
    }

    public int getDisplacementSize() {
        switch (mod) {
            case 0:
                return rm == 0b101 ? 4 : 0;
            case 1:
                return 1;
            case 2:
                return 4;
            case 3:
            default:
                return 0;
        }
    }

    public boolean hasDisplacement() {
        return getDisplacementSize() > 0;
    }

    @Override
    public String toString() {
        return "ModRM[mod=" + mod + ",reg=" + reg + ",rm=" + rm + "]";
    }
}
