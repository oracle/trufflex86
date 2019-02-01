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

import org.junit.Test;

public class FloatTest {
    @Test
    public void floatAdd() throws Exception {
        String stdout = "00000000:00000000:00000000\n" +
                        "3f800000:00000000:3f800000\n" +
                        "3f800000:3f800000:40000000\n" +
                        "41a80000:42280000:427c0000\n" +
                        "c1a80000:42280000:41a80000\n" +
                        "41a80000:c2280000:c1a80000\n" +
                        "c1a80000:c2280000:c27c0000\n" +
                        "41a80000:00000000:41a80000\n" +
                        "00000000:41a80000:41a80000\n" +
                        "c1a80000:00000000:c1a80000\n" +
                        "00000000:c1a80000:c1a80000\n" +
                        "322bcc77:3f800000:3f800000\n" +
                        "219392ef:3f800000:3f800000\n" +
                        "3f800000:322bcc77:3f800000\n" +
                        "3f800000:219392ef:3f800000\n" +
                        "4cbebc20:219392ef:4cbebc20\n" +
                        "322bcc77:5d5e0b6b:5d5e0b6b\n" +
                        "7fc00000:00000000:7fc00000\n" +
                        "7fc00000:3f800000:7fc00000\n" +
                        "7fc00000:bf800000:7fc00000\n" +
                        "7fc00000:42280000:7fc00000\n" +
                        "7fc00000:c2280000:7fc00000\n" +
                        "7fc00000:6c4ecb8f:7fc00000\n" +
                        "7fc00000:ec4ecb8f:7fc00000\n" +
                        "00000000:7fc00000:7fc00000\n" +
                        "3f800000:7fc00000:7fc00000\n" +
                        "bf800000:7fc00000:7fc00000\n" +
                        "42280000:7fc00000:7fc00000\n" +
                        "c2280000:7fc00000:7fc00000\n" +
                        "6c4ecb8f:7fc00000:7fc00000\n" +
                        "ec4ecb8f:7fc00000:7fc00000\n" +
                        "7fc00000:7fc00000:7fc00000\n" +
                        "7f800000:00000000:7f800000\n" +
                        "7f800000:3f800000:7f800000\n" +
                        "7f800000:bf800000:7f800000\n" +
                        "7f800000:42280000:7f800000\n" +
                        "7f800000:c2280000:7f800000\n" +
                        "7f800000:6c4ecb8f:7f800000\n" +
                        "7f800000:ec4ecb8f:7f800000\n" +
                        "00000000:7f800000:7f800000\n" +
                        "3f800000:7f800000:7f800000\n" +
                        "bf800000:7f800000:7f800000\n" +
                        "42280000:7f800000:7f800000\n" +
                        "c2280000:7f800000:7f800000\n" +
                        "6c4ecb8f:7f800000:7f800000\n" +
                        "ec4ecb8f:7f800000:7f800000\n" +
                        "7f800000:7f800000:7f800000\n" +
                        "ffc00000:00000000:ffc00000\n" +
                        "ffc00000:3f800000:ffc00000\n" +
                        "ffc00000:bf800000:ffc00000\n" +
                        "ffc00000:42280000:ffc00000\n" +
                        "ffc00000:c2280000:ffc00000\n" +
                        "ffc00000:6c4ecb8f:ffc00000\n" +
                        "ffc00000:ec4ecb8f:ffc00000\n" +
                        "00000000:ffc00000:ffc00000\n" +
                        "3f800000:ffc00000:ffc00000\n" +
                        "bf800000:ffc00000:ffc00000\n" +
                        "42280000:ffc00000:ffc00000\n" +
                        "c2280000:ffc00000:ffc00000\n" +
                        "6c4ecb8f:ffc00000:ffc00000\n" +
                        "ec4ecb8f:ffc00000:ffc00000\n" +
                        "7fc00000:ffc00000:7fc00000\n" +
                        "ffc00000:ffc00000:ffc00000\n" +
                        "ffc00000:7fc00000:ffc00000\n" +
                        "ff800000:00000000:ff800000\n" +
                        "ff800000:3f800000:ff800000\n" +
                        "ff800000:bf800000:ff800000\n" +
                        "ff800000:42280000:ff800000\n" +
                        "ff800000:c2280000:ff800000\n" +
                        "ff800000:6c4ecb8f:ff800000\n" +
                        "ff800000:ec4ecb8f:ff800000\n" +
                        "00000000:ff800000:ff800000\n" +
                        "3f800000:ff800000:ff800000\n" +
                        "bf800000:ff800000:ff800000\n" +
                        "42280000:ff800000:ff800000\n" +
                        "c2280000:ff800000:ff800000\n" +
                        "6c4ecb8f:ff800000:ff800000\n" +
                        "ec4ecb8f:ff800000:ff800000\n" +
                        "7f800000:ff800000:7fc00000\n" +
                        "ff800000:ff800000:ff800000\n" +
                        "ff800000:7f800000:7fc00000\n" +
                        "6c4ecb8f:6c4ecb8f:6ccecb8f\n" +
                        "6c4ecb8f:ec4ecb8f:00000000\n" +
                        "ec4ecb8f:6c4ecb8f:00000000\n" +
                        "ec4ecb8f:ec4ecb8f:eccecb8f\n" +
                        "7f7fffff:00000000:7f7fffff\n" +
                        "7f7fffff:3f800000:7f7fffff\n" +
                        "7f7fffff:bf800000:7f7fffff\n" +
                        "7f7fffff:42280000:7f7fffff\n" +
                        "7f7fffff:c2280000:7f7fffff\n" +
                        "7f7fffff:6c4ecb8f:7f7fffff\n" +
                        "7f7fffff:ec4ecb8f:7f7fffff\n" +
                        "00000000:7f7fffff:7f7fffff\n" +
                        "3f800000:7f7fffff:7f7fffff\n" +
                        "bf800000:7f7fffff:7f7fffff\n" +
                        "42280000:7f7fffff:7f7fffff\n" +
                        "c2280000:7f7fffff:7f7fffff\n" +
                        "6c4ecb8f:7f7fffff:7f7fffff\n" +
                        "ec4ecb8f:7f7fffff:7f7fffff\n" +
                        "7f7fffff:7f7fffff:7f800000\n" +
                        "7f7fffff:ff7fffff:00000000\n" +
                        "7f7fffff:00800000:7f7fffff\n" +
                        "7f7fffff:80800000:7f7fffff\n" +
                        "ff7fffff:00000000:ff7fffff\n" +
                        "ff7fffff:3f800000:ff7fffff\n" +
                        "ff7fffff:bf800000:ff7fffff\n" +
                        "ff7fffff:42280000:ff7fffff\n" +
                        "ff7fffff:c2280000:ff7fffff\n" +
                        "ff7fffff:6c4ecb8f:ff7fffff\n" +
                        "ff7fffff:ec4ecb8f:ff7fffff\n" +
                        "00000000:ff7fffff:ff7fffff\n" +
                        "3f800000:ff7fffff:ff7fffff\n" +
                        "bf800000:ff7fffff:ff7fffff\n" +
                        "42280000:ff7fffff:ff7fffff\n" +
                        "c2280000:ff7fffff:ff7fffff\n" +
                        "6c4ecb8f:ff7fffff:ff7fffff\n" +
                        "ec4ecb8f:ff7fffff:ff7fffff\n" +
                        "ff7fffff:7f7fffff:00000000\n" +
                        "ff7fffff:ff7fffff:ff800000\n" +
                        "ff7fffff:00800000:ff7fffff\n" +
                        "ff7fffff:80800000:ff7fffff\n" +
                        "00800000:00000000:00800000\n" +
                        "00800000:3f800000:3f800000\n" +
                        "00800000:bf800000:bf800000\n" +
                        "00800000:42280000:42280000\n" +
                        "00800000:c2280000:c2280000\n" +
                        "00800000:6c4ecb8f:6c4ecb8f\n" +
                        "00800000:ec4ecb8f:ec4ecb8f\n" +
                        "00000000:00800000:00800000\n" +
                        "3f800000:00800000:3f800000\n" +
                        "bf800000:00800000:bf800000\n" +
                        "42280000:00800000:42280000\n" +
                        "c2280000:00800000:c2280000\n" +
                        "6c4ecb8f:00800000:6c4ecb8f\n" +
                        "ec4ecb8f:00800000:ec4ecb8f\n" +
                        "00800000:00800000:01000000\n" +
                        "00800000:80800000:00000000\n" +
                        "00800000:00800000:01000000\n" +
                        "00800000:80800000:00000000\n" +
                        "80800000:00000000:80800000\n" +
                        "80800000:3f800000:3f800000\n" +
                        "80800000:bf800000:bf800000\n" +
                        "80800000:42280000:42280000\n" +
                        "80800000:c2280000:c2280000\n" +
                        "80800000:6c4ecb8f:6c4ecb8f\n" +
                        "80800000:ec4ecb8f:ec4ecb8f\n" +
                        "00000000:80800000:80800000\n" +
                        "3f800000:80800000:3f800000\n" +
                        "bf800000:80800000:bf800000\n" +
                        "42280000:80800000:42280000\n" +
                        "c2280000:80800000:c2280000\n" +
                        "6c4ecb8f:80800000:6c4ecb8f\n" +
                        "ec4ecb8f:80800000:ec4ecb8f\n" +
                        "80800000:00800000:00000000\n" +
                        "80800000:80800000:81000000\n" +
                        "80800000:7f7fffff:7f7fffff\n" +
                        "80800000:ff7fffff:ff7fffff\n";
        TestRunner.run("float-add.elf", new String[0], "", stdout, "", 0);
    }

