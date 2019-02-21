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

public class Sigaction implements Struct {
    public long sa_handler;
    public long sa_flags;
    public long sa_restorer;
    public final Sigset sa_mask = new Sigset();

    @Override
    public PosixPointer write32(PosixPointer ptr) {
        PosixPointer p = ptr;
        p.setI32((int) sa_handler);
        p = p.add(4);
        p.setI32((int) sa_flags);
        p = p.add(4);
        p.setI32((int) sa_restorer);
        p = p.add(4);
        return sa_mask.write32(p);
    }

    @Override
    public PosixPointer write64(PosixPointer ptr) {
        PosixPointer p = ptr;
        p.setI64(sa_handler);
        p = p.add(8);
        p.setI64(sa_flags);
        p = p.add(8);
        p.setI64(sa_restorer);
        p = p.add(8);
        return sa_mask.write64(p);
    }

    @Override
    public PosixPointer read32(PosixPointer ptr) {
        PosixPointer p = ptr;
        sa_handler = Integer.toUnsignedLong(p.getI32());
        p = p.add(4);
        sa_flags = Integer.toUnsignedLong(p.getI32());
        p = p.add(4);
        sa_restorer = Integer.toUnsignedLong(p.getI32());
        p = p.add(4);
        return sa_mask.read32(p);
    }

    @Override
    public PosixPointer read64(PosixPointer ptr) {
        PosixPointer p = ptr;
        sa_handler = p.getI64();
        p = p.add(8);
        sa_flags = p.getI64();
        p = p.add(8);
        sa_restorer = p.getI64();
        p = p.add(8);
        return sa_mask.read64(p);
    }

    public void copyFrom(Sigaction other) {
        sa_handler = other.sa_handler;
        sa_flags = other.sa_flags;
        sa_restorer = other.sa_restorer;
        sa_mask.setmask(other.sa_mask);
    }
}
