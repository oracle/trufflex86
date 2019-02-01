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

import java.nio.file.Paths;

import org.junit.Test;

public class SulongAsm {
    @Test
    public void adc001() throws Exception {
        String stdout = "00000000:00000000:0:00000000:0:0\n" +
                        "00000000:00000000:1:00000001:0:0\n" +
                        "00000d0c:00000000:1:00000d0d:0:0\n" +
                        "00000d0c:00000d0c:1:00001a19:0:0\n" +
                        "00000000:00000d0c:1:00000d0d:0:0\n" +
                        "00000d0c:00000000:0:00000d0c:0:0\n" +
                        "00000d0c:00000d0c:0:00001a18:0:0\n" +
                        "00000000:00000d0c:0:00000d0c:0:0\n" +
                        "ffffffff:00000000:0:ffffffff:0:0\n" +
                        "ffffffff:00000001:0:00000000:1:0\n" +
                        "ffffffff:00000d0c:0:00000d0b:1:0\n" +
                        "ffffffff:80000000:0:7fffffff:1:1\n" +
                        "ffffffff:ffffffff:0:fffffffe:1:0\n" +
                        "ffffffff:00000000:1:00000000:1:0\n" +
                        "ffffffff:00000001:1:00000001:1:0\n" +
                        "ffffffff:00000d0c:1:00000d0c:1:0\n" +
                        "ffffffff:80000000:1:80000000:1:0\n" +
                        "ffffffff:ffffffff:1:ffffffff:1:0\n" +
                        "80000000:00000000:0:80000000:0:0\n" +
                        "80000000:00000d0c:0:80000d0c:0:0\n" +
                        "80000000:80000000:0:00000000:1:1\n" +
                        "80000000:ffffffff:0:7fffffff:1:1\n" +
                        "80000000:00000000:1:80000001:0:0\n" +
                        "80000000:00000d0c:1:80000d0d:0:0\n" +
                        "80000000:80000000:1:00000001:1:1\n" +
                        "80000000:ffffffff:1:80000000:1:0\n";
        TestRunner.run("adc001.elf", new String[0], "", stdout, "", 0);
    }

    @Test
    public void add001() throws Exception {
        String stdout = "00000000:00000000:00000000:0:0\n" +
                        "00000000:00000d0c:00000d0c:0:0\n" +
                        "00000d0c:00000000:00000d0c:0:0\n" +
                        "00000d0c:00000d0c:00001a18:0:0\n" +
                        "ffffffff:00000000:ffffffff:0:0\n" +
                        "ffffffff:00000001:00000000:1:0\n" +
                        "ffffffff:00000d0c:00000d0b:1:0\n" +
                        "ffffffff:80000000:7fffffff:1:1\n" +
                        "ffffffff:ffffffff:fffffffe:1:0\n" +
                        "80000000:00000000:80000000:0:0\n" +
                        "80000000:00000d0c:80000d0c:0:0\n" +
                        "80000000:80000000:00000000:1:1\n" +
                        "80000000:ffffffff:7fffffff:1:1\n";
        TestRunner.run("add001.elf", new String[0], "", stdout, "", 0);
    }

    @Test
    public void sbb001() throws Exception {
        String stdout = "00000000:00000000:0:00000000:0\n" +
                        "00000000:00000000:1:ffffffff:1\n" +
                        "00000d0c:00000000:1:fffff2f3:1\n" +
                        "00000d0c:00000d0c:1:ffffffff:1\n" +
                        "00000000:00000d0c:1:00000d0b:0\n" +
                        "00000d0c:00000000:0:fffff2f4:1\n" +
                        "00000d0c:00000d0c:0:00000000:0\n" +
                        "00000000:00000d0c:0:00000d0c:0\n" +
                        "ffffffff:00000000:0:00000001:1\n" +
                        "ffffffff:00000d0c:0:00000d0d:1\n" +
                        "ffffffff:80000000:0:80000001:1\n" +
                        "ffffffff:ffffffff:0:00000000:0\n" +
                        "ffffffff:00000000:1:00000000:1\n" +
                        "ffffffff:00000d0c:1:00000d0c:1\n" +
                        "ffffffff:80000000:1:80000000:1\n" +
                        "ffffffff:ffffffff:1:ffffffff:1\n" +
                        "80000000:00000000:0:80000000:1\n" +
                        "80000000:00000d0c:0:80000d0c:1\n" +
                        "80000000:80000000:0:00000000:0\n" +
                        "80000000:ffffffff:0:7fffffff:0\n" +
                        "80000000:00000000:1:7fffffff:1\n" +
                        "80000000:00000d0c:1:80000d0b:1\n" +
                        "80000000:80000000:1:ffffffff:1\n" +
                        "80000000:ffffffff:1:7ffffffe:0\n";
        TestRunner.run("sbc001.elf", new String[0], "", stdout, "", 0);
    }