    @Test
    public void floatSub() throws Exception {
        String stdout = "00000000:00000000:00000000\n" +
                        "3f800000:00000000:3f800000\n" +
                        "3f800000:3f800000:00000000\n" +
                        "41a80000:42280000:c1a80000\n" +
                        "c1a80000:42280000:c27c0000\n" +
                        "41a80000:c2280000:427c0000\n" +
                        "c1a80000:c2280000:41a80000\n" +
                        "41a80000:00000000:41a80000\n" +
                        "00000000:41a80000:c1a80000\n" +
                        "c1a80000:00000000:c1a80000\n" +
                        "00000000:c1a80000:41a80000\n" +
                        "322bcc77:3f800000:bf800000\n" +
                        "219392ef:3f800000:bf800000\n" +
                        "3f800000:322bcc77:3f800000\n" +
                        "3f800000:219392ef:3f800000\n" +
                        "4cbebc20:219392ef:4cbebc20\n" +
                        "322bcc77:5d5e0b6b:dd5e0b6b\n" +
                        "7fc00000:00000000:7fc00000\n" +
                        "7fc00000:3f800000:7fc00000\n" +
                        "7fc00000:bf800000:7fc00000\n" +
                        "7fc00000:42280000:7fc00000\n" +
                        "7fc00000:c2280000:7fc00000\n" +
                        "7fc00000:6c4ecb8f:7fc00000\n" +
                        "7fc00000:ec4ecb8f:7fc00000\n" +
                        "00000000:7fc00000:7fc00000\n" +
                        "3f800000:7fc00000:7fc00000\n" +
                        "bf800000:7fc00000:7fc00000\n" +
                        "42280000:7fc00000:7fc00000\n" +
                        "c2280000:7fc00000:7fc00000\n" +
                        "6c4ecb8f:7fc00000:7fc00000\n" +
                        "ec4ecb8f:7fc00000:7fc00000\n" +
                        "7fc00000:7fc00000:7fc00000\n" +
                        "7f800000:00000000:7f800000\n" +
                        "7f800000:3f800000:7f800000\n" +
                        "7f800000:bf800000:7f800000\n" +
                        "7f800000:42280000:7f800000\n" +
                        "7f800000:c2280000:7f800000\n" +
                        "7f800000:6c4ecb8f:7f800000\n" +
                        "7f800000:ec4ecb8f:7f800000\n" +
                        "00000000:7f800000:ff800000\n" +
                        "3f800000:7f800000:ff800000\n" +
                        "bf800000:7f800000:ff800000\n" +
                        "42280000:7f800000:ff800000\n" +
                        "c2280000:7f800000:ff800000\n" +
                        "6c4ecb8f:7f800000:ff800000\n" +
                        "ec4ecb8f:7f800000:ff800000\n" +
                        "7f800000:7f800000:7fc00000\n" +
                        "ffc00000:00000000:ffc00000\n" +
                        "ffc00000:3f800000:ffc00000\n" +
                        "ffc00000:bf800000:ffc00000\n" +
                        "ffc00000:42280000:ffc00000\n" +
                        "ffc00000:c2280000:ffc00000\n" +
                        "ffc00000:6c4ecb8f:ffc00000\n" +
                        "ffc00000:ec4ecb8f:ffc00000\n" +
                        "00000000:ffc00000:ffc00000\n" +
                        "3f800000:ffc00000:ffc00000\n" +
                        "bf800000:ffc00000:ffc00000\n" +
                        "42280000:ffc00000:ffc00000\n" +
                        "c2280000:ffc00000:ffc00000\n" +
                        "6c4ecb8f:ffc00000:ffc00000\n" +
                        "ec4ecb8f:ffc00000:ffc00000\n" +
                        "7fc00000:ffc00000:7fc00000\n" +
                        "ffc00000:ffc00000:ffc00000\n" +
                        "ffc00000:7fc00000:ffc00000\n" +
                        "ff800000:00000000:ff800000\n" +
                        "ff800000:3f800000:ff800000\n" +
                        "ff800000:bf800000:ff800000\n" +
                        "ff800000:42280000:ff800000\n" +
                        "ff800000:c2280000:ff800000\n" +
                        "ff800000:6c4ecb8f:ff800000\n" +
                        "ff800000:ec4ecb8f:ff800000\n" +
                        "00000000:ff800000:7f800000\n" +
                        "3f800000:ff800000:7f800000\n" +
                        "bf800000:ff800000:7f800000\n" +
                        "42280000:ff800000:7f800000\n" +
                        "c2280000:ff800000:7f800000\n" +
                        "6c4ecb8f:ff800000:7f800000\n" +
                        "ec4ecb8f:ff800000:7f800000\n" +
                        "7f800000:ff800000:7f800000\n" +
                        "ff800000:ff800000:7fc00000\n" +
                        "ff800000:7f800000:ff800000\n" +
                        "6c4ecb8f:6c4ecb8f:00000000\n" +
                        "6c4ecb8f:ec4ecb8f:6ccecb8f\n" +
                        "ec4ecb8f:6c4ecb8f:eccecb8f\n" +
                        "ec4ecb8f:ec4ecb8f:00000000\n" +
                        "7f7fffff:00000000:7f7fffff\n" +
                        "7f7fffff:3f800000:7f7fffff\n" +
                        "7f7fffff:bf800000:7f7fffff\n" +
                        "7f7fffff:42280000:7f7fffff\n" +
                        "7f7fffff:c2280000:7f7fffff\n" +
                        "7f7fffff:6c4ecb8f:7f7fffff\n" +
                        "7f7fffff:ec4ecb8f:7f7fffff\n" +
                        "00000000:7f7fffff:ff7fffff\n" +
                        "3f800000:7f7fffff:ff7fffff\n" +
                        "bf800000:7f7fffff:ff7fffff\n" +
                        "42280000:7f7fffff:ff7fffff\n" +
                        "c2280000:7f7fffff:ff7fffff\n" +
                        "6c4ecb8f:7f7fffff:ff7fffff\n" +
                        "ec4ecb8f:7f7fffff:ff7fffff\n" +
                        "7f7fffff:7f7fffff:00000000\n" +
                        "7f7fffff:ff7fffff:7f800000\n" +
                        "7f7fffff:00800000:7f7fffff\n" +
                        "7f7fffff:80800000:7f7fffff\n" +
                        "ff7fffff:00000000:ff7fffff\n" +
                        "ff7fffff:3f800000:ff7fffff\n" +
                        "ff7fffff:bf800000:ff7fffff\n" +
                        "ff7fffff:42280000:ff7fffff\n" +
                        "ff7fffff:c2280000:ff7fffff\n" +
                        "ff7fffff:6c4ecb8f:ff7fffff\n" +
                        "ff7fffff:ec4ecb8f:ff7fffff\n" +
                        "00000000:ff7fffff:7f7fffff\n" +
                        "3f800000:ff7fffff:7f7fffff\n" +
                        "bf800000:ff7fffff:7f7fffff\n" +
                        "42280000:ff7fffff:7f7fffff\n" +
                        "c2280000:ff7fffff:7f7fffff\n" +
                        "6c4ecb8f:ff7fffff:7f7fffff\n" +
                        "ec4ecb8f:ff7fffff:7f7fffff\n" +
                        "ff7fffff:7f7fffff:ff800000\n" +
                        "ff7fffff:ff7fffff:00000000\n" +
                        "ff7fffff:00800000:ff7fffff\n" +
                        "ff7fffff:80800000:ff7fffff\n" +
                        "00800000:00000000:00800000\n" +
                        "00800000:3f800000:bf800000\n" +
                        "00800000:bf800000:3f800000\n" +
                        "00800000:42280000:c2280000\n" +
                        "00800000:c2280000:42280000\n" +
                        "00800000:6c4ecb8f:ec4ecb8f\n" +
                        "00800000:ec4ecb8f:6c4ecb8f\n" +
                        "00000000:00800000:80800000\n" +
                        "3f800000:00800000:3f800000\n" +
                        "bf800000:00800000:bf800000\n" +
                        "42280000:00800000:42280000\n" +
                        "c2280000:00800000:c2280000\n" +
                        "6c4ecb8f:00800000:6c4ecb8f\n" +
                        "ec4ecb8f:00800000:ec4ecb8f\n" +
                        "00800000:00800000:00000000\n" +
                        "00800000:80800000:01000000\n" +
                        "00800000:00800000:00000000\n" +
                        "00800000:80800000:01000000\n" +
                        "80800000:00000000:80800000\n" +
                        "80800000:3f800000:bf800000\n" +
                        "80800000:bf800000:3f800000\n" +
                        "80800000:42280000:c2280000\n" +
                        "80800000:c2280000:42280000\n" +
                        "80800000:6c4ecb8f:ec4ecb8f\n" +
                        "80800000:ec4ecb8f:6c4ecb8f\n" +
                        "00000000:80800000:00800000\n" +
                        "3f800000:80800000:3f800000\n" +
                        "bf800000:80800000:bf800000\n" +
                        "42280000:80800000:42280000\n" +
                        "c2280000:80800000:c2280000\n" +
                        "6c4ecb8f:80800000:6c4ecb8f\n" +
                        "ec4ecb8f:80800000:ec4ecb8f\n" +
                        "80800000:00800000:81000000\n" +
                        "80800000:80800000:00000000\n" +
                        "80800000:7f7fffff:ff7fffff\n" +
                        "80800000:ff7fffff:7f7fffff\n";
        TestRunner.run("float-sub.elf", new String[0], "", stdout, "", 0);
    }

