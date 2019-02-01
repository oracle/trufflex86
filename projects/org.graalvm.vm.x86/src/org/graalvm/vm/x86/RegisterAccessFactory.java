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
package org.graalvm.vm.x86;

import org.graalvm.vm.x86.isa.AVXRegister;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.node.ReadFlagsNode;

import com.oracle.truffle.api.frame.FrameSlot;

public class RegisterAccessFactory {
    private final FrameSlot[] gpr;
    private final FrameSlot[] zmm;
    private final FrameSlot[] xmm;
    private final FrameSlot[] xmmF32;
    private final FrameSlot[] xmmF64;
    private final FrameSlot[] xmmType;
    private final FrameSlot fs;
    private final FrameSlot gs;
    private final FrameSlot pc;

    private final FrameSlot cf;
    private final FrameSlot pf;
    private final FrameSlot af;
    private final FrameSlot zf;
    private final FrameSlot sf;
    private final FrameSlot df;
    private final FrameSlot of;
    private final FrameSlot ac;
    private final FrameSlot id;

    public RegisterAccessFactory(FrameSlot[] gpr, FrameSlot[] zmm, FrameSlot[] xmm, FrameSlot[] xmmF32, FrameSlot[] xmmF64, FrameSlot[] xmmType, FrameSlot pc, FrameSlot fs, FrameSlot gs, FrameSlot cf,
                    FrameSlot pf, FrameSlot af, FrameSlot zf, FrameSlot sf, FrameSlot df, FrameSlot of, FrameSlot ac, FrameSlot id) {
        this.gpr = gpr;
        this.zmm = zmm;
        this.xmm = xmm;
        this.xmmF32 = xmmF32;
        this.xmmF64 = xmmF64;
        this.xmmType = xmmType;
        this.fs = fs;
        this.gs = gs;
        this.pc = pc;
        this.cf = cf;
        this.pf = pf;
        this.af = af;
        this.zf = zf;
        this.sf = sf;
        this.df = df;
        this.of = of;
        this.ac = ac;
        this.id = id;
    }

    public AMD64Register getRegister(Register reg) {
        switch (reg) {
            case AH:
            case BH:
            case CH:
            case DH:
                return new AMD64Register(gpr[reg.getID()], 8);
            default:
                return new AMD64Register(gpr[reg.getID()]);
        }
    }

    public AVXRegister getAVXRegister(int i) {
        return new AVXRegister(zmm[i], xmm[i], xmmF32[i], xmmF64[i], xmmType[i]);
    }

    public AMD64Register getFS() {
        return new AMD64Register(fs);
    }

    public AMD64Register getGS() {
        return new AMD64Register(gs);
    }

    public AMD64Register getPC() {
        return new AMD64Register(pc);
    }

    public AMD64Flag getCF() {
        return new AMD64Flag(cf);
    }

    public AMD64Flag getPF() {
        return new AMD64Flag(pf);
    }

    public AMD64Flag getAF() {
        return new AMD64Flag(af);
    }

    public AMD64Flag getZF() {
        return new AMD64Flag(zf);
    }

    public AMD64Flag getSF() {
        return new AMD64Flag(sf);
    }

    public AMD64Flag getDF() {
        return new AMD64Flag(df);
    }

    public AMD64Flag getOF() {
        return new AMD64Flag(of);
    }

    public AMD64Flag getAC() {
        return new AMD64Flag(ac);
    }

    public AMD64Flag getID() {
        return new AMD64Flag(id);
    }

    public ReadFlagsNode createReadFlags() {
        return new ReadFlagsNode();
    }
}
