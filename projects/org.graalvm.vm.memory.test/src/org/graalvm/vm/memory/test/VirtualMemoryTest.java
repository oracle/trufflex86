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

import org.graalvm.vm.memory.ByteMemory;
import org.graalvm.vm.memory.JavaVirtualMemory;
import org.graalvm.vm.memory.Memory;
import org.graalvm.vm.memory.MemoryPage;
import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.posix.api.PosixException;
import org.junit.Before;
import org.junit.Test;

public class VirtualMemoryTest {
    private VirtualMemory vm;
    private MemoryPage page;
    private Memory mem;
    private byte[] bytes;

    @Before
    public void setup() {
        vm = new JavaVirtualMemory();
        bytes = new byte[32];
        mem = new ByteMemory(bytes);
        page = new MemoryPage(mem, 0, bytes.length);
        vm.add(page);
        vm.setBrk(32);
    }

    @Test
    public void compute001() {
        int val = 0xF0000021;
        long address = 0x00000000;
        vm.setI32(address, val);
        assertEquals(0xF0000021, vm.getI32(address));
    }

    @Test
    public void brk001() {
        long start = vm.brk();
        assertEquals(0x20, start);
        long brk = vm.brk(start + 0x10);
        assertEquals(0x30, brk);
        brk = vm.brk(brk + 0x10);
        assertEquals(0x40, brk);
        brk = vm.brk(brk + 0x10);
        assertEquals(0x50, brk);

        long address = start;
        for (int i = 0; i < 0x30; i++) {
            vm.setI8(address, (byte) i);
            address++;
        }

        address = start;
        for (int i = 0; i < 0x30; i++) {
            assertEquals((byte) i, vm.getI8(address));
            address++;
        }
    }

    @Test
    public void vm001() {
        vm.set32bit();
        Memory m = new ByteMemory(4096);
        m.setI32(0x42, 0xDEADBEEF);
        MemoryPage p = new MemoryPage(m, 0x80000000L, 4096);
        vm.add(p);
        assertEquals(0xDEADBEEF, vm.getI32(0x80000042));
    }

    @Test
    public void vm002() {
        vm.set32bit();
        Memory m = new ByteMemory(8192);
        MemoryPage p = new MemoryPage(m, 0x80000000L, 8192);
        vm.add(p);
        vm.setI32(0x80000042, 0xDEADBEEF);
        assertEquals(0xDEADBEEF, m.getI32(0x42));
    }

    @Test
    public void vmsplit001() {
        vm.set32bit();
        Memory m1 = new ByteMemory(65536);
        Memory m2 = new ByteMemory(4096);
        MemoryPage p1 = new MemoryPage(m1, 0x80000000L, 65536);
        MemoryPage p2 = new MemoryPage(m2, 0x80001000L, 4096);
        vm.add(p1);
        vm.add(p2);
        vm.setI32(0x80000042, 0xDEADBEEF);
        vm.setI32(0x80001042, 0xC0DEBABE);
        vm.setI32(0x80002042, 0xCAFEBABE);
        assertEquals(0xDEADBEEF, m1.getI32(0x42));
        assertEquals(0xC0DEBABE, m2.getI32(0x42));
        assertEquals(0xCAFEBABE, m1.getI32(0x2042));
    }

    @Test
    public void unmap001() throws PosixException {
        vm.set64bit();
        Memory m = new ByteMemory(4190208);
        MemoryPage p = new MemoryPage(m, 0x7f0000e2a000L, 4190208);
        vm.add(p);

        vm.setI32(0x7f0000e2af00L, 0xDEADBEEF);
        assertEquals(0xDEADBEEF, m.getI32(0xf00));

        vm.remove(0x00007f0000e2a000L, 1925120);

        vm.setI32(0x7f0001000000L, 0xDEADBEEF);
        assertEquals(0xDEADBEEF, m.getI32(0x1d6000));

        vm.remove(0x7f0001200000L, 167936);

        vm.setI32(0x7f0001000010L, 0xC0DEBABE);
        assertEquals(0xDEADBEEF, m.getI32(0x1d6000));

        vm.setI32(0x7f0001000000L, 0xDEADCAFE);
        assertEquals(0xDEADCAFE, m.getI32(0x1d6000));

        assertEquals(0xC0DEBABE, m.getI32(0x1d6010));
    }
}
