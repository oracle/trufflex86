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
package org.graalvm.vm.posix.elf;

import org.graalvm.vm.util.io.Endianess;

public class Section {
    protected Elf elf;

    private int sh_name;
    private int sh_type;
    public long sh_flags;
    private long sh_addr;
    private long sh_offset;
    public long sh_size;
    private int sh_link;
    public int sh_info;
    public long sh_addralign;
    public long sh_entsize;

    public Section(Elf elf, int offset) {
        this.elf = elf;
        byte[] data = elf.getData();
        if (elf.ei_class == Elf.ELFCLASS32) {
            if (elf.ei_data == Elf.ELFDATA2LSB) {
                sh_name = Endianess.get32bitLE(data, offset);
                sh_type = Endianess.get32bitLE(data, offset + 0x04);
                sh_flags = Endianess.get32bitLE(data, offset + 0x08);
                sh_addr = Endianess.get32bitLE(data, offset + 0x0C);
                sh_offset = Endianess.get32bitLE(data, offset + 0x10);
                sh_size = Endianess.get32bitLE(data, offset + 0x14);
                sh_link = Endianess.get32bitLE(data, offset + 0x18);
                sh_info = Endianess.get32bitLE(data, offset + 0x1C);
                sh_addralign = Endianess.get32bitLE(data, offset + 0x20);
                sh_entsize = Endianess.get32bitLE(data, offset + 0x24);
            } else if (elf.ei_data == Elf.ELFDATA2MSB) {
                sh_name = Endianess.get32bitBE(data, offset);
                sh_type = Endianess.get32bitBE(data, offset + 0x04);
                sh_flags = Endianess.get32bitBE(data, offset + 0x08);
                sh_addr = Endianess.get32bitBE(data, offset + 0x0C);
                sh_offset = Endianess.get32bitBE(data, offset + 0x10);
                sh_size = Endianess.get32bitBE(data, offset + 0x14);
                sh_link = Endianess.get32bitBE(data, offset + 0x18);
                sh_info = Endianess.get32bitBE(data, offset + 0x1C);
                sh_addralign = Endianess.get32bitBE(data, offset + 0x20);
                sh_entsize = Endianess.get32bitBE(data, offset + 0x24);
            } else {
                throw new IllegalArgumentException();
            }
        } else if (elf.ei_class == Elf.ELFCLASS64) {
            if (elf.ei_data == Elf.ELFDATA2LSB) {
                sh_name = Endianess.get32bitLE(data, offset);
                sh_type = Endianess.get32bitLE(data, offset + 0x04);
                sh_flags = Endianess.get64bitLE(data, offset + 0x08);
                sh_addr = Endianess.get64bitLE(data, offset + 0x10);
                sh_offset = Endianess.get64bitLE(data, offset + 0x18);
                sh_size = Endianess.get64bitLE(data, offset + 0x20);
                sh_link = Endianess.get32bitLE(data, offset + 0x28);
                sh_info = Endianess.get32bitLE(data, offset + 0x2C);
                sh_addralign = Endianess.get64bitLE(data, offset + 0x30);
                sh_entsize = Endianess.get64bitLE(data, offset + 0x38);
            } else if (elf.ei_data == Elf.ELFDATA2MSB) {
                sh_name = Endianess.get32bitBE(data, offset);
                sh_type = Endianess.get32bitBE(data, offset + 0x04);
                sh_flags = Endianess.get64bitBE(data, offset + 0x08);
                sh_addr = Endianess.get64bitBE(data, offset + 0x10);
                sh_offset = Endianess.get64bitBE(data, offset + 0x18);
                sh_size = Endianess.get64bitBE(data, offset + 0x20);
                sh_link = Endianess.get32bitBE(data, offset + 0x28);
                sh_info = Endianess.get32bitBE(data, offset + 0x2C);
                sh_addralign = Endianess.get64bitBE(data, offset + 0x30);
                sh_entsize = Endianess.get64bitBE(data, offset + 0x38);
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    public Section(Section section) {
        this.elf = section.elf;
        this.sh_name = section.sh_name;
        this.sh_type = section.sh_type;
        this.sh_flags = section.sh_flags;
        this.sh_addr = section.sh_addr;
        this.sh_offset = section.sh_offset;
        this.sh_size = section.sh_size;
        this.sh_link = section.sh_link;
        this.sh_info = section.sh_info;
        this.sh_addralign = section.sh_addralign;
        this.sh_entsize = section.sh_entsize;
    }

    Elf getElf() {
        return elf;
    }

    public String getName() {
        return elf.getSectionHeaderString(sh_name);
    }

    public int getType() {
        return sh_type;
    }

    public long getAddress() {
        return sh_addr;
    }

    public long getOffset() {
        return sh_offset;
    }

    public long getSize() {
        return sh_size;
    }

    public <T extends Section> T getLink() {
        return elf.getSection(sh_link);
    }

    public int getLinkNum() {
        return sh_link;
    }

    public void load(byte[] target) {
        if (target.length < sh_size) {
            throw new IllegalArgumentException();
        }
        System.arraycopy(elf.getData(), (int) sh_offset, target, 0, (int) sh_size);
    }
}
