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
import org.graalvm.vm.x86.isa.MemoryOperand;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.isa.SegmentRegister;

import com.oracle.truffle.api.frame.VirtualFrame;

public class AddressComputationNode extends AMD64Node {
    private final long displacement;
    private final long scale;
    private final Register baseReg;
    private final Register indexReg;
    private final SegmentRegister segment;
    private final boolean addressOverride;

    @Child private ReadNode base;
    @Child private ReadNode index;
    @Child private ReadNode segmentBase;

    public AddressComputationNode(ArchitecturalState state, MemoryOperand operand, long pc) {
        displacement = operand.getDisplacement();
        scale = operand.getScale();
        baseReg = operand.getBase();
        indexReg = operand.getIndex();
        segment = operand.getSegment();
        addressOverride = operand.isAddressOverride();

        assert scale >= 0 && scale <= 3;

        if (baseReg != null) {
            if (baseReg == Register.RIP) {
                base = new ImmediateNode(pc);
            } else {
                base = state.getRegisters().getRegister(baseReg).createRead();
            }
        }
        if (indexReg != null) {
            index = state.getRegisters().getRegister(indexReg).createRead();
        }
        if (segment != null) {
            segmentBase = state.getRegisters().getFS().createRead();
        }
    }

    public long executeI32(VirtualFrame frame) {
        int seg = 0;
        if (segment != null) {
            seg = segmentBase.executeI32(frame);
        }
        int baseaddr = 0;
        if (base != null) {
            baseaddr = base.executeI32(frame);
        }
        int indexval = 0;
        if (index != null) {
            indexval = index.executeI32(frame);
        }
        int addr = seg + (int) displacement + baseaddr + (indexval << scale);
        return addr;
    }

    public long executeI64(VirtualFrame frame) {
        long seg = 0;
        if (segment != null) {
            seg = segmentBase.executeI64(frame);
        }
        long baseaddr = 0;
        if (base != null) {
            baseaddr = base.executeI64(frame);
        }
        long indexval = 0;
        if (index != null) {
            indexval = index.executeI64(frame);
        }
        long addr = seg + displacement + baseaddr + (indexval << scale);
        return addr;
    }

    public long execute(VirtualFrame frame) {
        if (addressOverride) {
            return executeI32(frame);
        } else {
            return executeI64(frame);
        }
    }
}
