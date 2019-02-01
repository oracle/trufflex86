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

public class SIB {
    private final byte sib;
    public final int ss;
    public final int index;
    public final int base;

    private static final Register[] INDEX = {Register.EAX, Register.ECX, Register.EDX, Register.EBX, null, Register.EBP, Register.ESI, Register.EDI};
    private static final Register[] BASE = {Register.EAX, Register.ECX, Register.EDX, Register.EBX, Register.ESP, null, Register.ESI, Register.EDI};

    public SIB(byte sib) {
        this.sib = sib;
        ss = (sib >> 6) & 0x03;
        index = (sib >> 3) & 0x07;
        base = sib & 0x07;
    }

    public byte getSIB() {
        return sib;
    }

    public int getShift() {
        return ss;
    }

    public Register getIndex() {
        return INDEX[index];
    }

    public Register getBase() {
        return BASE[base];
    }

    public Register getBase(boolean ext) {
        return Register.get(base + (ext ? 8 : 0));
    }

    public Register getIndex(boolean ext) {
        return Register.get(index + (ext ? 8 : 0));
    }

    @Override
    public String toString() {
        return "SIB[sib=" + String.format("0x%02x", sib) + ";ss=" + ss + ";index=" + index + ";base=" + base + "]";
    }
}
