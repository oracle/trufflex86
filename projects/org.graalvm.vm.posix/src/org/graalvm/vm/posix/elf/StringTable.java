package org.graalvm.vm.posix.elf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.graalvm.vm.posix.libc.CString;

public class StringTable extends Section {
	private byte[] bytes;
	private int offset;
	private int size;

	public StringTable(Section section) {
		super(section);
		this.bytes = elf.getData();
		this.offset = (int) section.getOffset();
		this.size = (int) section.sh_size;
	}

	public String getString(int index) {
		return CString.str(bytes, offset + index);
	}

	public List<String> getStrings() {
		List<String> strings = new ArrayList<>();
		for(int off = 0; off < size;) {
			String s = getString(off);
			strings.add(s);
			off += s.length() + 1;
		}
		return Collections.unmodifiableList(strings);
	}

	@Override
	public String toString() {
		return "StringTable[" +
				getStrings().stream().map((s) -> '"' + s + '"').collect(Collectors.joining(",")) + "]";
	}
}