    @Test
    public void sub001() throws Exception {
        String stdout = "00000000:00000000:00000000:0:0:1\n" +
                        "00000000:00000d0c:00000d0c:0:0:0\n" +
                        "00000d0c:00000000:fffff2f4:1:0:0\n" +
                        "00000d0c:00000d0c:00000000:0:0:1\n" +
                        "ffffffff:00000000:00000001:1:0:0\n" +
                        "ffffffff:00000001:00000002:1:0:0\n" +
                        "ffffffff:00000d0c:00000d0d:1:0:0\n" +
                        "ffffffff:80000000:80000001:1:0:0\n" +
                        "ffffffff:ffffffff:00000000:0:0:1\n" +
                        "80000000:00000000:80000000:1:1:0\n" +
                        "80000000:00000d0c:80000d0c:1:1:0\n" +
                        "80000000:80000000:00000000:0:0:1\n" +
                        "80000000:ffffffff:7fffffff:0:0:0\n";
        TestRunner.run("sub001.elf", new String[0], "", stdout, "", 0);
    }

    @Test
    public void cmp001() throws Exception {
        TestRunner.run("cmp001.elf", new String[0], "", "80\n", "", 0);
    }

    @Test
    public void bsf001() throws Exception {
        TestRunner.run("bsf001.elf", new String[0], "", "", "", 1);
    }

    @Test
    public void bsf002() throws Exception {
        TestRunner.run("bsf002.elf", new String[0], "", "", "", 1);
    }

    @Test
    public void bsf003() throws Exception {
        TestRunner.run("bsf003.elf", new String[0], "", "", "", 1);
    }

    @Test
    public void bsr001() throws Exception {
        TestRunner.run("bsr001.elf", new String[0], "", "", "", 1);
    }

    @Test
    public void bsr002() throws Exception {
        TestRunner.run("bsr002.elf", new String[0], "", "", "", 1);
    }

    @Test
    public void bsr003() throws Exception {
        TestRunner.run("bsr003.elf", new String[0], "", "", "", 1);
    }

    @Test
    public void bswap001() throws Exception {
        TestRunner.run("bswap001.elf", new String[0], "", "", "", 1);
    }

    @Test
    public void bswap002() throws Exception {
        TestRunner.run("bswap002.elf", new String[0], "", "", "", 1);
    }

    @Test
    public void cpuid002() throws Exception {
        TestRunner.run("cpuid002.elf", new String[0], "", "'VMX86onGraal'\n", "", 0);
    }

    @Test
    public void lea001() throws Exception {
        TestRunner.run("lea001.elf", new String[0], "", "", "", 1);
    }

    @Test
    public void push001() throws Exception {
        TestRunner.run("push001.elf", new String[0], "", "", "", 1);
    }

    @Test
    public void push002() throws Exception {
        TestRunner.run("push002.elf", new String[0], "", "", "", 1);
    }

    @Test
    public void stosb001() throws Exception {
        TestRunner.run("stosb001.elf", new String[0], "", "", "", 1);
    }

    @Test
    public void stosb002() throws Exception {
        TestRunner.run("stosb002.elf", new String[0], "", "buf: CC CC 42 42 42 42 42 42 42 42 42 42 CC CC CC CC\n", "", 1);
    }

    @Test
    public void seto001() throws Exception {
        TestRunner.run("seto001.elf", new String[0], "", "", "", 0);
    }

