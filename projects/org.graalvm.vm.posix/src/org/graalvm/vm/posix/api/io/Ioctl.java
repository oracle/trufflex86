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
package org.graalvm.vm.posix.api.io;

public class Ioctl {
    public static final int _IOC_NONE = 0;
    public static final int _IOC_READ = 2;
    public static final int _IOC_WRITE = 4;

    public static final int _IOC_NRBITS = 8;
    public static final int _IOC_TYPEBITS = 8;
    public static final int _IOC_SIZEBITS = 13;
    public static final int _IOC_DIRBITS = 3;

    public static final int _IOC_NRMASK = (1 << _IOC_NRBITS) - 1;
    public static final int _IOC_TYPEMASK = (1 << _IOC_TYPEBITS) - 1;
    public static final int _IOC_SIZEMASK = (1 << _IOC_SIZEBITS) - 1;
    public static final int _IOC_DIRMASK = (1 << _IOC_DIRBITS) - 1;

    public static final int _IOC_NRSHIFT = 0;
    public static final int _IOC_TYPESHIFT = _IOC_NRSHIFT + _IOC_NRBITS;
    public static final int _IOC_SIZESHIFT = _IOC_TYPESHIFT + _IOC_TYPEBITS;
    public static final int _IOC_DIRSHIFT = _IOC_SIZESHIFT + _IOC_SIZEBITS;

    public static int _IOC(int dir, int type, int nr, int size) {
        // @formatter:off
        return (dir << _IOC_DIRSHIFT)
                | (type << _IOC_TYPESHIFT)
                | (nr << _IOC_NRSHIFT)
                | (size << _IOC_SIZESHIFT);
        // @formatter:on
    }

    public static int _IO(int type, int nr) {
        return _IOC(_IOC_NONE, type, nr, 0);
    }

    public static int _IOR(int type, int nr, int size) {
        return _IOC(_IOC_READ, type, nr, size);
    }

    public static int _IOW(int type, int nr, int size) {
        return _IOC(_IOC_WRITE, type, nr, size);
    }

    public static int _IORW(int type, int nr, int size) {
        return _IOC(_IOC_READ | _IOC_WRITE, type, nr, size);
    }

    public static int _IOC_DIR(int nr) {
        return (nr >> _IOC_DIRSHIFT) & _IOC_DIRMASK;
    }

    public static int _IOC_TYPE(int nr) {
        return (nr >> _IOC_TYPESHIFT) & _IOC_TYPEMASK;
    }

    public static int _IOC_NR(int nr) {
        return (nr >> _IOC_NRSHIFT) & _IOC_NRMASK;
    }

    public static int _IOC_SIZE(int nr) {
        return (nr >> _IOC_SIZESHIFT) & _IOC_SIZEMASK;
    }

    public static String toString(int n) {
        int dir = _IOC_DIR(n);
        int type = _IOC_TYPE(n);
        int nr = _IOC_NR(n);
        int size = _IOC_SIZE(n);
        String rw;
        switch (dir) {
            case _IOC_NONE:
                rw = "_IOC_NONE";
                break;
            case _IOC_READ:
                rw = "_IOC_READ";
                break;
            case _IOC_WRITE:
                rw = "_IOC_WRITE";
                break;
            case _IOC_READ | _IOC_WRITE:
                rw = "_IOC_READ|_IOC_WRITE";
                break;
            default:
                // unreachable
                rw = Integer.toString(dir);
        }
        return String.format("_IOC(%s, %d, %d, %d)", rw, type, nr, size);
    }
}
