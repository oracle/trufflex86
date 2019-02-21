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
package org.graalvm.vm.posix.test.api;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.graalvm.vm.posix.api.BytePosixPointer;
import org.graalvm.vm.posix.api.CString;
import org.graalvm.vm.posix.api.PosixPointer;
import org.junit.Test;

public class CStringTest {
    public static final byte[] data = {0, 'n', 'a', 'm', 'e', '.', 0, 'V', 'a', 'r', 'i', 'a', 'b', 'l', 'e', 0,
                    'a', 'b', 'l', 'e', 0, 0, 'x', 'x', 0};

    private static PosixPointer ptr(int offset) {
        return new BytePosixPointer(data, offset);
    }

    private static BytePosixPointer buf(int sz) {
        return new BytePosixPointer(new byte[sz]);
    }

    @Test
    public void testRead1() {
        String ref = "";
        String act = CString.cstr(ptr(0));
        assertEquals(ref, act);
    }

    @Test
    public void testRead2() {
        String ref = "name.";
        String act = CString.cstr(ptr(1));
        assertEquals(ref, act);
    }

    @Test
    public void testRead3() {
        String ref = "Variable";
        String act = CString.cstr(ptr(7));
        assertEquals(ref, act);
    }

    @Test
    public void testRead4() {
        String ref = "able";
        String act = CString.cstr(ptr(11));
        assertEquals(ref, act);
    }

    @Test
    public void testRead5() {
        String ref = "able";
        String act = CString.cstr(ptr(16));
        assertEquals(ref, act);
    }

    @Test
    public void testRead6() {
        String ref = "";
        String act = CString.cstr(ptr(24));
        assertEquals(ref, act);
    }

    @Test
    public void testWrite1() {
        BytePosixPointer buf = buf(6);
        byte[] ref = {'h', 'e', 'l', 'l', 'o', 0};
        CString.strcpy(buf, "hello");
        assertArrayEquals(ref, buf.memory);
    }

    @Test
    public void testWrite2() {
        BytePosixPointer buf = buf(12);
        byte[] ref = {'h', 'e', 'l', 'l', 'o', 0, 'w', 'o', 'r', 'l', 'd', 0};
        CString.strcpy(buf, "hello\0world");
        assertArrayEquals(ref, buf.memory);
    }
}