    @Test
    public void seto002() throws Exception {
        TestRunner.run("seto002.elf", new String[0], "", "", "", 0);
    }

    @Test
    public void seto003() throws Exception {
        TestRunner.run("seto003.elf", new String[0], "", "", "", 0);
    }

    @Test
    public void seto004() throws Exception {
        TestRunner.run("seto004.elf", new String[0], "", "", "", 0);
    }

    @Test
    public void seto005() throws Exception {
        TestRunner.run("seto005.elf", new String[0], "", "", "", 0);
    }

    @Test
    public void seto006() throws Exception {
        TestRunner.run("seto006.elf", new String[0], "", "", "", 0);
    }

    @Test
    public void seto007() throws Exception {
        TestRunner.run("seto007.elf", new String[0], "", "", "", 0);
    }

    @Test
    public void seto008() throws Exception {
        TestRunner.run("seto008.elf", new String[0], "", "", "", 0);
    }

    @Test
    public void seto009() throws Exception {
        TestRunner.run("seto009.elf", new String[0], "", "", "", 1);
    }

    @Test
    public void seto010() throws Exception {
        TestRunner.run("seto010.elf", new String[0], "", "", "", 1);
    }

    @Test
    public void qemuBsx() throws Exception {
        String stdout = "bsrw       A=0000000000000000 R=0000000012345678 1\n" +
                        "bsrw       A=0000000012340128 R=0000000012340008 0\n" +
                        "bsfw       A=0000000000000000 R=0000000012345678 1\n" +
                        "bsfw       A=0000000012340128 R=0000000012340003 0\n" +
                        "bsrl       A=0000000000000000 R=0000000012345678 1\n" +
                        "bsrl       A=0000000000340128 R=0000000000000015 0\n" +
                        "bsfl       A=0000000000000000 R=0000000012345678 1\n" +
                        "bsfl       A=0000000000340128 R=0000000000000003 0\n" +
                        "bsrq       A=0000000000000000 R=0000000012345678 1\n" +
                        "bsrq       A=0000003401281234 R=0000000000000025 0\n" +
                        "bsfq       A=0000000000000000 R=0000000012345678 1\n" +
                        "bsfq       A=0000003401281234 R=0000000000000002 0\n";
        TestRunner.run("qemu-bsx.elf", new String[0], "", stdout, "", 0);
    }

