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
package org.graalvm.vm.memory;

import org.graalvm.vm.memory.exception.SegmentationViolation;
import org.graalvm.vm.posix.api.MemoryFaultException;
import org.graalvm.vm.posix.api.PosixPointer;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;

public class PosixVirtualMemoryPointer implements PosixPointer {
    private final VirtualMemory memory;
    private final long offset;

    public PosixVirtualMemoryPointer(VirtualMemory memory, long offset) {
        this.memory = memory;
        this.offset = offset;
    }

    @Override
    public long getAddress() {
        return offset;
    }

    @Override
    public PosixPointer add(int off) {
        return new PosixVirtualMemoryPointer(memory, offset + off);
    }

    @Override
    public byte getI8() throws MemoryFaultException {
        try {
            return memory.getI8(offset);
        } catch (SegmentationViolation e) {
            CompilerDirectives.transferToInterpreter();
            throw new MemoryFaultException(e);
        }
    }

    @Override
    public short getI16() throws MemoryFaultException {
        try {
            return memory.getI16(offset);
        } catch (SegmentationViolation e) {
            CompilerDirectives.transferToInterpreter();
            throw new MemoryFaultException(e);
        }
    }

    @Override
    public int getI32() throws MemoryFaultException {
        try {
            return memory.getI32(offset);
        } catch (SegmentationViolation e) {
            CompilerDirectives.transferToInterpreter();
            throw new MemoryFaultException(e);
        }
    }

    @Override
    public long getI64() throws MemoryFaultException {
        try {
            return memory.getI64(offset);
        } catch (SegmentationViolation e) {
            CompilerDirectives.transferToInterpreter();
            throw new MemoryFaultException(e);
        }
    }

    @Override
    public void setI8(byte val) throws MemoryFaultException {
        try {
            memory.setI8(offset, val);
        } catch (SegmentationViolation e) {
            CompilerDirectives.transferToInterpreter();
            throw new MemoryFaultException(e);
        }
    }

    @Override
    public void setI16(short val) throws MemoryFaultException {
        try {
            memory.setI16(offset, val);
        } catch (SegmentationViolation e) {
            CompilerDirectives.transferToInterpreter();
            throw new MemoryFaultException(e);
        }
    }

    @Override
    public void setI32(int val) throws MemoryFaultException {
        try {
            memory.setI32(offset, val);
        } catch (SegmentationViolation e) {
            CompilerDirectives.transferToInterpreter();
            throw new MemoryFaultException(e);
        }
    }

    @Override
    public void setI64(long val) throws MemoryFaultException {
        try {
            memory.setI64(offset, val);
        } catch (SegmentationViolation e) {
            CompilerDirectives.transferToInterpreter();
            throw new MemoryFaultException(e);
        }
    }

    @Override
    public String toString() {
        CompilerAsserts.neverPartOfCompilation();
        return String.format("PosixVirtualMemoryPointer[0x%016X]", offset);
    }
}
