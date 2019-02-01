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
package org.graalvm.vm.memory.test.hardware;

import static org.graalvm.vm.memory.hardware.MMU.mmap;
import static org.graalvm.vm.memory.hardware.MMU.munmap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.graalvm.vm.memory.exception.SegmentationViolation;
import org.graalvm.vm.memory.hardware.NativeMemory;
import org.graalvm.vm.memory.hardware.NativeVirtualMemory;
import org.graalvm.vm.posix.api.PosixException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MMUTest {
    private NativeVirtualMemory mem;

    @BeforeClass
    public static void init() {
        try {
            TestOptions.setLibraryPath();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
        assertTrue(NativeVirtualMemory.isSupported());
    }

    @Before
    public void setup() {
        mem = new NativeVirtualMemory(NativeVirtualMemory.LOW, NativeVirtualMemory.HIGH, 0, NativeVirtualMemory.SIZE);
    }

    @Test
    public void testMmap1() throws PosixException {
        long ptr = mmap(0, 4096, true, true, false, false, true, false, -1, 0);
        assertTrue(ptr != 0);
        munmap(ptr, 4096);
    }

    @Test
    public void testMemoryAccess1() throws PosixException {
        long ptr = mmap(0, 4096, true, true, false, false, true, false, -1, 0);
        assertTrue(ptr != 0);
        try {
            NativeMemory.i8(ptr + 0, (byte) 'B');
            NativeMemory.i8(ptr + 1, (byte) 'E');
            NativeMemory.i8(ptr + 2, (byte) 'E');
            NativeMemory.i8(ptr + 3, (byte) 'F');
            int val = NativeMemory.i32B(ptr);
            assertEquals(0x42454546, val);
        } finally {
            munmap(ptr, 4096);
        }
    }

    @Test
    public void vm01() throws PosixException {
        long ptr = mmap(mem.getPhysicalLow(), 4096, true, true, false, true, true, false, -1, 0);
        try {
            mem.i8(0);
        } finally {
            munmap(ptr, 4096);
        }
    }

    @Test
    public void vmSegfault01() throws PosixException {
        long ptr = mmap(mem.getPhysicalLow(), 4096, true, true, false, true, true, false, -1, 0);
        try {
            mem.i8(4097);
            fail();
        } catch (SegmentationViolation e) {
            assertEquals(4097, e.getAddress());
        } finally {
            munmap(ptr, 4096);
        }
    }

    @Test
    public void segfault() {
        NativeMemory.i8(NativeVirtualMemory.LOW);
    }
}
