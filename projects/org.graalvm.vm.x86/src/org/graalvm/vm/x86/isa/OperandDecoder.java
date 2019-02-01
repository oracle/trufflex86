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

public class OperandDecoder {
    public static final int R8 = 0;
    public static final int R16 = 1;
    public static final int R32 = 2;
    public static final int R64 = 3;

    private final ModRM modrm;
    private final SIB sib;
    private final long displacement;
    private final AMD64RexPrefix rex;
    private final SegmentRegister segment;
    private final boolean addressOverride;

    public OperandDecoder(ModRM modrm, SIB sib, long displacement, AMD64RexPrefix rex, SegmentRegister segment, boolean addressOverride) {
        this.modrm = modrm;
        this.sib = sib;
        this.displacement = displacement;
        this.rex = rex;
        this.segment = segment;
        this.addressOverride = addressOverride;
    }

    public Operand getOperand1(int type) {
        if (rex != null) {
            // TODO!
            if (modrm.hasSIB()) {
                boolean hasDisplacement = modrm.hasDisplacement();
                if (modrm.hasSIB() && sib.base == 0b101) {
                    switch (modrm.getMod()) {
                        case 0b00:
                        case 0b10:
                        case 0b01:
                            hasDisplacement = true;
                    }
                }
                if (hasDisplacement) {
                    if (sib.index == 0b100 && !rex.x) { // rsp not used
                        if (modrm.getMod() == 0) { // base not used
                            return new MemoryOperand(segment, displacement, addressOverride);
                        } else {
                            return new MemoryOperand(segment, sib.getBase(rex.b), displacement, addressOverride);
                        }
                    } else if (sib.base == 0b101) {
                        switch (modrm.getMod()) {
                            case 0b00:
                                return new MemoryOperand(segment, null, sib.getIndex(rex.x), sib.getShift(), displacement, addressOverride);
                            case 0b01:
                            case 0b10:
                                return new MemoryOperand(segment, sib.getBase(rex.b), sib.getIndex(rex.x), sib.getShift(), displacement, addressOverride);
                            default:
                                throw new AssertionError("this should not have a SIB/displacement!");
                        }
                    } else {
                        if (sib.index == 0b100 && !rex.x) { // no index
                            return new MemoryOperand(segment, sib.getBase(rex.b), displacement, addressOverride);
                        } else {
                            return new MemoryOperand(segment, sib.getBase(rex.b), sib.getIndex(rex.x), sib.getShift(), displacement, addressOverride);
                        }
                    }
                } else if (modrm.getMod() == 0 && modrm.getRM() == 0b101) { // base not used
                    return new MemoryOperand(segment, sib.getIndex(rex.x), sib.getShift(), addressOverride);
                } else if (sib.index == 0b100 && !rex.x) { // index not used
                    return new MemoryOperand(segment, sib.getBase(rex.b), addressOverride);
                } else if (modrm.getMod() == 0 && sib.base == 0b101 && !rex.b) { // base not used
                    return new MemoryOperand(segment, null, sib.getIndex(rex.x), sib.getShift(), addressOverride);
                } else {
                    return new MemoryOperand(segment, sib.getBase(rex.b), sib.getIndex(rex.x), sib.getShift(), addressOverride);
                }
            } else if (modrm.hasDisplacement()) {
                RegisterOperand op = (RegisterOperand) modrm.getOperand1(ModRM.A64, ModRM.R64);
                Register reg = Register.RIP;
                if (op != null) {
                    reg = op.getRegister();
                    reg = getRegister(reg, rex.b);
                }
                return new MemoryOperand(segment, reg, displacement, addressOverride);
            } else {
                Operand op = modrm.getOperand1REX(ModRM.A64, type);
                if (op instanceof RegisterOperand) {
                    if (type == R8 && modrm.getMod() == 0b11) {
                        int id = modrm.getRM();
                        Register reg = Register.get(id + (rex.b ? 8 : 0)).getSize(1);
                        return new RegisterOperand(reg);
                    }
                    Register reg = ((RegisterOperand) op).getRegister();
                    return new RegisterOperand(getRegister(reg, rex.b));
                } else if (op instanceof MemoryOperand) {
                    MemoryOperand mem = (MemoryOperand) op;
                    Register base = mem.getBase();
                    assert mem.getIndex() == null;
                    assert mem.getDisplacement() == 0;
                    return new MemoryOperand(segment, getRegister(base, rex.b), addressOverride);
                } else {
                    return seg(op);
                }
            }
        }
        if (modrm.hasSIB()) {
            boolean hasDisplacement = modrm.hasDisplacement();
            if (modrm.hasSIB() && sib.base == 0b101) {
                switch (modrm.getMod()) {
                    case 0b00:
                    case 0b10:
                    case 0b01:
                        hasDisplacement = true;
                }
            }
            if (hasDisplacement) {
                if (sib.base == 0b101 && modrm.getMod() != 0) {
                    return new MemoryOperand(segment, Register.RBP, sib.getIndex(), sib.getShift(), displacement, addressOverride);
                } else {
                    return new MemoryOperand(segment, sib.getBase(), sib.getIndex(), sib.getShift(), displacement, addressOverride);
                }
            } else {
                return new MemoryOperand(segment, sib.getBase(), sib.getIndex(), sib.getShift(), addressOverride);
            }
        } else {
            if (modrm.hasDisplacement()) {
                RegisterOperand op = (RegisterOperand) modrm.getOperand1(ModRM.A64, type);
                Register reg = Register.RIP;
                if (op != null) {
                    reg = op.getRegister();
                }
                return new MemoryOperand(segment, reg, displacement, addressOverride);
            } else {
                return seg(modrm.getOperand1(ModRM.A64, type));
            }
        }
    }

    private Operand seg(Operand op) {
        if (op instanceof MemoryOperand) {
            return ((MemoryOperand) op).getInSegment(segment);
        } else {
            return op;
        }
    }

    private static Register getRegister(Register reg, boolean r) {
        if (r) {
            return Register.get(reg.getID() + 8).getSize(reg.getSize());
        } else {
            return reg;
        }
    }

    public Operand getOperand2(int type) {
        if (rex != null) {
            Register reg = modrm.getOperand2REX(type);
            return new RegisterOperand(getRegister(reg, rex.r));
        } else {
            return new RegisterOperand(modrm.getOperand2(type));
        }
    }

    public Operand getAVXOperand1(int size) {
        Operand op = getOperand1(R64);
        if (op instanceof RegisterOperand) {
            return new AVXRegisterOperand(((RegisterOperand) op).getRegister().getID(), size);
        } else {
            return op;
        }
    }

    public Operand getAVXOperand2(int size) {
        Operand op = getOperand2(R64);
        if (op instanceof RegisterOperand) {
            return new AVXRegisterOperand(((RegisterOperand) op).getRegister().getID(), size);
        } else {
            return op;
        }
    }
}
