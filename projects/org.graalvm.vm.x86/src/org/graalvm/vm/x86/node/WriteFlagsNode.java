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

import org.graalvm.vm.util.BitTest;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.RegisterAccessFactory;
import org.graalvm.vm.x86.isa.Flags;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public class WriteFlagsNode extends WriteNode {
    @Child private WriteFlagNode writeCF;
    @Child private WriteFlagNode writePF;
    @Child private WriteFlagNode writeAF;
    @Child private WriteFlagNode writeZF;
    @Child private WriteFlagNode writeSF;
    @Child private WriteFlagNode writeDF;
    @Child private WriteFlagNode writeOF;
    @Child private WriteFlagNode writeAC;
    @Child private WriteFlagNode writeID;

    private static long bit(long shift) {
        return bit(shift, true);
    }

    private static long bit(long shift, boolean value) {
        return value ? (1L << shift) : 0;
    }

    private void createChildrenIfNecessary() {
        if (writeCF == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            ArchitecturalState state = getContextReference().get().getState();
            RegisterAccessFactory regs = state.getRegisters();
            writeCF = regs.getCF().createWrite();
            writePF = regs.getPF().createWrite();
            writeAF = regs.getAF().createWrite();
            writeZF = regs.getZF().createWrite();
            writeSF = regs.getSF().createWrite();
            writeDF = regs.getDF().createWrite();
            writeOF = regs.getOF().createWrite();
            writeAC = regs.getAC().createWrite();
            writeID = regs.getID().createWrite();
        }
    }

    @Override
    public void executeI8(VirtualFrame frame, byte value) {
        createChildrenIfNecessary();
        boolean cf = BitTest.test(value, bit(Flags.CF));
        boolean pf = BitTest.test(value, bit(Flags.PF));
        boolean af = BitTest.test(value, bit(Flags.AF));
        boolean zf = BitTest.test(value, bit(Flags.ZF));
        boolean sf = BitTest.test(value, bit(Flags.SF));
        writeCF.execute(frame, cf);
        writePF.execute(frame, pf);
        writeAF.execute(frame, af);
        writeZF.execute(frame, zf);
        writeSF.execute(frame, sf);
    }

    @Override
    public void executeI16(VirtualFrame frame, short value) {
        createChildrenIfNecessary();
        boolean cf = BitTest.test(value, bit(Flags.CF));
        boolean pf = BitTest.test(value, bit(Flags.PF));
        boolean af = BitTest.test(value, bit(Flags.AF));
        boolean zf = BitTest.test(value, bit(Flags.ZF));
        boolean sf = BitTest.test(value, bit(Flags.SF));
        boolean df = BitTest.test(value, bit(Flags.DF));
        boolean of = BitTest.test(value, bit(Flags.OF));
        writeCF.execute(frame, cf);
        writePF.execute(frame, pf);
        writeAF.execute(frame, af);
        writeZF.execute(frame, zf);
        writeSF.execute(frame, sf);
        writeDF.execute(frame, df);
        writeOF.execute(frame, of);
    }

    @Override
    public void executeI32(VirtualFrame frame, int value) {
        executeI16(frame, (short) value);

        boolean ac = BitTest.test(value, bit(Flags.AC));
        boolean id = BitTest.test(value, bit(Flags.ID));
        writeAC.execute(frame, ac);
        writeID.execute(frame, id);
    }

    @Override
    public void executeI64(VirtualFrame frame, long value) {
        executeI32(frame, (int) value);
    }
}
