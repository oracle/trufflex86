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
package org.graalvm.vm.x86.node;

import java.util.logging.Logger;

import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.memory.vector.Vector256;
import org.graalvm.vm.memory.vector.Vector512;
import org.graalvm.vm.util.log.Trace;
import org.graalvm.vm.x86.isa.AVXRegister;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameUtil;
import com.oracle.truffle.api.frame.VirtualFrame;

public class AVXRegisterReadNode extends ReadNode {
    private static final Logger log = Trace.create(AVXRegisterReadNode.class);

    private static final boolean USE_XMM = true;
    private static final boolean USE_TYPE = true;

    private final FrameSlot zmm;
    private final FrameSlot xmm;
    private final FrameSlot xmmF32;
    private final FrameSlot xmmF64;
    private final FrameSlot xmmType;

    @CompilationFinal private int cachedType;

    public AVXRegisterReadNode(FrameSlot zmm, FrameSlot xmm, FrameSlot xmmF32, FrameSlot xmmF64, FrameSlot xmmType) {
        this.zmm = zmm;
        this.xmm = xmm;
        this.xmmF32 = xmmF32;
        this.xmmF64 = xmmF64;
        this.xmmType = xmmType;
    }

    // TODO: add fallback if type changes during runtime
    private int cached(int type) {
        if (type != cachedType) {
            if (CompilerDirectives.inCompiledCode()) {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                log.info("PERF WARNING: AVX register type changed! (" + type + " -> " + type + ")");
            }
            CompilerDirectives.transferToInterpreterAndInvalidate();
            cachedType = type;
            return type;
        } else {
            return cachedType;
        }
    }

    private void materialize(VirtualFrame frame, int type) {
        CompilerDirectives.transferToInterpreterAndInvalidate();
        if (type == AVXRegister.TYPE_F32) {
            Vector128 reg = (Vector128) FrameUtil.getObjectSafe(frame, xmm);
            float val = FrameUtil.getFloatSafe(frame, xmmF32);
            reg.setF32(3, val);
            frame.setInt(xmmType, AVXRegister.TYPE_XMM);
        } else if (type == AVXRegister.TYPE_F64) {
            Vector128 reg = (Vector128) FrameUtil.getObjectSafe(frame, xmm);
            double val = FrameUtil.getDoubleSafe(frame, xmmF64);
            reg.setF64(1, val);
            frame.setInt(xmmType, AVXRegister.TYPE_XMM);
        } else {
            throw new AssertionError("unknown type: " + type);
        }
    }

    @Override
    public byte executeI8(VirtualFrame frame) {
        CompilerAsserts.partialEvaluationConstant(zmm);
        if (USE_XMM) {
            if (USE_TYPE) {
                int type = cached(FrameUtil.getIntSafe(frame, xmmType));
                if (type != AVXRegister.TYPE_XMM) {
                    materialize(frame, type);
                }
            }
            Vector128 reg = (Vector128) FrameUtil.getObjectSafe(frame, xmm);
            return reg.getI8(15);
        } else {
            Vector512 reg = (Vector512) FrameUtil.getObjectSafe(frame, zmm);
            return (byte) reg.getI32(15);
        }
    }

    @Override
    public short executeI16(VirtualFrame frame) {
        CompilerAsserts.partialEvaluationConstant(zmm);
        if (USE_XMM) {
            if (USE_TYPE) {
                int type = cached(FrameUtil.getIntSafe(frame, xmmType));
                if (type != AVXRegister.TYPE_XMM) {
                    materialize(frame, type);
                }
            }
            Vector128 reg = (Vector128) FrameUtil.getObjectSafe(frame, xmm);
            return reg.getI16(7);
        } else {
            Vector512 reg = (Vector512) FrameUtil.getObjectSafe(frame, zmm);
            return (short) reg.getI32(15);
        }
    }

    @Override
    public int executeI32(VirtualFrame frame) {
        CompilerAsserts.partialEvaluationConstant(zmm);
        if (USE_XMM) {
            if (USE_TYPE) {
                int type = cached(FrameUtil.getIntSafe(frame, xmmType));
                if (type != AVXRegister.TYPE_XMM) {
                    materialize(frame, type);
                }
            }
            Vector128 reg = (Vector128) FrameUtil.getObjectSafe(frame, xmm);
            return reg.getI32(3);
        } else {
            Vector512 reg = (Vector512) FrameUtil.getObjectSafe(frame, zmm);
            return reg.getI32(15);
        }
    }

