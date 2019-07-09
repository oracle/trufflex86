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
package org.graalvm.vm.posix.api;

public class Sigevent implements Struct {
    private static final int SIGEV_PAD_SIZE_32 = 13 * 4;
    private static final int SIGEV_PAD_SIZE_64 = 12 * 4;

    public final Sigval sigev_value = new Sigval();
    public int sigev_signo;
    public int sigev_notify;
    public long _function;
    public long _attribute;

    @Override
    public PosixPointer read32(PosixPointer ptr) {
        PosixPointer p = ptr;
        p = sigev_value.read32(p);
        sigev_signo = p.getI32();
        p = p.add(4);
        sigev_notify = p.getI32();
        p = p.add(4);

        PosixPointer union = p;
        _function = p.getI32();
        p = p.add(4);
        _attribute = p.getI32();
        p = p.add(4);
        return union.add(SIGEV_PAD_SIZE_32);
    }

    @Override
    public PosixPointer read64(PosixPointer ptr) {
        PosixPointer p = ptr;
        p = sigev_value.read64(p);
        sigev_signo = p.getI32();
        p = p.add(4);
        sigev_notify = p.getI32();
        p = p.add(4);

        PosixPointer union = p;
        _function = p.getI64();
        p = p.add(8);
        _attribute = p.getI64();
        p = p.add(8);
        return union.add(SIGEV_PAD_SIZE_64);
    }

    @Override
    public String toString() {
        return String.format("{sigev_value=%s, sigev_signo=%s, sigev_notify=%s}", sigev_value, Signal.toString(sigev_signo), Signal.sigev(sigev_notify));
    }
}
