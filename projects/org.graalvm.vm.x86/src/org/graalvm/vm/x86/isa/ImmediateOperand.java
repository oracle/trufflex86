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

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.node.ImmediateNode;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

public class ImmediateOperand extends Operand {
    private final long value;
    private final int size;

    public ImmediateOperand(byte value) {
        this.value = value;
        this.size = 1;
    }

    public ImmediateOperand(short value) {
        this.value = value;
        this.size = 2;
    }

    public ImmediateOperand(int value) {
        this.value = value;
        this.size = 4;
    }

    public ImmediateOperand(long value) {
        this.value = value;
        this.size = 8;
    }

    public long getValue() {
        return value;
    }

    @Override
    public String toString() {
        switch (getSize()) {
            case 1:
                if ((byte) value == (byte) 0x80) {
                    return "0x80";
                } else if (value < 0) {
                    return String.format("-0x%x", (byte) -value);
                } else {
                    return String.format("0x%x", (byte) value);
                }
            case 2:
                if ((short) value == (short) 0x8000) {
                    return "0x8000";
                } else if (value < 0) {
                    return String.format("-0x%x", (short) -value);
                } else {
                    return String.format("0x%x", (short) value);
                }
            case 4:
                if ((int) value == 0x80000000) {
                    return "0x80000000";
                } else if (value < 0) {
                    return String.format("-0x%x", (int) -value);
                } else {
                    return String.format("0x%x", (int) value);
                }
            default:
                return String.format("0x%x", value);
        }
    }

    @Override
    public ReadNode createRead(ArchitecturalState state, long pc) {
        return new ImmediateNode(value);
    }

    @Override
    public WriteNode createWrite(ArchitecturalState state, long pc) {
        throw new UnsupportedOperationException("cannot create write node for an immediate");
    }

    @Override
    public int getSize() {
        return size;
    }
}