    @Test
    public void qemuLea() throws Exception {
        String stdout = "lea 0x4000 = 0000000000004000\n" +
                        "lea (%%eax) = 0000000000000001\n" +
                        "lea (%%ebx) = 0000000000000002\n" +
                        "lea (%%ecx) = 0000000000000004\n" +
                        "lea (%%edx) = 0000000000000008\n" +
                        "lea (%%esi) = 0000000000000010\n" +
                        "lea (%%edi) = 0000000000000020\n" +
                        "lea 0x40(%%eax) = 0000000000000041\n" +
                        "lea 0x40(%%ebx) = 0000000000000042\n" +
                        "lea 0x40(%%ecx) = 0000000000000044\n" +
                        "lea 0x40(%%edx) = 0000000000000048\n" +
                        "lea 0x40(%%esi) = 0000000000000050\n" +
                        "lea 0x40(%%edi) = 0000000000000060\n" +
                        "lea 0x4000(%%eax) = 0000000000004001\n" +
                        "lea 0x4000(%%ebx) = 0000000000004002\n" +
                        "lea 0x4000(%%ecx) = 0000000000004004\n" +
                        "lea 0x4000(%%edx) = 0000000000004008\n" +
                        "lea 0x4000(%%esi) = 0000000000004010\n" +
                        "lea 0x4000(%%edi) = 0000000000004020\n" +
                        "lea (%%eax, %%ecx) = 0000000000000005\n" +
                        "lea (%%ebx, %%edx) = 000000000000000a\n" +
                        "lea (%%ecx, %%ecx) = 0000000000000008\n" +
                        "lea (%%edx, %%ecx) = 000000000000000c\n" +
                        "lea (%%esi, %%ecx) = 0000000000000014\n" +
                        "lea (%%edi, %%ecx) = 0000000000000024\n" +
                        "lea 0x40(%%eax, %%ecx) = 0000000000000045\n" +
                        "lea 0x4000(%%ebx, %%edx) = 000000000000400a\n" +
                        "lea (%%ecx, %%ecx, 2) = 000000000000000c\n" +
                        "lea (%%edx, %%ecx, 4) = 0000000000000018\n" +
                        "lea (%%esi, %%ecx, 8) = 0000000000000030\n" +
                        "lea (,%%eax, 2) = 0000000000000002\n" +
                        "lea (,%%ebx, 4) = 0000000000000008\n" +
                        "lea (,%%ecx, 8) = 0000000000000020\n" +
                        "lea 0x40(,%%eax, 2) = 0000000000000042\n" +
                        "lea 0x40(,%%ebx, 4) = 0000000000000048\n" +
                        "lea 0x40(,%%ecx, 8) = 0000000000000060\n" +
                        "lea -10(%%ecx, %%ecx, 2) = 0000000000000002\n" +
                        "lea -10(%%edx, %%ecx, 4) = 000000000000000e\n" +
                        "lea -10(%%esi, %%ecx, 8) = 0000000000000026\n" +
                        "lea 0x4000(%%ecx, %%ecx, 2) = 000000000000400c\n" +
                        "lea 0x4000(%%edx, %%ecx, 4) = 0000000000004018\n" +
                        "lea 0x4000(%%esi, %%ecx, 8) = 0000000000004030\n" +
                        "lea 0x4000 = 0000000000004000\n" +
                        "lea 0x4000(%%rip) = 0000000000405cfa\n" +
                        "lea (%%rax) = 0000abcc00000001\n" +
                        "lea (%%rbx) = 0000abcf00000002\n" +
                        "lea (%%rcx) = 0000abc900000004\n" +
                        "lea (%%rdx) = 0000abc500000008\n" +
                        "lea (%%rsi) = 0000abdd00000010\n" +
                        "lea (%%rdi) = 0000abed00000020\n" +
                        "lea 0x40(%%rax) = 0000abcc00000041\n" +
                        "lea 0x40(%%rbx) = 0000abcf00000042\n" +
                        "lea 0x40(%%rcx) = 0000abc900000044\n" +
                        "lea 0x40(%%rdx) = 0000abc500000048\n" +
                        "lea 0x40(%%rsi) = 0000abdd00000050\n" +
                        "lea 0x40(%%rdi) = 0000abed00000060\n" +
                        "lea 0x4000(%%rax) = 0000abcc00004001\n" +
                        "lea 0x4000(%%rbx) = 0000abcf00004002\n" +
                        "lea 0x4000(%%rcx) = 0000abc900004004\n" +
                        "lea 0x4000(%%rdx) = 0000abc500004008\n" +
                        "lea 0x4000(%%rsi) = 0000abdd00004010\n" +
                        "lea 0x4000(%%rdi) = 0000abed00004020\n" +
                        "lea (%%rax, %%rcx) = 0001579500000005\n" +
                        "lea (%%rbx, %%rdx) = 000157940000000a\n" +
                        "lea (%%rcx, %%rcx) = 0001579200000008\n" +
                        "lea (%%rdx, %%rcx) = 0001578e0000000c\n" +
                        "lea (%%rsi, %%rcx) = 000157a600000014\n" +
                        "lea (%%rdi, %%rcx) = 000157b600000024\n" +
                        "lea 0x40(%%rax, %%rcx) = 0001579500000045\n" +
                        "lea 0x4000(%%rbx, %%rdx) = 000157940000400a\n" +
                        "lea (%%rcx, %%rcx, 2) = 0002035b0000000c\n" +
                        "lea (%%rdx, %%rcx, 4) = 00035ae900000018\n" +
                        "lea (%%rsi, %%rcx, 8) = 00060a2500000030\n" +
                        "lea (,%%rax, 2) = 0001579800000002\n" +
                        "lea (,%%rbx, 4) = 0002af3c00000008\n" +
                        "lea (,%%rcx, 8) = 00055e4800000020\n" +
                        "lea 0x40(,%%rax, 2) = 0001579800000042\n" +
                        "lea 0x40(,%%rbx, 4) = 0002af3c00000048\n" +
                        "lea 0x40(,%%rcx, 8) = 00055e4800000060\n" +
                        "lea -10(%%rcx, %%rcx, 2) = 0002035b00000002\n" +
                        "lea -10(%%rdx, %%rcx, 4) = 00035ae90000000e\n" +
                        "lea -10(%%rsi, %%rcx, 8) = 00060a2500000026\n" +
                        "lea 0x4000(%%rcx, %%rcx, 2) = 0002035b0000400c\n" +
                        "lea 0x4000(%%rdx, %%rcx, 4) = 00035ae900004018\n" +
                        "lea 0x4000(%%rsi, %%rcx, 8) = 00060a2500004030\n";
        TestRunner.run("qemu-lea.elf", new String[0], "", stdout, "", 0);
    }

