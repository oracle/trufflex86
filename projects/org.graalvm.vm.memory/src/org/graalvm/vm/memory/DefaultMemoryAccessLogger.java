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
package org.graalvm.vm.memory;

import org.graalvm.vm.memory.util.Stringify;
import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.memory.vector.Vector256;
import org.graalvm.vm.memory.vector.Vector512;

public class DefaultMemoryAccessLogger implements MemoryAccessListener {
    private static String stringify(int size, long value) {
        switch (size) {
            case 1:
                return Stringify.i8((byte) value);
            case 2:
                return Stringify.i16((short) value);
            case 4:
                return Stringify.i32((int) value);
            case 8:
                return Stringify.i64(value);
            default:
                return null;
        }
    }

    public void logMemoryRead(long address, int size) {
        System.out.printf("Memory access to 0x%016x: read %d bytes\n", address, size);
    }

    public void logMemoryRead(long address, int size, long value) {
        String val = stringify(size, value);
        if (val != null) {
            System.out.printf("Memory access to 0x%016x: read %d bytes (0x%016x, '%s')\n", address, size, value, val);
        } else {
            System.out.printf("Memory access to 0x%016x: read %d bytes (0x%016x)\n", address, size, value);
        }
    }

    public void logMemoryRead(long address, Vector128 value) {
        System.out.printf("Memory access to 0x%016x: read 16 bytes (%s)\n", address, value);
    }

    public void logMemoryRead(long address, Vector256 value) {
        System.out.printf("Memory access to 0x%016x: read 32 bytes (%s)\n", address, value);
    }

    public void logMemoryRead(long address, Vector512 value) {
        System.out.printf("Memory access to 0x%016x: read 64 bytes (%s)\n", address, value);
    }

    public void logMemoryWrite(long address, int size, long value) {
        String val = stringify(size, value);
        if (val != null) {
            System.out.printf("Memory access to 0x%016x: write %d bytes (0x%016x, '%s')\n", address, size, value, val);
        } else {
            System.out.printf("Memory access to 0x%016x: write %d bytes (0x%016x)\n", address, size, value);
        }
    }

    public void logMemoryWrite(long address, Vector128 value) {
        System.out.printf("Memory access to 0x%016x: write 16 bytes (%s)\n", address, value);
    }

    public void logMemoryWrite(long address, Vector256 value) {
        System.out.printf("Memory access to 0x%016x: write 32 bytes (%s)\n", address, value);
    }

    public void logMemoryWrite(long address, Vector512 value) {
        System.out.printf("Memory access to 0x%016x: write 64 bytes (%s)\n", address, value);
    }
}
