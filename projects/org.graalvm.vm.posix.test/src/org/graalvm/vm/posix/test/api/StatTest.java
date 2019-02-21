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

import static org.junit.Assert.assertEquals;

import org.graalvm.vm.posix.api.BytePosixPointer;
import org.graalvm.vm.posix.api.Posix;
import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.posix.api.PosixPointer;
import org.graalvm.vm.posix.api.io.Stat;
import org.junit.Before;
import org.junit.Test;

public class StatTest {
    private Posix posix;

    @Before
    public void setup() {
        posix = new Posix();
    }

    @Test
    public void posixFstat001() throws PosixException {
        Stat stat = new Stat();
        posix.fstat(1, stat);
        assertEquals(0, stat.st_dev);
        assertEquals(0x2190, stat.st_mode);
    }

    @Test
    public void posixFstat002() throws PosixException {
        Stat stat = new Stat();
        posix.fstat(1, stat);
        byte[] memory = new byte[256];
        for (int i = 0; i < memory.length; i++) {
            memory[i] = 0x55;
        }
        PosixPointer ptr = new BytePosixPointer(memory);
        stat.write64(ptr);
        assertEquals(0, memory[0]);
        assertEquals(0, memory[1]);
        assertEquals(0, memory[2]);
        assertEquals(0, memory[3]);
        assertEquals(0, memory[4]);
        assertEquals(0, memory[5]);
        assertEquals(0, memory[6]);
        assertEquals(0, memory[7]);
    }

    @Test
    public void posixFstat003() throws PosixException {
        Stat stat = new Stat();
        posix.fstat(1, stat);
        byte[] memory = new byte[256];
        for (int i = 0; i < memory.length; i++) {
            memory[i] = 0x55;
        }
        PosixPointer ptr = new BytePosixPointer(memory, 16);
        stat.write64(ptr);
        assertEquals(0x55, memory[0]);
        assertEquals(0x55, memory[15]);
        assertEquals(0, memory[16]);
        assertEquals(0, memory[23]);
    }
}
