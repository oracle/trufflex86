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

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.RegisterAccessFactory;
import org.graalvm.vm.x86.isa.Flags;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.VirtualFrame;

public class ReadFlagsNode extends ReadNode {
    @Child private ReadFlagNode readCF;
    @Child private ReadFlagNode readPF;
    @Child private ReadFlagNode readAF;
    @Child private ReadFlagNode readZF;
    @Child private ReadFlagNode readSF;
    @Child private ReadFlagNode readDF;
    @Child private ReadFlagNode readOF;
    @Child private ReadFlagNode readAC;
    @Child private ReadFlagNode readID;

    private static final long RESERVED = bit(1, true) | bit(Flags.IF, true);

    @CompilationFinal private boolean initialized = false;
    private final Object lock = new Object();

    private static long bit(long shift, boolean value) {
        return value ? (1L << shift) : 0;
    }

    private void createChildrenIfNecessary() {
        if (!initialized) {
            synchronized (lock) {
                if (!initialized) {
                    CompilerDirectives.transferToInterpreterAndInvalidate();
                    ArchitecturalState state = getContextReference().get().getState();
                    RegisterAccessFactory regs = state.getRegisters();
                    readCF = regs.getCF().createRead();
                    readPF = regs.getPF().createRead();
                    readAF = regs.getAF().createRead();
                    readZF = regs.getZF().createRead();
                    readSF = regs.getSF().createRead();
                    readDF = regs.getDF().createRead();
                    readOF = regs.getOF().createRead();
                    readAC = regs.getAC().createRead();
                    readID = regs.getID().createRead();
                    initialized = true;
                }
            }
        }
    }

    private long getRFLAGS(VirtualFrame frame) {
        createChildrenIfNecessary();
        boolean cf = readCF.execute(frame);
        boolean pf = readPF.execute(frame);
        boolean af = readAF.execute(frame);
        boolean zf = readZF.execute(frame);
        boolean sf = readSF.execute(frame);
        boolean df = readDF.execute(frame);
        boolean of = readOF.execute(frame);
        boolean ac = readAC.execute(frame);
        boolean id = readID.execute(frame);
        return bit(Flags.CF, cf) | bit(Flags.PF, pf) | bit(Flags.AF, af) | bit(Flags.ZF, zf) | bit(Flags.SF, sf) | bit(Flags.DF, df) | bit(Flags.OF, of) | bit(Flags.AC, ac) | bit(Flags.ID, id) |
                        RESERVED;
    }

    @Override
    public byte executeI8(VirtualFrame frame) {
        return (byte) getRFLAGS(frame);
    }

    @Override
    public short executeI16(VirtualFrame frame) {
        return (short) getRFLAGS(frame);
    }

    @Override
    public int executeI32(VirtualFrame frame) {
        return (int) getRFLAGS(frame);
    }

    @Override
    public long executeI64(VirtualFrame frame) {
        return getRFLAGS(frame);
    }
}
