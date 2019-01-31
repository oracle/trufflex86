package org.graalvm.vm.posix.elf;

public abstract class SectionData {
	protected Elf elf;
	protected Section section;

	public SectionData(Section section) {
		this.section = section;
		this.elf = section.getElf();
	}
}
