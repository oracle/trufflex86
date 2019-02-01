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
package org.graalvm.vm.memory.test;

import static org.junit.Assert.assertEquals;

import org.graalvm.vm.memory.util.Stringify;
import org.graalvm.vm.posix.elf.Elf;
import org.junit.Test;

public class StringifyTest {
    @Test
    public void testI8_1() {
        assertEquals("A", Stringify.i8((byte) 'A'));
    }

    @Test
    public void testI8_2() {
        assertEquals("\\r", Stringify.i8((byte) 0x0D));
    }

    @Test
    public void testI8_3() {
        assertEquals("\\n", Stringify.i8((byte) 0x0A));
    }

    @Test
    public void testI8_4() {
        assertEquals("\\x1b", Stringify.i8((byte) 0x1B));
    }

    @Test
    public void testI16_1() {
        assertEquals("A\\n", Stringify.i16((short) 0x410A));
    }

    @Test
    public void testI16_2() {
        assertEquals("MZ", Stringify.i16((short) 0x4D5A));
    }

    @Test
    public void testI16_3() {
        assertEquals("pe", Stringify.i16((short) 0x7065));
    }

    @Test
    public void testI32_1() {
        assertEquals("\\x7fELF", Stringify.i32(Elf.MAGIC));
    }
}
