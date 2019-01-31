package org.graalvm.vm.posix.test.elf;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.graalvm.vm.posix.elf.Elf;
import org.graalvm.vm.posix.elf.ProgramHeader;
import org.graalvm.vm.posix.elf.Section;
import org.graalvm.vm.posix.elf.Symbol;
import org.graalvm.vm.util.ResourceLoader;
import org.junit.Before;
import org.junit.Test;

public class ElfParserTestDyn {
	private Elf elf;

	private static byte[] readTestFile() throws IOException {
		try(InputStream in = ResourceLoader.loadResource(ElfParserTestDyn.class, "helloworld.elf");
				ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			byte[] buf = new byte[256];
			int n;
			while((n = in.read(buf)) != -1) {
				out.write(buf, 0, n);
			}
			return out.toByteArray();
		}
	}

	@Before
	public void setup() throws IOException {
		elf = new Elf(readTestFile());
	}

	@Test
	public void testFileHeader() {
		assertEquals(Elf.ELFCLASS32, elf.ei_class);
		assertEquals(Elf.ELFDATA2MSB, elf.ei_data);
		assertEquals(1, elf.ei_version);
		assertEquals(Elf.OSABI_SYSV, elf.ei_osabi);
		assertEquals(0, elf.ei_abiversion);
		assertEquals(Elf.ET_EXEC, elf.e_type);
		assertEquals(Elf.EM_PPC, elf.e_machine);
		assertEquals(1, elf.e_version);
		assertEquals(0x100002fc, elf.e_entry);
		assertEquals(52, elf.e_phoff);
		assertEquals(3408, elf.e_shoff);
		assertEquals(0, elf.e_flags);
		assertEquals(32, elf.e_phentsize);
		assertEquals(7, elf.e_phnum);
		assertEquals(40, elf.e_shentsize);
		assertEquals(29, elf.e_shnum);
		assertEquals(26, elf.e_shstrndx);
	}

	private void testProgramHeader(int n, int p_type, long p_offset, long p_vaddr, long p_paddr, long p_filesz,
			long p_memsz, int p_flags, long p_align) {
		ProgramHeader ph = elf.getProgramHeader(n);
		assertEquals(p_type, ph.p_type);
		assertEquals(p_offset, ph.p_offset);
		assertEquals(p_vaddr, ph.p_vaddr);
		assertEquals(p_paddr, ph.p_paddr);
		assertEquals(p_filesz, ph.p_filesz);
		assertEquals(p_memsz, ph.p_memsz);
		assertEquals(p_flags, ph.p_flags);
		assertEquals(p_align, ph.p_align);
	}

	@Test
	public void testProgramHeaders() {
		int LOAD = Elf.PT_LOAD;
		int PHDR = Elf.PT_PHDR;
		int INTERP = Elf.PT_INTERP;
		int NOTE = Elf.PT_NOTE;
		int DYNAMIC = Elf.PT_DYNAMIC;
		int GNU_STACK = Elf.PT_GNU_STACK;
		int R = Elf.PF_R;
		int RW = Elf.PF_R | Elf.PF_W;
		int RWE = Elf.PF_R | Elf.PF_W | Elf.PF_X;
		int E = Elf.PF_X;

		// @formatter:off
		testProgramHeader(0, PHDR,      0x000034, 0x10000034, 0x10000034, 0x000e0, 0x000e0, R|E, 0x4);
		testProgramHeader(1, INTERP,    0x000114, 0x10000114, 0x10000114, 0x0000d, 0x0000d, R,   0x1);
		testProgramHeader(2, LOAD,      0x000000, 0x10000000, 0x10000000, 0x00664, 0x00664, R|E, 0x10000);
		testProgramHeader(3, LOAD,      0x000664, 0x10010664, 0x10010664, 0x00100, 0x00174, RWE, 0x10000);
		testProgramHeader(4, DYNAMIC,   0x000680, 0x10010680, 0x10010680, 0x000c8, 0x000c8, RW,  0x4);
		testProgramHeader(5, NOTE,      0x000124, 0x10000124, 0x10000124, 0x00044, 0x00044, R,   0x4);
		testProgramHeader(6, GNU_STACK, 0x000000, 0x00000000, 0x00000000, 0x00000, 0x00000, RW,  0x10);
		// @formatter:on
		ProgramHeader interp = elf.getProgramHeader(1);
		String path = new String(elf.getData(), (int) interp.p_offset, (int) interp.p_filesz - 1);
		assertEquals("/lib/ld.so.1", path);
	}

