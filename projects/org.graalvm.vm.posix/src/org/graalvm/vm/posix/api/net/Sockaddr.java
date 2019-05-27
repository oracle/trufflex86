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
package org.graalvm.vm.posix.api.net;

import org.graalvm.vm.posix.api.PosixPointer;
import org.graalvm.vm.posix.api.Struct;

public class Sockaddr implements Struct {
    public short sa_family;
    public byte[] sa_data = new byte[14];

    @Override
    public PosixPointer read(PosixPointer ptr) {
        PosixPointer p = ptr;
        sa_family = p.getI16();
        p.add(2);
        for (int i = 0; i < sa_data.length; i++) {
            sa_data[i] = p.getI8();
            p = p.add(1);
        }
        return p;
    }

    @Override
    public PosixPointer write(PosixPointer ptr) {
        PosixPointer p = ptr;
        p.setI16(sa_family);
        p = p.add(2);
        for (int i = 0; i < sa_data.length; i++) {
            p.setI8(sa_data[i]);
            p = p.add(1);
        }
        return p;
    }

    public static Sockaddr get(PosixPointer ptr, int len) {
        if (ptr == null) {
            return null;
        }
        short family = ptr.getI16();
        switch (family) {
            case Socket.AF_INET: {
                assert len == 16;
                SockaddrIn sin = new SockaddrIn();
                sin.read(ptr);
                return sin;
            }
            case Socket.AF_INET6: {
                assert len == 28;
                SockaddrIn6 sin6 = new SockaddrIn6();
                sin6.read(ptr);
                return sin6;
            }
            default: {
                Sockaddr sa = new Sockaddr();
                sa.read(ptr);
                return sa;
            }
        }
    }

    public int getSize() {
        return 16;
    }

    @Override
    public String toString() {
        return "{sa_family=" + Socket.addressFamily(sa_family) + "}";
    }
}