    @Test
    public void qemuMuldiv() throws Exception {
        String stdout = "imulb      A=000000001234561d B=0000000000000004 R=0000000012340074 CC=0004\n" +
                        "imulb      A=0000000000000003 B=fffffffffffffffc R=000000000000fff4 CC=0080\n" +
                        "imulb      A=0000000000000080 B=0000000000000080 R=0000000000004000 CC=0805\n" +
                        "imulb      A=0000000000000010 B=0000000000000010 R=0000000000000100 CC=0805\n" +
                        "imulw      AH=0000000000000000 AL=000000001234001d B=000000000000002d RH=0000000000000000 RL=0000000012340519 CC=0000\n" +
                        "imulw      AH=0000000000000000 AL=0000000000000017 B=ffffffffffffffd3 RH=000000000000ffff RL=000000000000fbf5 CC=0084\n" +
                        "imulw      AH=0000000000000000 AL=0000000000008000 B=0000000000008000 RH=0000000000004000 RL=0000000000000000 CC=0805\n" +
                        "imulw      AH=0000000000000000 AL=0000000000000100 B=0000000000000100 RH=0000000000000001 RL=0000000000000000 CC=0805\n" +
                        "imull      AH=0000000000000000 AL=000000001234001d B=000000000000002d RH=0000000000000003 RL=0000000033240519 CC=0801\n" +
                        "imull      AH=0000000000000000 AL=0000000000000017 B=ffffffffffffffd3 RH=00000000ffffffff RL=00000000fffffbf5 CC=0084\n" +
                        "imull      AH=0000000000000000 AL=0000000080000000 B=0000000080000000 RH=0000000040000000 RL=0000000000000000 CC=0805\n" +
                        "imull      AH=0000000000000000 AL=0000000000010000 B=0000000000010000 RH=0000000000000001 RL=0000000000000000 CC=0805\n" +
                        "mulb       A=000000001234561d B=0000000000000004 R=0000000012340074 CC=0004\n" +
                        "mulb       A=0000000000000003 B=fffffffffffffffc R=00000000000002f4 CC=0881\n" +
                        "mulb       A=0000000000000080 B=0000000000000080 R=0000000000004000 CC=0805\n" +
                        "mulb       A=0000000000000010 B=0000000000000010 R=0000000000000100 CC=0805\n" +
                        "mulw       AH=0000000000000000 AL=000000001234001d B=000000000000002d RH=0000000000000000 RL=0000000012340519 CC=0000\n" +
                        "mulw       AH=0000000000000000 AL=0000000000000017 B=ffffffffffffffd3 RH=0000000000000016 RL=000000000000fbf5 CC=0885\n" +
                        "mulw       AH=0000000000000000 AL=0000000000008000 B=0000000000008000 RH=0000000000004000 RL=0000000000000000 CC=0805\n" +
                        "mulw       AH=0000000000000000 AL=0000000000000100 B=0000000000000100 RH=0000000000000001 RL=0000000000000000 CC=0805\n" +
                        "mull       AH=0000000000000000 AL=000000001234001d B=000000000000002d RH=0000000000000003 RL=0000000033240519 CC=0801\n" +
                        "mull       AH=0000000000000000 AL=0000000000000017 B=ffffffffffffffd3 RH=0000000000000016 RL=00000000fffffbf5 CC=0885\n" +
                        "mull       AH=0000000000000000 AL=0000000080000000 B=0000000080000000 RH=0000000040000000 RL=0000000000000000 CC=0805\n" +
                        "mull       AH=0000000000000000 AL=0000000000010000 B=0000000000010000 RH=0000000000000001 RL=0000000000000000 CC=0805\n" +
                        "imulw      A=000000001234001d B=000000000000002d R=0000000012340519 CC=0000\n" +
                        "imulw      A=0000000000000017 B=ffffffffffffffd3 R=000000000000fbf5 CC=0084\n" +
                        "imulw      A=0000000000008000 B=0000000000008000 R=0000000000000000 CC=0805\n" +
                        "imulw      A=0000000000000100 B=0000000000000100 R=0000000000000000 CC=0805\n" +
                        "imull      A=000000001234001d B=000000000000002d R=0000000033240519 CC=0801\n" +
                        "imull      A=0000000000000017 B=ffffffffffffffd3 R=00000000fffffbf5 CC=0084\n" +
                        "imull      A=0000000080000000 B=0000000080000000 R=0000000000000000 CC=0805\n" +
                        "imull      A=0000000000010000 B=0000000000010000 R=0000000000000000 CC=0805\n" +
                        "imulw im   A=000000000000002d B=0000000000001234 R=0000000000003324 CC=0000\n" +
                        "imulw im   A=ffffffffffffffd3 B=0000000000000017 R=000000000000fbf5 CC=0000\n" +
                        "imulw im   A=0000000000008000 B=0000000080000000 R=0000000000000000 CC=0000\n" +
                        "imulw im   A=0000000000007fff B=0000000000001000 R=000000000000f000 CC=0000\n" +
                        "imull im   A=000000000000002d B=0000000000001234 R=0000000000033324 CC=0000\n" +
                        "imull im   A=ffffffffffffffd3 B=0000000000000017 R=00000000fffffbf5 CC=0084\n" +
                        "imull im   A=0000000000008000 B=0000000080000000 R=0000000000000000 CC=0805\n" +
                        "imull im   A=0000000000007fff B=0000000000001000 R=0000000007fff000 CC=0004\n" +
                        "idivb      A=0000000012341678 B=000000000000127e R=000000001234522d CC=0000\n" +
                        "idivb      A=0000000043210123 B=fffffffffffffffb R=00000000432101c6 CC=0000\n" +
                        "idivb      A=0000000012340004 B=ffffffffffffffff R=00000000123400fc CC=0000\n" +
                        "idivw      AH=0000000000000000 AL=0000000012345678 B=000000000000303b RH=000000000000263d RL=0000000012340001 CC=0000\n" +
                        "idivw      AH=0000000000000000 AL=ffffffffffffa549 B=ffffffffffffffd3 RH=000000000000000d RL=fffffffffffffc54 CC=0000\n" +
                        "idivw      AH=0000000000000000 AL=0000000012348000 B=ffffffffffffffff RH=0000000000000000 RL=0000000012348000 CC=0000\n" +
                        "idivw      AH=0000000000012343 AL=0000000012345678 B=0000000081238567 RH=00000000000120a6 RL=000000001234b65e CC=0000\n" +
                        "idivl      AH=0000000000000000 AL=0000000012345678 B=000000000000303b RH=0000000000001198 RL=00000000000060a0 CC=0000\n" +
                        "idivl      AH=0000000000000000 AL=fffffffffffc70f9 B=ffffffffffffffd3 RH=000000000000002b RL=00000000fa4fb93a CC=0000\n" +
                        "idivl      AH=0000000000000000 AL=0000000080000000 B=ffffffffffffffff RH=0000000000000000 RL=0000000080000000 CC=0000\n" +
                        "idivl      AH=0000000000012343 AL=0000000012345678 B=0000000081234567 RH=000000004ba84b51 RL=00000000fffdb441 CC=0000\n" +
                        "divb       A=0000000012341678 B=000000000000127e R=000000001234522d CC=0000\n" +
                        "divb       A=0000000043210123 B=fffffffffffffffb R=0000000043212801 CC=0000\n" +
                        "divb       A=0000000012340004 B=ffffffffffffffff R=0000000012340400 CC=0000\n" +
                        "divw       AH=0000000000000000 AL=0000000012345678 B=000000000000303b RH=000000000000263d RL=0000000012340001 CC=0000\n" +
                        "divw       AH=0000000000000000 AL=ffffffffffffa549 B=ffffffffffffffd3 RH=000000000000a549 RL=ffffffffffff0000 CC=0000\n" +
                        "divw       AH=0000000000000000 AL=0000000012348000 B=ffffffffffffffff RH=0000000000008000 RL=0000000012340000 CC=0000\n" +
                        "divw       AH=0000000000012343 AL=0000000012345678 B=0000000081238567 RH=00000000000145ab RL=00000000123443ab CC=0000\n" +
                        "divl       AH=0000000000000000 AL=0000000012345678 B=000000000000303b RH=0000000000001198 RL=00000000000060a0 CC=0000\n" +
                        "divl       AH=0000000000000000 AL=fffffffffffc70f9 B=ffffffffffffffd3 RH=00000000fffc70f9 RL=0000000000000000 CC=0000\n" +
                        "divl       AH=0000000000000000 AL=0000000080000000 B=ffffffffffffffff RH=0000000080000000 RL=0000000000000000 CC=0000\n" +
                        "divl       AH=0000000000012343 AL=0000000012345678 B=0000000081234567 RH=000000002100133c RL=0000000000024164 CC=0000\n" +
                        "imulq      AH=0000000000000000 AL=1234001d1234001d B=000000000000002d RH=0000000000000003 RL=3324051c33240519 CC=0801\n" +
                        "imulq      AH=0000000000000000 AL=0000000000000017 B=ffffffffffffffd3 RH=ffffffffffffffff RL=fffffffffffffbf5 CC=0084\n" +
                        "imulq      AH=0000000000000000 AL=8000000000000000 B=8000000000000000 RH=4000000000000000 RL=0000000000000000 CC=0805\n" +
                        "imulq      AH=0000000000000000 AL=0000000100000000 B=0000000100000000 RH=0000000000000001 RL=0000000000000000 CC=0805\n" +
                        "mulq       AH=0000000000000000 AL=1234001d1234001d B=000000000000002d RH=0000000000000003 RL=3324051c33240519 CC=0801\n" +
                        "mulq       AH=0000000000000000 AL=0000000000000017 B=ffffffffffffffd3 RH=0000000000000016 RL=fffffffffffffbf5 CC=0885\n" +
                        "mulq       AH=0000000000000000 AL=8000000000000000 B=8000000000000000 RH=4000000000000000 RL=0000000000000000 CC=0805\n" +
                        "mulq       AH=0000000000000000 AL=0000000100000000 B=0000000100000000 RH=0000000000000001 RL=0000000000000000 CC=0805\n" +
                        "imulq      A=1234001d1234001d B=000000000000002d R=3324051c33240519 CC=0801\n" +
                        "imulq      A=0000000000000017 B=ffffffffffffffd3 R=fffffffffffffbf5 CC=0084\n" +
                        "imulq      A=8000000000000000 B=8000000000000000 R=0000000000000000 CC=0805\n" +
                        "imulq      A=0000000100000000 B=0000000100000000 R=0000000000000000 CC=0805\n" +
                        "imulq im   A=000000000000002d B=0000000012341234 R=0000000333273324 CC=0004\n" +
                        "imulq im   A=ffffffffffffffd3 B=0000000000000017 R=fffffffffffffbf5 CC=0084\n" +
                        "imulq im   A=0000000000008000 B=8000000000000000 R=0000000000000000 CC=0805\n" +
                        "imulq im   A=0000000000007fff B=0000000010000000 R=000007fff0000000 CC=0004\n" +
                        "idivq      AH=0000000000000000 AL=0012345678abcdef B=000000000000303b RH=000000000000057b RL=00000060a05d661c CC=0000\n" +
                        "idivq      AH=0000000000000000 AL=fffffffffffc70f9 B=ffffffffffffffd3 RH=000000000000001c RL=fa4fa4fa4fa50e8f CC=0000\n" +
                        "idivq      AH=0000000000000000 AL=8000000000000000 B=ffffffffffffffff RH=0000000000000000 RL=8000000000000000 CC=0000\n" +
                        "idivq      AH=0000000000012343 AL=0000000012345678 B=0000000081234567 RH=000000005ed95edf RL=000241641d54baff CC=0000\n" +
                        "divq       AH=0000000000000000 AL=0012345678abcdef B=000000000000303b RH=000000000000057b RL=00000060a05d661c CC=0000\n" +
                        "divq       AH=0000000000000000 AL=fffffffffffc70f9 B=ffffffffffffffd3 RH=fffffffffffc70f9 RL=0000000000000000 CC=0000\n" +
                        "divq       AH=0000000000000000 AL=8000000000000000 B=ffffffffffffffff RH=8000000000000000 RL=0000000000000000 CC=0000\n" +
                        "divq       AH=0000000000012343 AL=0000000012345678 B=0000000081234567 RH=000000005ed95edf RL=000241641d54baff CC=0000\n";
        TestRunner.run("qemu-muldiv.elf", new String[0], "", stdout, "", 0);
    }