    public void floatMul() throws Exception {
        String stdout = "00000000:00000000:00000000\n" +
                        "3f800000:00000000:00000000\n" +
                        "3f800000:3f800000:3f800000\n" +
                        "41a80000:42280000:445c8000\n" +
                        "c1a80000:42280000:c45c8000\n" +
                        "41a80000:c2280000:c45c8000\n" +
                        "c1a80000:c2280000:445c8000\n" +
                        "41a80000:00000000:00000000\n" +
                        "00000000:41a80000:00000000\n" +
                        "c1a80000:00000000:80000000\n" +
                        "00000000:c1a80000:80000000\n" +
                        "322bcc77:3f800000:322bcc77\n" +
                        "219392ef:3f800000:219392ef\n" +
                        "3f800000:322bcc77:322bcc77\n" +
                        "3f800000:219392ef:219392ef\n" +
                        "4cbebc20:219392ef:2edbe6ff\n" +
                        "322bcc77:5d5e0b6b:501502f9\n" +
                        "7fc00000:00000000:7fc00000\n" +
                        "7fc00000:3f800000:7fc00000\n" +
                        "7fc00000:bf800000:7fc00000\n" +
                        "7fc00000:42280000:7fc00000\n" +
                        "7fc00000:c2280000:7fc00000\n" +
                        "7fc00000:6c4ecb8f:7fc00000\n" +
                        "7fc00000:ec4ecb8f:7fc00000\n" +
                        "00000000:7fc00000:7fc00000\n" +
                        "3f800000:7fc00000:7fc00000\n" +
                        "bf800000:7fc00000:7fc00000\n" +
                        "42280000:7fc00000:7fc00000\n" +
                        "c2280000:7fc00000:7fc00000\n" +
                        "6c4ecb8f:7fc00000:7fc00000\n" +
                        "ec4ecb8f:7fc00000:7fc00000\n" +
                        "7fc00000:7fc00000:7fc00000\n" +
                        "7f800000:00000000:7fc00000\n" +
                        "7f800000:3f800000:7f800000\n" +
                        "7f800000:bf800000:ff800000\n" +
                        "7f800000:42280000:7f800000\n" +
                        "7f800000:c2280000:ff800000\n" +
                        "7f800000:6c4ecb8f:7f800000\n" +
                        "7f800000:ec4ecb8f:ff800000\n" +
                        "00000000:7f800000:7fc00000\n" +
                        "3f800000:7f800000:7f800000\n" +
                        "bf800000:7f800000:ff800000\n" +
                        "42280000:7f800000:7f800000\n" +
                        "c2280000:7f800000:ff800000\n" +
                        "6c4ecb8f:7f800000:7f800000\n" +
                        "ec4ecb8f:7f800000:ff800000\n" +
                        "7f800000:7f800000:7f800000\n" +
                        "ffc00000:00000000:ffc00000\n" +
                        "ffc00000:3f800000:ffc00000\n" +
                        "ffc00000:bf800000:ffc00000\n" +
                        "ffc00000:42280000:ffc00000\n" +
                        "ffc00000:c2280000:ffc00000\n" +
                        "ffc00000:6c4ecb8f:ffc00000\n" +
                        "ffc00000:ec4ecb8f:ffc00000\n" +
                        "00000000:ffc00000:ffc00000\n" +
                        "3f800000:ffc00000:ffc00000\n" +
                        "bf800000:ffc00000:ffc00000\n" +
                        "42280000:ffc00000:ffc00000\n" +
                        "c2280000:ffc00000:ffc00000\n" +
                        "6c4ecb8f:ffc00000:ffc00000\n" +
                        "ec4ecb8f:ffc00000:ffc00000\n" +
                        "7fc00000:ffc00000:7fc00000\n" +
                        "ffc00000:ffc00000:ffc00000\n" +
                        "ffc00000:7fc00000:ffc00000\n" +
                        "ff800000:00000000:ffc00000\n" +
                        "ff800000:3f800000:ff800000\n" +
                        "ff800000:bf800000:7f800000\n" +
                        "ff800000:42280000:ff800000\n" +
                        "ff800000:c2280000:7f800000\n" +
                        "ff800000:6c4ecb8f:ff800000\n" +
                        "ff800000:ec4ecb8f:7f800000\n" +
                        "00000000:ff800000:ffc00000\n" +
                        "3f800000:ff800000:ff800000\n" +
                        "bf800000:ff800000:7f800000\n" +
                        "42280000:ff800000:ff800000\n" +
                        "c2280000:ff800000:7f800000\n" +
                        "6c4ecb8f:ff800000:ff800000\n" +
                        "ec4ecb8f:ff800000:7f800000\n" +
                        "7f800000:ff800000:ff800000\n" +
                        "ff800000:ff800000:7f800000\n" +
                        "ff800000:7f800000:ff800000\n" +
                        "6c4ecb8f:6c4ecb8f:7f800000\n" +
                        "6c4ecb8f:ec4ecb8f:ff800000\n" +
                        "ec4ecb8f:6c4ecb8f:ff800000\n" +
                        "ec4ecb8f:ec4ecb8f:7f800000\n" +
                        "7f7fffff:00000000:00000000\n" +
                        "7f7fffff:3f800000:7f7fffff\n" +
                        "7f7fffff:bf800000:ff7fffff\n" +
                        "7f7fffff:42280000:7f800000\n" +
                        "7f7fffff:c2280000:ff800000\n" +
                        "7f7fffff:6c4ecb8f:7f800000\n" +
                        "7f7fffff:ec4ecb8f:ff800000\n" +
                        "00000000:7f7fffff:00000000\n" +
                        "3f800000:7f7fffff:7f7fffff\n" +
                        "bf800000:7f7fffff:ff7fffff\n" +
                        "42280000:7f7fffff:7f800000\n" +
                        "c2280000:7f7fffff:ff800000\n" +
                        "6c4ecb8f:7f7fffff:7f800000\n" +
                        "ec4ecb8f:7f7fffff:ff800000\n" +
                        "7f7fffff:7f7fffff:7f800000\n" +
                        "7f7fffff:ff7fffff:ff800000\n" +
                        "7f7fffff:00800000:407fffff\n" +
                        "7f7fffff:80800000:c07fffff\n" +
                        "ff7fffff:00000000:80000000\n" +
                        "ff7fffff:3f800000:ff7fffff\n" +
                        "ff7fffff:bf800000:7f7fffff\n" +
                        "ff7fffff:42280000:ff800000\n" +
                        "ff7fffff:c2280000:7f800000\n" +
                        "ff7fffff:6c4ecb8f:ff800000\n" +
                        "ff7fffff:ec4ecb8f:7f800000\n" +
                        "00000000:ff7fffff:80000000\n" +
                        "3f800000:ff7fffff:ff7fffff\n" +
                        "bf800000:ff7fffff:7f7fffff\n" +
                        "42280000:ff7fffff:ff800000\n" +
                        "c2280000:ff7fffff:7f800000\n" +
                        "6c4ecb8f:ff7fffff:ff800000\n" +
                        "ec4ecb8f:ff7fffff:7f800000\n" +
                        "ff7fffff:7f7fffff:ff800000\n" +
                        "ff7fffff:ff7fffff:7f800000\n" +
                        "ff7fffff:00800000:c07fffff\n" +
                        "ff7fffff:80800000:407fffff\n" +
                        "00800000:00000000:00000000\n" +
                        "00800000:3f800000:00800000\n" +
                        "00800000:bf800000:80800000\n" +
                        "00800000:42280000:03280000\n" +
                        "00800000:c2280000:83280000\n" +
                        "00800000:6c4ecb8f:2d4ecb8f\n" +
                        "00800000:ec4ecb8f:ad4ecb8f\n" +
                        "00000000:00800000:00000000\n" +
                        "3f800000:00800000:00800000\n" +
                        "bf800000:00800000:80800000\n" +
                        "42280000:00800000:03280000\n" +
                        "c2280000:00800000:83280000\n" +
                        "6c4ecb8f:00800000:2d4ecb8f\n" +
                        "ec4ecb8f:00800000:ad4ecb8f\n" +
                        "00800000:00800000:00000000\n" +
                        "00800000:80800000:80000000\n" +
                        "00800000:00800000:00000000\n" +
                        "00800000:80800000:80000000\n" +
                        "80800000:00000000:80000000\n" +
                        "80800000:3f800000:80800000\n" +
                        "80800000:bf800000:00800000\n" +
                        "80800000:42280000:83280000\n" +
                        "80800000:c2280000:03280000\n" +
                        "80800000:6c4ecb8f:ad4ecb8f\n" +
                        "80800000:ec4ecb8f:2d4ecb8f\n" +
                        "00000000:80800000:80000000\n" +
                        "3f800000:80800000:80800000\n" +
                        "bf800000:80800000:00800000\n" +
                        "42280000:80800000:83280000\n" +
                        "c2280000:80800000:03280000\n" +
                        "6c4ecb8f:80800000:ad4ecb8f\n" +
                        "ec4ecb8f:80800000:2d4ecb8f\n" +
                        "80800000:00800000:80000000\n" +
                        "80800000:80800000:00000000\n" +
                        "80800000:7f7fffff:c07fffff\n" +
                        "80800000:ff7fffff:407fffff\n";
        TestRunner.run("float-mul.elf", new String[0], "", stdout, "", 0);
    }