	private void testSectionHeader(int i, String name, int type, int addr, int off, int size, int es, int flg,
			int lk, int inf, int al) {
		Section sh = elf.getSection(i);
		assertEquals(name, sh.getName());
		assertEquals(type, sh.getType());
		assertEquals(addr, sh.getAddress());
		assertEquals(off, sh.getOffset());
		assertEquals(size, sh.sh_size);
		assertEquals(es, sh.sh_entsize);
		assertEquals(flg, sh.sh_flags);
		assertEquals(lk, sh.getLinkNum());
		assertEquals(inf, sh.sh_info);
		assertEquals(al, sh.sh_addralign);
	}

	@Test
	public void testSectionHeaders() {
		int NULL = Elf.SHT_NULL;
		int PROGBITS = Elf.SHT_PROGBITS;
		int NOBITS = Elf.SHT_NOBITS;
		int STRTAB = Elf.SHT_STRTAB;
		int SYMTAB = Elf.SHT_SYMTAB;
		int NOTE = Elf.SHT_NOTE;
		int GNU_HASH = Elf.SHT_GNU_HASH;
		int DYNSYM = Elf.SHT_DYNSYM;
		int VERSYM = Elf.SHT_GNU_versym;
		int RELA = Elf.SHT_RELA;
		int VERNEED = Elf.SHT_GNU_verneed;
		int DYNAMIC = Elf.SHT_DYNAMIC;
		int WAX = Elf.SHF_WRITE | Elf.SHF_ALLOC | Elf.SHF_EXECINSTR;
		int WA = Elf.SHF_WRITE | Elf.SHF_ALLOC;
		int AX = Elf.SHF_ALLOC | Elf.SHF_EXECINSTR;
		int A = Elf.SHF_ALLOC;
		int AI = Elf.SHF_ALLOC | Elf.SHF_INFO_LINK;
		int MS = Elf.SHF_MERGE | Elf.SHF_STRINGS;

		// @formatter:off
		testSectionHeader( 0, "",                   NULL,     0x00000000, 0x000000, 0x000000, 0x00,   0,  0,  0,  0);
		testSectionHeader( 1, ".interp",            PROGBITS, 0x10000114, 0x000114, 0x00000d, 0x00,   A,  0,  0,  1);
		testSectionHeader( 2, ".note.ABI-tag",      NOTE,     0x10000124, 0x000124, 0x000020, 0x00,   A,  0,  0,  4);
		testSectionHeader( 3, ".note.gnu.build-id", NOTE,     0x10000144, 0x000144, 0x000024, 0x00,   A,  0,  0,  4);
		testSectionHeader( 4, ".gnu.hash",          GNU_HASH, 0x10000168, 0x000168, 0x000020, 0x04,   A,  5,  0,  4);
		testSectionHeader( 5, ".dynsym",            DYNSYM,   0x10000188, 0x000188, 0x000050, 0x10,   A,  6,  1,  4);
		testSectionHeader( 6, ".dynstr",            STRTAB,   0x100001d8, 0x0001d8, 0x00004a, 0x00,   A,  0,  0,  1);
		testSectionHeader( 7, ".gnu.version",       VERSYM,   0x10000222, 0x000222, 0x00000a, 0x02,   A,  5,  0,  2);
		testSectionHeader( 8, ".gnu.version_r",     VERNEED,  0x1000022c, 0x00022c, 0x000020, 0x00,   A,  6,  1,  4);
		testSectionHeader( 9, ".rela.dyn",          RELA,     0x1000024c, 0x00024c, 0x00000c, 0x0c,   A,  5,  0,  4);
		testSectionHeader(10, ".rela.plt",          RELA,     0x10000258, 0x000258, 0x000024, 0x0c,  AI,  5, 23,  4);
		testSectionHeader(11, ".init",              PROGBITS, 0x1000027c, 0x00027c, 0x00004c, 0x00,  AX,  0,  0,  4);
		testSectionHeader(12, ".text",              PROGBITS, 0x100002d0, 0x0002d0, 0x000338, 0x00,  AX,  0,  0, 16);
		testSectionHeader(13, ".fini",              PROGBITS, 0x10000608, 0x000608, 0x000030, 0x00,  AX,  0,  0,  4);
		testSectionHeader(14, ".rodata",            PROGBITS, 0x10000638, 0x000638, 0x000028, 0x00,   A,  0,  0,  8);
		testSectionHeader(15, ".eh_frame",          PROGBITS, 0x10000660, 0x000660, 0x000004, 0x00,   A,  0,  0,  4);
		testSectionHeader(16, ".ctors",             PROGBITS, 0x10010664, 0x000664, 0x000008, 0x00,  WA,  0,  0,  4);
		testSectionHeader(17, ".dtors",             PROGBITS, 0x1001066c, 0x00066c, 0x000008, 0x00,  WA,  0,  0,  4);
		testSectionHeader(18, ".jcr",               PROGBITS, 0x10010674, 0x000674, 0x000004, 0x00,  WA,  0,  0,  4);
		testSectionHeader(19, ".got2",              PROGBITS, 0x10010678, 0x000678, 0x000008, 0x00,  WA,  0,  0,  1);
		testSectionHeader(20, ".dynamic",           DYNAMIC,  0x10010680, 0x000680, 0x0000c8, 0x08,  WA,  6,  0,  4);
		testSectionHeader(21, ".data",              PROGBITS, 0x10010748, 0x000748, 0x000008, 0x00,  WA,  0,  0,  4);
		testSectionHeader(22, ".got",               PROGBITS, 0x10010750, 0x000750, 0x000014, 0x04, WAX,  0,  0,  4);
		testSectionHeader(23, ".plt",               NOBITS,   0x10010764, 0x000764, 0x00006c, 0x00, WAX,  0,  0,  4);
		testSectionHeader(24, ".bss",               NOBITS,   0x100107d0, 0x000764, 0x000008, 0x00,  WA,  0,  0,  4);
		testSectionHeader(25, ".comment",           PROGBITS, 0x00000000, 0x000764, 0x00001a, 0x01,  MS,  0,  0,  1);
		testSectionHeader(26, ".shstrtab",          STRTAB,   0x00000000, 0x000c61, 0x0000ed, 0x00,   0,  0,  0,  1);
		testSectionHeader(27, ".symtab",            SYMTAB,   0x00000000, 0x000780, 0x000380, 0x10,   0, 28, 35,  4);
		testSectionHeader(28, ".strtab",            STRTAB,   0x00000000, 0x000b00, 0x000161, 0x00,   0,  0,  0,  1);
		// @formatter:on

	}

	@Test
	public void testSymbolTable() {
		Symbol main = elf.getSymbol("main");
		assertEquals(0x100002d0, main.getValue());
		assertEquals(44, main.getSize());
		assertEquals(Symbol.FUNC, main.getType());
		assertEquals(Symbol.GLOBAL, main.getBind());
		assertEquals(Symbol.DEFAULT, main.getVisibility());
		assertEquals(12, main.getSectionIndex());
		assertEquals(51, main.getIndex());
	}

	@Test
	public void testDynamicSymbolTable() {
		Symbol main = elf.getDynamicSymbol("puts");
		assertEquals(0, main.getValue());
		assertEquals(0, main.getSize());
		assertEquals(Symbol.FUNC, main.getType());
		assertEquals(Symbol.GLOBAL, main.getBind());
		assertEquals(Symbol.DEFAULT, main.getVisibility());
		assertEquals(Symbol.SHN_UNDEF, main.getSectionIndex());
		assertEquals(1, main.getIndex());
	}
}