    @Test
    public void qemuMul() throws Exception {
        TestRunner.run("qemu-mul.elf", new String[0], "", "", "", 0);
    }

    @Test
    public void syscall001() throws Exception {
        TestRunner.run("syscall001.elf", new String[0], "", "Hello world!\n", "", 42);
    }

    @Test
    public void syscall002() throws Exception {
        TestRunner.run("syscall002.elf", new String[0], "", "Hello world!\n", "", 42);
    }

    @Test
    public void syscall_getuid001() throws Exception {
        TestRunner.run("syscall-getuid001.elf", new String[0], "", "uid: 1000\n", "", 0);
    }

    @Test
    public void syscall_getgid001() throws Exception {
        TestRunner.run("syscall-getgid001.elf", new String[0], "", "gid: 1000\n", "", 0);
    }

    @Test
    public void syscall_write001() throws Exception {
        TestRunner.run("syscall-write001.elf", new String[0], "", "Hello world!\n", "", 0);
    }

    @Test
    public void syscall_writev001() throws Exception {
        TestRunner.run("syscall-writev001.elf", new String[0], "", "hello world\nwritten: 12\n", "", 0);
    }

    @Test
    public void syscall_writev002() throws Exception {
        TestRunner.run("syscall-writev002.elf", new String[0], "", "written: -1\nerrno: 22\n", "", 0);
    }

