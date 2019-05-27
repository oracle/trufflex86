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
import org.graalvm.vm.util.io.Endianess;

public class SockaddrIn6 extends Sockaddr {
    public short sin6_port;
    public int sin6_flowinfo;
    public byte[] sin6_addr;
    public int sin6_scope_id;

    @Override
    public PosixPointer read(PosixPointer ptr) {
        PosixPointer p = ptr;
        sa_family = p.getI16();
        p = p.add(2);
        sin6_port = p.getI16();
        p = p.add(2);
        sin6_flowinfo = p.getI32();
        p = p.add(4);
        sin6_addr = new byte[16];
        for (int i = 0; i < sin6_addr.length; i++) {
            sin6_addr[i] = p.add(i).getI8();
        }
        p = p.add(16);
        sin6_scope_id = p.getI32();
        return p.add(4);
    }

    @Override
    public PosixPointer write(PosixPointer ptr) {
        PosixPointer p = ptr;
        p.setI16(sa_family);
        p = p.add(2);
        p.setI16(sin6_port);
        p = p.add(2);
        p.setI32(sin6_flowinfo);
        p = p.add(4);
        for (int i = 0; i < sin6_addr.length; i++) {
            p.add(i).setI8(sin6_addr[i]);
        }
        p = p.add(16);
        p.setI32(sin6_scope_id);
        return p.add(4);
    }

    public String getAddressString() {
        short[] parts = new short[8];
        for (int i = 0; i < parts.length; i++) {
            parts[i] = Endianess.get16bitBE(sin6_addr, i * 2);
        }
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                buf.append(':');
            }
            buf.append(Integer.toHexString(Short.toUnsignedInt(parts[i])));
        }
        return buf.toString();
    }

    @Override
    public String toString() {
        return "{sa_family=" + Socket.addressFamily(sa_family) + ",sin_port=" + sin6_port + ",sin_addr=\"" + getAddressString() + "\"}";
    }
}
