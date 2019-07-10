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
package org.graalvm.vm.x86.test.runner;

import org.junit.Ignore;
import org.junit.Test;

public class MiscTests {
    @Test
    public void iAmPure() throws Exception {
        TestRunner.run("i-am-pure.elf", new String[0], "", "0\n0\n", "", 0);
    }

    @Ignore
    @Test
    public void dlIteratePhdr() throws Exception {
        String stdout = "Name: \"\" (8 segments)\n" +
                        "     0: [      0x400000; memsz:    470] flags: 0x4; PT_LOAD\n" +
                        "     1: [      0x401000; memsz:  7a40d] flags: 0x5; PT_LOAD\n" +
                        "     2: [      0x47c000; memsz:  22b2f] flags: 0x4; PT_LOAD\n" +
                        "     3: [      0x4a02c0; memsz:   65e0] flags: 0x6; PT_LOAD\n" +
                        "     4: [      0x400200; memsz:     44] flags: 0x4; PT_NOTE\n" +
                        "     5: [      0x4a02c0; memsz:     60] flags: 0x4; PT_TLS\n" +
                        "     6: [         (nil); memsz:      0] flags: 0x6; PT_GNU_STACK\n" +
                        "     7: [      0x4a02c0; memsz:   2d40] flags: 0x4; PT_GNU_RELRO\n";
        TestRunner.run("dl_iterate_phdr.elf", new String[0], "", stdout, "", 0);
    }

    @Test
    public void ftell() throws Exception {
        String stdout = "Running test case \"(fh = tmpfile()) != NULL\"\n" +
                        "Running test case \"setvbuf(fh, buffer, _IOLBF, 4) == 0\"\n" +
                        "Running test case \"fseek(fh, 0L, SEEK_SET) == 0\"\n" +
                        "Running test case \"ungetc('x', fh) == 'x'\"\n" +
                        "Running test case \"ftell(fh) == -1l\"\n" +
                        "Running test case \"fseek(fh, 0L, SEEK_SET) == 0\"\n" +
                        "Running test case \"fputc('1', fh) == '1'\"\n" +
                        "EXIT()\n";
        TestRunner.run("ftell.elf", new String[0], "", stdout, "", 0);
    }
}
