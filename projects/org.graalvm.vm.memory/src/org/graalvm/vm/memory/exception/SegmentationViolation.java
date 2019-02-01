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
package org.graalvm.vm.memory.exception;

import org.graalvm.vm.memory.Memory;
import org.graalvm.vm.memory.MemoryPage;

import com.oracle.truffle.api.CompilerAsserts;

public class SegmentationViolation extends RuntimeException {
    private static final long serialVersionUID = 6904011763641924860L;

    private final MemoryPage page;
    private final Memory memory;
    private final long offset;

    public SegmentationViolation(SegmentationViolation e) {
        this.page = e.page;
        this.memory = e.memory;
        this.offset = e.offset;
    }

    public SegmentationViolation(long offset) {
        this.page = null;
        this.memory = null;
        this.offset = offset;
    }

    public SegmentationViolation(Memory memory, long offset) {
        this.page = null;
        this.memory = memory;
        this.offset = offset;
    }

    public SegmentationViolation(MemoryPage page, long offset) {
        this.page = page;
        this.memory = null;
        this.offset = offset;
    }

    public long getAddress() {
        return offset;
    }

    @Override
    public String toString() {
        CompilerAsserts.neverPartOfCompilation();
        if (page != null) {
            return String.format(
                            "Invalid memory access at 0x%016X (page base: 0x%016X, page end: 0x%019X)",
                            offset, page.getBase(), page.getEnd());
        } else if (memory != null) {
            return String.format("Invalid memory access at 0x%016X", offset);
        } else {
            return String.format("Invalid memory access at 0x%016X", offset);
        }
    }
}
