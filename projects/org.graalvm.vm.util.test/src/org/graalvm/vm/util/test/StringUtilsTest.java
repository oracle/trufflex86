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
package org.graalvm.vm.util.test;

import static org.junit.Assert.assertEquals;

import org.graalvm.vm.util.StringUtils;
import org.junit.Test;

public class StringUtilsTest {
    @Test
    public void testRepeat1() {
        assertEquals("     ", StringUtils.repeat(" ", 5));
    }

    @Test
    public void testRepeat2() {
        assertEquals("-------", StringUtils.repeat("-", 7));
    }

    @Test
    public void testRepeat3() {
        assertEquals("+-+-+-+-+-+-", StringUtils.repeat("+-", 6));
    }

    @Test
    public void testFit1() {
        assertEquals("Hell", StringUtils.fit("Hello World", 4));
    }

    @Test
    public void testFit2() {
        assertEquals("com...tils", StringUtils.fit("com.everyware.util.StringUtils", 10));
    }

    @Test
    public void testFit3() {
        assertEquals("com...tils", StringUtils.fit("com.everyware.utils.StringUtils", 10));
    }

    @Test
    public void testFit4() {
        assertEquals("com....tils", StringUtils.fit("com.everyware.util.StringUtils", 11));
    }

    @Test
    public void testFit5() {
        assertEquals("com....tils", StringUtils.fit("com.everyware.utils.StringUtils", 11));
    }

    @Test
    public void testFit6() {
        assertEquals("Hello World   ", StringUtils.fit("Hello World", 14));
    }

    @Test
    public void testPad1() {
        assertEquals("H...", StringUtils.pad("Hello World", 4));
    }

    @Test
    public void testPad2() {
        assertEquals("Hel", StringUtils.pad("Hello World", 3));
    }

    @Test
    public void testPad3() {
        assertEquals("Hello", StringUtils.pad("Hello", 5));
    }

    @Test
    public void testPad4() {
        assertEquals("Hell...", StringUtils.pad("Hello World", 7));
    }

    @Test
    public void testPad5() {
        assertEquals("Hello World   ", StringUtils.pad("Hello World", 14));
    }

    @Test
    public void testRpad1() {
        assertEquals("rld", StringUtils.rpad("Hello World", 3));
    }

    @Test
    public void testRpad2() {
        assertEquals("...orld", StringUtils.rpad("Hello World", 7));
    }

    @Test
    public void testRpad3() {
        assertEquals("...d", StringUtils.rpad("Hello World", 4));
    }

    @Test
    public void testTab1() {
        assertEquals("te      st", StringUtils.tab("te\tst", 8));
    }

    @Test
    public void testTab2() {
        assertEquals("        test", StringUtils.tab("\ttest", 8));
    }

    @Test
    public void testTab3() {
        assertEquals("ttttttt est", StringUtils.tab("ttttttt\test", 8));
    }

    @Test
    public void testTab4() {
        assertEquals("tttttttt        est", StringUtils.tab("tttttttt\test", 8));
    }
}
