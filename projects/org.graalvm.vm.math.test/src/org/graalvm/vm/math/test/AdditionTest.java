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
package org.graalvm.vm.math.test;

import static org.junit.Assert.assertEquals;

import org.graalvm.vm.math.Addition;
import org.junit.Test;

public class AdditionTest {
    private static void test(int x, int y, boolean cf, boolean expectedCf) {
        boolean c = Addition.carry(x, y, cf);
        assertEquals(expectedCf, c);
    }

    @Test
    public void test() {
        test(0x00000000, 0x00000000, false, false);
        test(0x00000000, 0x00000000, true, false);
        test(0x00000d0c, 0x00000000, true, false);
        test(0x00000d0c, 0x00000d0c, true, false);
        test(0x00000000, 0x00000d0c, true, false);
        test(0x00000d0c, 0x00000000, false, false);
        test(0x00000d0c, 0x00000d0c, false, false);
        test(0x00000000, 0x00000d0c, false, false);
        test(0xffffffff, 0x00000000, false, false);
        test(0xffffffff, 0x00000001, false, true);
        test(0xffffffff, 0x00000d0c, false, true);
        test(0xffffffff, 0x80000000, false, true);
        test(0xffffffff, 0xffffffff, false, true);
        test(0xffffffff, 0x00000000, true, true);
        test(0xffffffff, 0x00000001, true, true);
        test(0xffffffff, 0x00000d0c, true, true);
        test(0xffffffff, 0x80000000, true, true);
        test(0xffffffff, 0xffffffff, true, true);
        test(0x80000000, 0x00000000, false, false);
        test(0x80000000, 0x00000d0c, false, false);
        test(0x80000000, 0x80000000, false, true);
        test(0x80000000, 0xffffffff, false, true);
        test(0x80000000, 0x00000000, true, false);
        test(0x80000000, 0x00000d0c, true, false);
        test(0x80000000, 0x80000000, true, true);
        test(0x80000000, 0xffffffff, true, true);
    }
}