    @Override
    public float executeF32(VirtualFrame frame) {
        CompilerAsserts.partialEvaluationConstant(zmm);
        if (USE_XMM) {
            if (USE_TYPE) {
                int type = cached(FrameUtil.getIntSafe(frame, xmmType));
                if (type == AVXRegister.TYPE_F32) {
                    return FrameUtil.getFloatSafe(frame, xmmF32);
                } else if (type != AVXRegister.TYPE_XMM) {
                    materialize(frame, type);
                }
            }
            Vector128 reg = (Vector128) FrameUtil.getObjectSafe(frame, xmm);
            return reg.getF32(3);
        } else {
            Vector512 reg = (Vector512) FrameUtil.getObjectSafe(frame, zmm);
            return reg.getF32(15);
        }
    }

    @Override
    public long executeI64(VirtualFrame frame) {
        CompilerAsserts.partialEvaluationConstant(zmm);
        if (USE_XMM) {
            if (USE_TYPE) {
                int type = cached(FrameUtil.getIntSafe(frame, xmmType));
                if (type != AVXRegister.TYPE_XMM) {
                    materialize(frame, type);
                }
            }
            Vector128 reg = (Vector128) FrameUtil.getObjectSafe(frame, xmm);
            return reg.getI64(1);
        } else {
            Vector512 reg = (Vector512) FrameUtil.getObjectSafe(frame, zmm);
            return reg.getI64(7);
        }
    }

    @Override
    public double executeF64(VirtualFrame frame) {
        CompilerAsserts.partialEvaluationConstant(zmm);
        if (USE_XMM) {
            if (USE_TYPE) {
                int type = cached(FrameUtil.getIntSafe(frame, xmmType));
                if (type == AVXRegister.TYPE_F64) {
                    return FrameUtil.getDoubleSafe(frame, xmmF64);
                } else if (type != AVXRegister.TYPE_XMM) {
                    materialize(frame, type);
                }
            }
            Vector128 reg = (Vector128) FrameUtil.getObjectSafe(frame, xmm);
            return reg.getF64(1);
        } else {
            Vector512 reg = (Vector512) FrameUtil.getObjectSafe(frame, zmm);
            return reg.getF64(7);
        }
    }

    @Override
    public Vector128 executeI128(VirtualFrame frame) {
        CompilerAsserts.partialEvaluationConstant(zmm);
        if (USE_XMM) {
            if (USE_TYPE) {
                int type = cached(FrameUtil.getIntSafe(frame, xmmType));
                if (type != AVXRegister.TYPE_XMM) {
                    materialize(frame, type);
                }
            }
            Vector128 reg = (Vector128) FrameUtil.getObjectSafe(frame, xmm);
            return reg.clone();
        } else {
            Vector512 reg = (Vector512) FrameUtil.getObjectSafe(frame, zmm);
            return reg.getI128(3);
        }
    }

    @Override
    public Vector256 executeI256(VirtualFrame frame) {
        CompilerAsserts.partialEvaluationConstant(zmm);
        if (USE_XMM) {
            CompilerDirectives.transferToInterpreter();
            throw new AssertionError("AVX is not supported");
        } else {
            Vector512 reg = (Vector512) FrameUtil.getObjectSafe(frame, zmm);
            return reg.getI256(1);
        }
    }

    @Override
    public Vector512 executeI512(VirtualFrame frame) {
        CompilerAsserts.partialEvaluationConstant(zmm);
        if (USE_XMM) {
            if (USE_TYPE) {
                int type = cached(FrameUtil.getIntSafe(frame, xmmType));
                if (type != AVXRegister.TYPE_XMM) {
                    materialize(frame, type);
                }
            }
            Vector128 reg = (Vector128) FrameUtil.getObjectSafe(frame, xmm);
            return new Vector512(Vector128.ZERO, Vector128.ZERO, Vector128.ZERO, reg);
        } else {
            Vector512 reg = (Vector512) FrameUtil.getObjectSafe(frame, zmm);
            return reg;
        }
    }
}