    @Test
    public void doubleAdd() throws Exception {
        String stdout = "0000000000000000:0000000000000000:0000000000000000\n" +
                        "3ff0000000000000:0000000000000000:3ff0000000000000\n" +
                        "3ff0000000000000:3ff0000000000000:4000000000000000\n" +
                        "4035000000000000:4045000000000000:404f800000000000\n" +
                        "c035000000000000:4045000000000000:4035000000000000\n" +
                        "4035000000000000:c045000000000000:c035000000000000\n" +
                        "c035000000000000:c045000000000000:c04f800000000000\n" +
                        "4035000000000000:0000000000000000:4035000000000000\n" +
                        "0000000000000000:4035000000000000:4035000000000000\n" +
                        "c035000000000000:0000000000000000:c035000000000000\n" +
                        "0000000000000000:c035000000000000:c035000000000000\n" +
                        "3e45798ee2308c3a:3ff0000000000000:3ff0000002af31dc\n" +
                        "3c32725dd1d243ac:3ff0000000000000:3ff0000000000000\n" +
                        "3ff0000000000000:3e45798ee2308c3a:3ff0000002af31dc\n" +
                        "3ff0000000000000:3c32725dd1d243ac:3ff0000000000000\n" +
                        "4197d78400000000:3c32725dd1d243ac:4197d78400000000\n" +
                        "3e45798ee2308c3a:43abc16d674ec800:43abc16d674ec800\n" +
                        "7ff8000000000000:0000000000000000:7ff8000000000000\n" +
                        "7ff8000000000000:3ff0000000000000:7ff8000000000000\n" +
                        "7ff8000000000000:bff0000000000000:7ff8000000000000\n" +
                        "7ff8000000000000:4045000000000000:7ff8000000000000\n" +
                        "7ff8000000000000:c045000000000000:7ff8000000000000\n" +
                        "7ff8000000000000:4589d971e0000000:7ff8000000000000\n" +
                        "7ff8000000000000:c589d971e0000000:7ff8000000000000\n" +
                        "0000000000000000:7ff8000000000000:7ff8000000000000\n" +
                        "3ff0000000000000:7ff8000000000000:7ff8000000000000\n" +
                        "bff0000000000000:7ff8000000000000:7ff8000000000000\n" +
                        "4045000000000000:7ff8000000000000:7ff8000000000000\n" +
                        "c045000000000000:7ff8000000000000:7ff8000000000000\n" +
                        "4589d971e0000000:7ff8000000000000:7ff8000000000000\n" +
                        "c589d971e0000000:7ff8000000000000:7ff8000000000000\n" +
                        "7ff8000000000000:7ff8000000000000:7ff8000000000000\n" +
                        "7ff0000000000000:0000000000000000:7ff0000000000000\n" +
                        "7ff0000000000000:3ff0000000000000:7ff0000000000000\n" +
                        "7ff0000000000000:bff0000000000000:7ff0000000000000\n" +
                        "7ff0000000000000:4045000000000000:7ff0000000000000\n" +
                        "7ff0000000000000:c045000000000000:7ff0000000000000\n" +
                        "7ff0000000000000:4589d971e0000000:7ff0000000000000\n" +
                        "7ff0000000000000:c589d971e0000000:7ff0000000000000\n" +
                        "0000000000000000:7ff0000000000000:7ff0000000000000\n" +
                        "3ff0000000000000:7ff0000000000000:7ff0000000000000\n" +
                        "bff0000000000000:7ff0000000000000:7ff0000000000000\n" +
                        "4045000000000000:7ff0000000000000:7ff0000000000000\n" +
                        "c045000000000000:7ff0000000000000:7ff0000000000000\n" +
                        "4589d971e0000000:7ff0000000000000:7ff0000000000000\n" +
                        "c589d971e0000000:7ff0000000000000:7ff0000000000000\n" +
                        "7ff0000000000000:7ff0000000000000:7ff0000000000000\n" +
                        "fff8000000000000:0000000000000000:fff8000000000000\n" +
                        "fff8000000000000:3ff0000000000000:fff8000000000000\n" +
                        "fff8000000000000:bff0000000000000:fff8000000000000\n" +
                        "fff8000000000000:4045000000000000:fff8000000000000\n" +
                        "fff8000000000000:c045000000000000:fff8000000000000\n" +
                        "fff8000000000000:4589d971e0000000:fff8000000000000\n" +
                        "fff8000000000000:c589d971e0000000:fff8000000000000\n" +
                        "0000000000000000:fff8000000000000:fff8000000000000\n" +
                        "3ff0000000000000:fff8000000000000:fff8000000000000\n" +
                        "bff0000000000000:fff8000000000000:fff8000000000000\n" +
                        "4045000000000000:fff8000000000000:fff8000000000000\n" +
                        "c045000000000000:fff8000000000000:fff8000000000000\n" +
                        "4589d971e0000000:fff8000000000000:fff8000000000000\n" +
                        "c589d971e0000000:fff8000000000000:fff8000000000000\n" +
                        "7ff8000000000000:fff8000000000000:7ff8000000000000\n" +
                        "fff8000000000000:fff8000000000000:fff8000000000000\n" +
                        "fff8000000000000:7ff8000000000000:fff8000000000000\n" +
                        "fff0000000000000:0000000000000000:fff0000000000000\n" +
                        "fff0000000000000:3ff0000000000000:fff0000000000000\n" +
                        "fff0000000000000:bff0000000000000:fff0000000000000\n" +
                        "fff0000000000000:4045000000000000:fff0000000000000\n" +
                        "fff0000000000000:c045000000000000:fff0000000000000\n" +
                        "fff0000000000000:4589d971e0000000:fff0000000000000\n" +
                        "fff0000000000000:c589d971e0000000:fff0000000000000\n" +
                        "0000000000000000:fff0000000000000:fff0000000000000\n" +
                        "3ff0000000000000:fff0000000000000:fff0000000000000\n" +
                        "bff0000000000000:fff0000000000000:fff0000000000000\n" +
                        "4045000000000000:fff0000000000000:fff0000000000000\n" +
                        "c045000000000000:fff0000000000000:fff0000000000000\n" +
                        "4589d971e0000000:fff0000000000000:fff0000000000000\n" +
                        "c589d971e0000000:fff0000000000000:fff0000000000000\n" +
                        "7ff0000000000000:fff0000000000000:fff8000000000000\n" +
                        "fff0000000000000:fff0000000000000:fff0000000000000\n" +
                        "fff0000000000000:7ff0000000000000:fff8000000000000\n" +
                        "4589d971e0000000:4589d971e0000000:4599d971e0000000\n" +
                        "4589d971e0000000:c589d971e0000000:0000000000000000\n" +
                        "c589d971e0000000:4589d971e0000000:0000000000000000\n" +
                        "c589d971e0000000:c589d971e0000000:c599d971e0000000\n" +
                        "7fefffffffffffff:0000000000000000:7fefffffffffffff\n" +
                        "7fefffffffffffff:3ff0000000000000:7fefffffffffffff\n" +
                        "7fefffffffffffff:bff0000000000000:7fefffffffffffff\n" +
                        "7fefffffffffffff:4045000000000000:7fefffffffffffff\n" +
                        "7fefffffffffffff:c045000000000000:7fefffffffffffff\n" +
                        "7fefffffffffffff:4589d971e0000000:7fefffffffffffff\n" +
                        "7fefffffffffffff:c589d971e0000000:7fefffffffffffff\n" +
                        "0000000000000000:7fefffffffffffff:7fefffffffffffff\n" +
                        "3ff0000000000000:7fefffffffffffff:7fefffffffffffff\n" +
                        "bff0000000000000:7fefffffffffffff:7fefffffffffffff\n" +
                        "4045000000000000:7fefffffffffffff:7fefffffffffffff\n" +
                        "c045000000000000:7fefffffffffffff:7fefffffffffffff\n" +
                        "4589d971e0000000:7fefffffffffffff:7fefffffffffffff\n" +
                        "c589d971e0000000:7fefffffffffffff:7fefffffffffffff\n" +
                        "7fefffffffffffff:7fefffffffffffff:7ff0000000000000\n" +
                        "7fefffffffffffff:ffefffffffffffff:0000000000000000\n" +
                        "7fefffffffffffff:0010000000000000:7fefffffffffffff\n" +
                        "7fefffffffffffff:8010000000000000:7fefffffffffffff\n" +
                        "ffefffffffffffff:0000000000000000:ffefffffffffffff\n" +
                        "ffefffffffffffff:3ff0000000000000:ffefffffffffffff\n" +
                        "ffefffffffffffff:bff0000000000000:ffefffffffffffff\n" +
                        "ffefffffffffffff:4045000000000000:ffefffffffffffff\n" +
                        "ffefffffffffffff:c045000000000000:ffefffffffffffff\n" +
                        "ffefffffffffffff:4589d971e0000000:ffefffffffffffff\n" +
                        "ffefffffffffffff:c589d971e0000000:ffefffffffffffff\n" +
                        "0000000000000000:ffefffffffffffff:ffefffffffffffff\n" +
                        "3ff0000000000000:ffefffffffffffff:ffefffffffffffff\n" +
                        "bff0000000000000:ffefffffffffffff:ffefffffffffffff\n" +
                        "4045000000000000:ffefffffffffffff:ffefffffffffffff\n" +
                        "c045000000000000:ffefffffffffffff:ffefffffffffffff\n" +
                        "4589d971e0000000:ffefffffffffffff:ffefffffffffffff\n" +
                        "c589d971e0000000:ffefffffffffffff:ffefffffffffffff\n" +
                        "ffefffffffffffff:7fefffffffffffff:0000000000000000\n" +
                        "ffefffffffffffff:ffefffffffffffff:fff0000000000000\n" +
                        "ffefffffffffffff:0010000000000000:ffefffffffffffff\n" +
                        "ffefffffffffffff:8010000000000000:ffefffffffffffff\n" +
                        "0010000000000000:0000000000000000:0010000000000000\n" +
                        "0010000000000000:3ff0000000000000:3ff0000000000000\n" +
                        "0010000000000000:bff0000000000000:bff0000000000000\n" +
                        "0010000000000000:4045000000000000:4045000000000000\n" +
                        "0010000000000000:c045000000000000:c045000000000000\n" +
                        "0010000000000000:4589d971e0000000:4589d971e0000000\n" +
                        "0010000000000000:c589d971e0000000:c589d971e0000000\n" +
                        "0000000000000000:0010000000000000:0010000000000000\n" +
                        "3ff0000000000000:0010000000000000:3ff0000000000000\n" +
                        "bff0000000000000:0010000000000000:bff0000000000000\n" +
                        "4045000000000000:0010000000000000:4045000000000000\n" +
                        "c045000000000000:0010000000000000:c045000000000000\n" +
                        "4589d971e0000000:0010000000000000:4589d971e0000000\n" +
                        "c589d971e0000000:0010000000000000:c589d971e0000000\n" +
                        "0010000000000000:0010000000000000:0020000000000000\n" +
                        "0010000000000000:8010000000000000:0000000000000000\n" +
                        "0010000000000000:0010000000000000:0020000000000000\n" +
                        "0010000000000000:8010000000000000:0000000000000000\n" +
                        "8010000000000000:0000000000000000:8010000000000000\n" +
                        "8010000000000000:3ff0000000000000:3ff0000000000000\n" +
                        "8010000000000000:bff0000000000000:bff0000000000000\n" +
                        "8010000000000000:4045000000000000:4045000000000000\n" +
                        "8010000000000000:c045000000000000:c045000000000000\n" +
                        "8010000000000000:4589d971e0000000:4589d971e0000000\n" +
                        "8010000000000000:c589d971e0000000:c589d971e0000000\n" +
                        "0000000000000000:8010000000000000:8010000000000000\n" +
                        "3ff0000000000000:8010000000000000:3ff0000000000000\n" +
                        "bff0000000000000:8010000000000000:bff0000000000000\n" +
                        "4045000000000000:8010000000000000:4045000000000000\n" +
                        "c045000000000000:8010000000000000:c045000000000000\n" +
                        "4589d971e0000000:8010000000000000:4589d971e0000000\n" +
                        "c589d971e0000000:8010000000000000:c589d971e0000000\n" +
                        "8010000000000000:0010000000000000:0000000000000000\n" +
                        "8010000000000000:8010000000000000:8020000000000000\n" +
                        "8010000000000000:7fefffffffffffff:7fefffffffffffff\n" +
                        "8010000000000000:ffefffffffffffff:ffefffffffffffff\n";
        TestRunner.run("double-add.elf", new String[0], "", stdout, "", 0);
    }

