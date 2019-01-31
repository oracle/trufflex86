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
		if(elf.ei_class == Elf.ELFCLASS32) {
			if(elf.ei_data == Elf.ELFDATA2LSB) {
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
			} else if(elf.ei_data == Elf.ELFDATA2MSB) {
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
		} else if(elf.ei_class == Elf.ELFCLASS64) {
			if(elf.ei_data == Elf.ELFDATA2LSB) {
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
			} else if(elf.ei_data == Elf.ELFDATA2MSB) {
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
		if(target.length < sh_size) {
			throw new IllegalArgumentException();
		}
		System.arraycopy(elf.getData(), (int) sh_offset, target, 0, (int) sh_size);
	}
}