    @Test
    public void syscall_getcwd001() throws Exception {
        String cwd = Paths.get(".").toAbsolutePath().normalize().toString();
        String stdout = "len: " + (cwd.length() + 1) + "\nvalue: '" + cwd + "'\n";
        TestRunner.run("syscall-getcwd001.elf", new String[0], "", stdout, "", 0);
    }

    @Test
    public void xadd001() throws Exception {
        TestRunner.run("xadd001.elf", new String[0], "", "", "", 1);
    }

    @Test
    public void xadd002() throws Exception {
        TestRunner.run("xadd002.elf", new String[0], "", "", "", 1);
    }

    @Test
    public void xadd003() throws Exception {
        TestRunner.run("xadd003.elf", new String[0], "", "", "", 1);
    }

    @Test
    public void xadd004() throws Exception {
        TestRunner.run("xadd004.elf", new String[0], "", "", "", 1);
    }

    @Test
    public void xchg001() throws Exception {
        TestRunner.run("xchg001.elf", new String[0], "", "", "", 1);
    }

    @Test
    public void xchg002() throws Exception {
        TestRunner.run("xchg002.elf", new String[0], "", "", "", 1);
    }

    @Test
    public void xchg003() throws Exception {
        TestRunner.run("xchg003.elf", new String[0], "", "", "", 1);
    }

    @Test
    public void xchg004() throws Exception {
        TestRunner.run("xchg004.elf", new String[0], "", "", "", 1);
    }

    @Test
    public void pmovmskb() throws Exception {
        TestRunner.run("pmovmskb.elf", new String[0], "", "mask 0x96a5\n", "", 0);
    }
}