    @Test
    public void doubleSub() throws Exception {
        String stdout = "0000000000000000:0000000000000000:0000000000000000\n" +
                        "3ff0000000000000:0000000000000000:3ff0000000000000\n" +
                        "3ff0000000000000:3ff0000000000000:0000000000000000\n" +
                        "4035000000000000:4045000000000000:c035000000000000\n" +
                        "c035000000000000:4045000000000000:c04f800000000000\n" +
                        "4035000000000000:c045000000000000:404f800000000000\n" +
                        "c035000000000000:c045000000000000:4035000000000000\n" +
                        "4035000000000000:0000000000000000:4035000000000000\n" +
                        "0000000000000000:4035000000000000:c035000000000000\n" +
                        "c035000000000000:0000000000000000:c035000000000000\n" +
                        "0000000000000000:c035000000000000:4035000000000000\n" +
                        "3e45798ee2308c3a:3ff0000000000000:bfeffffffaa19c47\n" +
                        "3c32725dd1d243ac:3ff0000000000000:bff0000000000000\n" +
                        "3ff0000000000000:3e45798ee2308c3a:3feffffffaa19c47\n" +
                        "3ff0000000000000:3c32725dd1d243ac:3ff0000000000000\n" +
                        "4197d78400000000:3c32725dd1d243ac:4197d78400000000\n" +
                        "3e45798ee2308c3a:43abc16d674ec800:c3abc16d674ec800\n" +
                        "7ff8000000000000:0000000000000000:7ff8000000000000\n" +
                        "7ff8000000000000:3ff0000000000000:7ff8000000000000\n" +
                        "7ff8000000000000:bff0000000000000:7ff8000000000000\n" +
                        "7ff8000000000000:4045000000000000:7ff8000000000000\n" +
                        "7ff8000000000000:c045000000000000:7ff8000000000000\n" +
                        "7ff8000000000000:4589d971e0000000:7ff8000000000000\n" +
                        "7ff8000000000000:c589d971e0000000:7ff8000000000000\n" +
                        "0000000000000000:7ff8000000000000:7ff8000000000000\n" +
                        "3ff0000000000000:7ff8000000000000:7ff8000000000000\n" +
                        "bff0000000000000:7ff8000000000000:7ff8000000000000\n" +
                        "4045000000000000:7ff8000000000000:7ff8000000000000\n" +
                        "c045000000000000:7ff8000000000000:7ff8000000000000\n" +
                        "4589d971e0000000:7ff8000000000000:7ff8000000000000\n" +
                        "c589d971e0000000:7ff8000000000000:7ff8000000000000\n" +
                        "7ff8000000000000:7ff8000000000000:7ff8000000000000\n" +
                        "7ff0000000000000:0000000000000000:7ff0000000000000\n" +
                        "7ff0000000000000:3ff0000000000000:7ff0000000000000\n" +
                        "7ff0000000000000:bff0000000000000:7ff0000000000000\n" +
                        "7ff0000000000000:4045000000000000:7ff0000000000000\n" +
                        "7ff0000000000000:c045000000000000:7ff0000000000000\n" +
                        "7ff0000000000000:4589d971e0000000:7ff0000000000000\n" +
                        "7ff0000000000000:c589d971e0000000:7ff0000000000000\n" +
                        "0000000000000000:7ff0000000000000:fff0000000000000\n" +
                        "3ff0000000000000:7ff0000000000000:fff0000000000000\n" +
                        "bff0000000000000:7ff0000000000000:fff0000000000000\n" +
                        "4045000000000000:7ff0000000000000:fff0000000000000\n" +
                        "c045000000000000:7ff0000000000000:fff0000000000000\n" +
                        "4589d971e0000000:7ff0000000000000:fff0000000000000\n" +
                        "c589d971e0000000:7ff0000000000000:fff0000000000000\n" +
                        "7ff0000000000000:7ff0000000000000:fff8000000000000\n" +
                        "fff8000000000000:0000000000000000:fff8000000000000\n" +
                        "fff8000000000000:3ff0000000000000:fff8000000000000\n" +
                        "fff8000000000000:bff0000000000000:fff8000000000000\n" +
                        "fff8000000000000:4045000000000000:fff8000000000000\n" +
                        "fff8000000000000:c045000000000000:fff8000000000000\n" +
                        "fff8000000000000:4589d971e0000000:fff8000000000000\n" +
                        "fff8000000000000:c589d971e0000000:fff8000000000000\n" +
                        "0000000000000000:fff8000000000000:fff8000000000000\n" +
                        "3ff0000000000000:fff8000000000000:fff8000000000000\n" +
                        "bff0000000000000:fff8000000000000:fff8000000000000\n" +
                        "4045000000000000:fff8000000000000:fff8000000000000\n" +
                        "c045000000000000:fff8000000000000:fff8000000000000\n" +
                        "4589d971e0000000:fff8000000000000:fff8000000000000\n" +
                        "c589d971e0000000:fff8000000000000:fff8000000000000\n" +
                        "7ff8000000000000:fff8000000000000:7ff8000000000000\n" +
                        "fff8000000000000:fff8000000000000:fff8000000000000\n" +
                        "fff8000000000000:7ff8000000000000:fff8000000000000\n" +
                        "fff0000000000000:0000000000000000:fff0000000000000\n" +
                        "fff0000000000000:3ff0000000000000:fff0000000000000\n" +
                        "fff0000000000000:bff0000000000000:fff0000000000000\n" +
                        "fff0000000000000:4045000000000000:fff0000000000000\n" +
                        "fff0000000000000:c045000000000000:fff0000000000000\n" +
                        "fff0000000000000:4589d971e0000000:fff0000000000000\n" +
                        "fff0000000000000:c589d971e0000000:fff0000000000000\n" +
                        "0000000000000000:fff0000000000000:7ff0000000000000\n" +
                        "3ff0000000000000:fff0000000000000:7ff0000000000000\n" +
                        "bff0000000000000:fff0000000000000:7ff0000000000000\n" +
                        "4045000000000000:fff0000000000000:7ff0000000000000\n" +
                        "c045000000000000:fff0000000000000:7ff0000000000000\n" +
                        "4589d971e0000000:fff0000000000000:7ff0000000000000\n" +
                        "c589d971e0000000:fff0000000000000:7ff0000000000000\n" +
                        "7ff0000000000000:fff0000000000000:7ff0000000000000\n" +
                        "fff0000000000000:fff0000000000000:fff8000000000000\n" +
                        "fff0000000000000:7ff0000000000000:fff0000000000000\n" +
                        "4589d971e0000000:4589d971e0000000:0000000000000000\n" +
                        "4589d971e0000000:c589d971e0000000:4599d971e0000000\n" +
                        "c589d971e0000000:4589d971e0000000:c599d971e0000000\n" +
                        "c589d971e0000000:c589d971e0000000:0000000000000000\n" +
                        "7fefffffffffffff:0000000000000000:7fefffffffffffff\n" +
                        "7fefffffffffffff:3ff0000000000000:7fefffffffffffff\n" +
                        "7fefffffffffffff:bff0000000000000:7fefffffffffffff\n" +
                        "7fefffffffffffff:4045000000000000:7fefffffffffffff\n" +
                        "7fefffffffffffff:c045000000000000:7fefffffffffffff\n" +
                        "7fefffffffffffff:4589d971e0000000:7fefffffffffffff\n" +
                        "7fefffffffffffff:c589d971e0000000:7fefffffffffffff\n" +
                        "0000000000000000:7fefffffffffffff:ffefffffffffffff\n" +
                        "3ff0000000000000:7fefffffffffffff:ffefffffffffffff\n" +
                        "bff0000000000000:7fefffffffffffff:ffefffffffffffff\n" +
                        "4045000000000000:7fefffffffffffff:ffefffffffffffff\n" +
                        "c045000000000000:7fefffffffffffff:ffefffffffffffff\n" +
                        "4589d971e0000000:7fefffffffffffff:ffefffffffffffff\n" +
                        "c589d971e0000000:7fefffffffffffff:ffefffffffffffff\n" +
                        "7fefffffffffffff:7fefffffffffffff:0000000000000000\n" +
                        "7fefffffffffffff:ffefffffffffffff:7ff0000000000000\n" +
                        "7fefffffffffffff:0010000000000000:7fefffffffffffff\n" +
                        "7fefffffffffffff:8010000000000000:7fefffffffffffff\n" +
                        "ffefffffffffffff:0000000000000000:ffefffffffffffff\n" +
                        "ffefffffffffffff:3ff0000000000000:ffefffffffffffff\n" +
                        "ffefffffffffffff:bff0000000000000:ffefffffffffffff\n" +
                        "ffefffffffffffff:4045000000000000:ffefffffffffffff\n" +
                        "ffefffffffffffff:c045000000000000:ffefffffffffffff\n" +
                        "ffefffffffffffff:4589d971e0000000:ffefffffffffffff\n" +
                        "ffefffffffffffff:c589d971e0000000:ffefffffffffffff\n" +
                        "0000000000000000:ffefffffffffffff:7fefffffffffffff\n" +
                        "3ff0000000000000:ffefffffffffffff:7fefffffffffffff\n" +
                        "bff0000000000000:ffefffffffffffff:7fefffffffffffff\n" +
                        "4045000000000000:ffefffffffffffff:7fefffffffffffff\n" +
                        "c045000000000000:ffefffffffffffff:7fefffffffffffff\n" +
                        "4589d971e0000000:ffefffffffffffff:7fefffffffffffff\n" +
                        "c589d971e0000000:ffefffffffffffff:7fefffffffffffff\n" +
                        "ffefffffffffffff:7fefffffffffffff:fff0000000000000\n" +
                        "ffefffffffffffff:ffefffffffffffff:0000000000000000\n" +
                        "ffefffffffffffff:0010000000000000:ffefffffffffffff\n" +
                        "ffefffffffffffff:8010000000000000:ffefffffffffffff\n" +
                        "0010000000000000:0000000000000000:0010000000000000\n" +
                        "0010000000000000:3ff0000000000000:bff0000000000000\n" +
                        "0010000000000000:bff0000000000000:3ff0000000000000\n" +
                        "0010000000000000:4045000000000000:c045000000000000\n" +
                        "0010000000000000:c045000000000000:4045000000000000\n" +
                        "0010000000000000:4589d971e0000000:c589d971e0000000\n" +
                        "0010000000000000:c589d971e0000000:4589d971e0000000\n" +
                        "0000000000000000:0010000000000000:8010000000000000\n" +
                        "3ff0000000000000:0010000000000000:3ff0000000000000\n" +
                        "bff0000000000000:0010000000000000:bff0000000000000\n" +
                        "4045000000000000:0010000000000000:4045000000000000\n" +
                        "c045000000000000:0010000000000000:c045000000000000\n" +
                        "4589d971e0000000:0010000000000000:4589d971e0000000\n" +
                        "c589d971e0000000:0010000000000000:c589d971e0000000\n" +
                        "0010000000000000:0010000000000000:0000000000000000\n" +
                        "0010000000000000:8010000000000000:0020000000000000\n" +
                        "0010000000000000:0010000000000000:0000000000000000\n" +
                        "0010000000000000:8010000000000000:0020000000000000\n" +
                        "8010000000000000:0000000000000000:8010000000000000\n" +
                        "8010000000000000:3ff0000000000000:bff0000000000000\n" +
                        "8010000000000000:bff0000000000000:3ff0000000000000\n" +
                        "8010000000000000:4045000000000000:c045000000000000\n" +
                        "8010000000000000:c045000000000000:4045000000000000\n" +
                        "8010000000000000:4589d971e0000000:c589d971e0000000\n" +
                        "8010000000000000:c589d971e0000000:4589d971e0000000\n" +
                        "0000000000000000:8010000000000000:0010000000000000\n" +
                        "3ff0000000000000:8010000000000000:3ff0000000000000\n" +
                        "bff0000000000000:8010000000000000:bff0000000000000\n" +
                        "4045000000000000:8010000000000000:4045000000000000\n" +
                        "c045000000000000:8010000000000000:c045000000000000\n" +
                        "4589d971e0000000:8010000000000000:4589d971e0000000\n" +
                        "c589d971e0000000:8010000000000000:c589d971e0000000\n" +
                        "8010000000000000:0010000000000000:8020000000000000\n" +
                        "8010000000000000:8010000000000000:0000000000000000\n" +
                        "8010000000000000:7fefffffffffffff:ffefffffffffffff\n" +
                        "8010000000000000:ffefffffffffffff:7fefffffffffffff\n";
        TestRunner.run("double-sub.elf", new String[0], "", stdout, "", 0);
    }

    @Test
    public void printf() throws Exception {
        String stdout = "values[0] = 0.000000\n" +
                        "values[1] = 1.000000\n" +
                        "values[2] = 3.141593\n" +
                        "values[3] = 4.200000\n" +
                        "values[4] = 8.920000\n" +
                        "values[5] = 10.000000\n" +
                        "values[6] = 12.240000\n" +
                        "values[7] = 25.570000\n" +
                        "values[8] = 1997.977100\n";
        TestRunner.run("float-printf.elf", new String[0], "", stdout, "", 0);
    }
}
