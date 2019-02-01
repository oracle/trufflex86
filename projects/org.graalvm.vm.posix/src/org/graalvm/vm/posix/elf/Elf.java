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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.graalvm.vm.util.io.Endianess;

public class Elf {
	public static final int MAGIC = 0x7f454c46;

	public static final byte ELFCLASSNONE = 0;
	public static final byte ELFCLASS32 = 1;
	public static final byte ELFCLASS64 = 2;

	public static final byte ELFDATA2LSB = 1; /* 2's complement, little endian */
	public static final byte ELFDATA2MSB = 2; /* 2's complement, big endian */

	public static final byte OSABI_SYSV = 0x00;
	public static final byte OSABI_HPUX = 0x01;
	public static final byte OSABI_NETBSD = 0x02;
	public static final byte OSABI_LINUX = 0x03;
	public static final byte OSABI_GNU_HURD = 0x04;
	public static final byte OSABI_SOLARIS = 0x06;
	public static final byte OSABI_AIX = 0x07;
	public static final byte OSABI_IRIX = 0x08;
	public static final byte OSABI_FREEBSD = 0x09;
	public static final byte OSABI_TRU64 = 0x0A;
	public static final byte OSABI_MODESTO = 0x0B;
	public static final byte OSABI_OPENBSD = 0x0C;
	public static final byte OSABI_OPENVMS = 0x0D;
	public static final byte OSABI_NONSTOP_KERNEL = 0x0E;
	public static final byte OSABI_AROS = 0x0F;
	public static final byte OSABI_FENIX_OS = 0x10;
	public static final byte OSABI_CLOUDABI = 0x11;
	public static final byte OSABI_SORTIX = 0x53;
	public static final byte OSABI_STANDALONE = (byte) 0xFF;

	public static final short ET_NONE = 0x00;
	public static final short ET_REL = 0x01;
	public static final short ET_EXEC = 0x02;
	public static final short ET_DYN = 0x03;
	public static final short ET_CORE = 0x04;
	public static final short ET_NUM = 0x05;

	// @formatter:off
	public static final short EM_NONE            =  0;         /* No machine */
	public static final short EM_M32             =  1;         /* AT&T WE 32100 */
	public static final short EM_SPARC           =  2;         /* SUN SPARC */
	public static final short EM_386             =  3;         /* Intel 80386 */
	public static final short EM_68K             =  4;         /* Motorola m68k family */
	public static final short EM_88K             =  5;         /* Motorola m88k family */
	public static final short EM_IAMCU           =  6;         /* Intel MCU */
	public static final short EM_860             =  7;         /* Intel 80860 */
	public static final short EM_MIPS            =  8;         /* MIPS R3000 big-endian */
	public static final short EM_S370            =  9;         /* IBM System/370 */
	public static final short EM_MIPS_RS3_LE     = 10;         /* MIPS R3000 little-endian */
	                                                           /* reserved 11-14 */
	public static final short EM_PARISC          = 15;         /* HPPA */
	                                                           /* reserved 16 */
	public static final short EM_VPP500          = 17;         /* Fujitsu VPP500 */
	public static final short EM_SPARC32PLUS     = 18;         /* Sun's "v8plus" */
	public static final short EM_960             = 19;         /* Intel 80960 */
	public static final short EM_PPC             = 20;         /* PowerPC */
	public static final short EM_PPC64           = 21;         /* PowerPC 64-bit */
	public static final short EM_S390            = 22;         /* IBM S390 */
	public static final short EM_SPU             = 23;         /* IBM SPU/SPC */
	                                                           /* reserved 24-35 */
	public static final short EM_V800            = 36;         /* NEC V800 series */
	public static final short EM_FR20            = 37;         /* Fujitsu FR20 */
	public static final short EM_RH32            = 38;         /* TRW RH-32 */
	public static final short EM_RCE             = 39;         /* Motorola RCE */
	public static final short EM_ARM             = 40;         /* ARM */
	public static final short EM_FAKE_ALPHA      = 41;         /* Digital Alpha */
	public static final short EM_SH              = 42;         /* Hitachi SH */
	public static final short EM_SPARCV9         = 43;         /* SPARC v9 64-bit */
	public static final short EM_TRICORE         = 44;         /* Siemens Tricore */
	public static final short EM_ARC             = 45;         /* Argonaut RISC Core */
	public static final short EM_H8_300          = 46;         /* Hitachi H8/300 */
	public static final short EM_H8_300H         = 47;         /* Hitachi H8/300H */
	public static final short EM_H8S             = 48;         /* Hitachi H8S */
	public static final short EM_H8_500          = 49;         /* Hitachi H8/500 */
	public static final short EM_IA_64           = 50;         /* Intel Merced */
	public static final short EM_MIPS_X          = 51;         /* Stanford MIPS-X */
	public static final short EM_COLDFIRE        = 52;         /* Motorola Coldfire */
	public static final short EM_68HC12          = 53;         /* Motorola M68HC12 */
	public static final short EM_MMA             = 54;         /* Fujitsu MMA Multimedia Accelerator */
	public static final short EM_PCP             = 55;         /* Siemens PCP */
	public static final short EM_NCPU            = 56;         /* Sony nCPU embeeded RISC */
	public static final short EM_NDR1            = 57;         /* Denso NDR1 microprocessor */
	public static final short EM_STARCORE        = 58;         /* Motorola Start*Core processor */
	public static final short EM_ME16            = 59;         /* Toyota ME16 processor */
	public static final short EM_ST100           = 60;         /* STMicroelectronic ST100 processor */
	public static final short EM_TINYJ           = 61;         /* Advanced Logic Corp. Tinyj emb.fam */
	public static final short EM_X86_64          = 62;         /* AMD x86-64 architecture */
	public static final short EM_PDSP            = 63;         /* Sony DSP Processor */
	public static final short EM_PDP10           = 64;         /* Digital PDP-10 */
	public static final short EM_PDP11           = 65;         /* Digital PDP-11 */
	public static final short EM_FX66            = 66;         /* Siemens FX66 microcontroller */
	public static final short EM_ST9PLUS         = 67;         /* STMicroelectronics ST9+ 8/16 mc */
	public static final short EM_ST7             = 68;         /* STmicroelectronics ST7 8 bit mc */
	public static final short EM_68HC16          = 69;         /* Motorola MC68HC16 microcontroller */
	public static final short EM_68HC11          = 70;         /* Motorola MC68HC11 microcontroller */
	public static final short EM_68HC08          = 71;         /* Motorola MC68HC08 microcontroller */
	public static final short EM_68HC05          = 72;         /* Motorola MC68HC05 microcontroller */
	public static final short EM_SVX             = 73;         /* Silicon Graphics SVx */
	public static final short EM_ST19            = 74;         /* STMicroelectronics ST19 8 bit mc */
	public static final short EM_VAX             = 75;         /* Digital VAX */
	public static final short EM_CRIS            = 76;         /* Axis Communications 32-bit emb.proc */
	public static final short EM_JAVELIN         = 77;         /* Infineon Technologies 32-bit emb.proc */
	public static final short EM_FIREPATH        = 78;         /* Element 14 64-bit DSP Processor */
	public static final short EM_ZSP             = 79;         /* LSI Logic 16-bit DSP Processor */
	public static final short EM_MMIX            = 80;         /* Donald Knuth's educational 64-bit proc */
	public static final short EM_HUANY           = 81;         /* Harvard University machine-independent object files */
	public static final short EM_PRISM           = 82;         /* SiTera Prism */
	public static final short EM_AVR             = 83;         /* Atmel AVR 8-bit microcontroller */
	public static final short EM_FR30            = 84;         /* Fujitsu FR30 */
	public static final short EM_D10V            = 85;         /* Mitsubishi D10V */
	public static final short EM_D30V            = 86;         /* Mitsubishi D30V */
	public static final short EM_V850            = 87;         /* NEC v850 */
	public static final short EM_M32R            = 88;         /* Mitsubishi M32R */
	public static final short EM_MN10300         = 89;         /* Matsushita MN10300 */
	public static final short EM_MN10200         = 90;         /* Matsushita MN10200 */
	public static final short EM_PJ              = 91;         /* picoJava */
	public static final short EM_OPENRISC        = 92;         /* OpenRISC 32-bit embedded processor */
	public static final short EM_ARC_COMPACT     = 93;         /* ARC International ARCompact */
	public static final short EM_XTENSA          = 94;         /* Tensilica Xtensa Architecture */
	public static final short EM_VIDEOCORE       = 95;         /* Alphamosaic VideoCore */
	public static final short EM_TMM_GPP         = 96;         /* Thompson Multimedia General Purpose Proc */
	public static final short EM_NS32K           = 97;         /* National Semi. 32000 */
	public static final short EM_TPC             = 98;         /* Tenor Network TPC */
	public static final short EM_SNP1K           = 99;         /* Trebia SNP 1000 */
	public static final short EM_ST200           = 100;        /* STMicroelectronics ST200 */
	public static final short EM_IP2K            = 101;        /* Ubicom IP2xxx */
	public static final short EM_MAX             = 102;        /* MAX processor */
	public static final short EM_CR              = 103;        /* National Semi. CompactRISC */
	public static final short EM_F2MC16          = 104;        /* Fujitsu F2MC16 */
	public static final short EM_MSP430          = 105;        /* Texas Instruments msp430 */
	public static final short EM_BLACKFIN        = 106;        /* Analog Devices Blackfin DSP */
	public static final short EM_SE_C33          = 107;        /* Seiko Epson S1C33 family */
	public static final short EM_SEP             = 108;        /* Sharp embedded microprocessor */
	public static final short EM_ARCA            = 109;        /* Arca RISC */
	public static final short EM_UNICORE         = 110;        /* PKU-Unity & MPRC Peking Uni. mc series */
	public static final short EM_EXCESS          = 111;        /* eXcess configurable cpu */
	public static final short EM_DXP             = 112;        /* Icera Semi. Deep Execution Processor */
	public static final short EM_ALTERA_NIOS2    = 113;        /* Altera Nios II */
	public static final short EM_CRX             = 114;        /* National Semi. CompactRISC CRX */
	public static final short EM_XGATE           = 115;        /* Motorola XGATE */
	public static final short EM_C166            = 116;        /* Infineon C16x/XC16x */
	public static final short EM_M16C            = 117;        /* Renesas M16C */
	public static final short EM_DSPIC30F        = 118;        /* Microchip Technology dsPIC30F */
	public static final short EM_CE              = 119;        /* Freescale Communication Engine RISC */
	public static final short EM_M32C            = 120;        /* Renesas M32C */
	                                                           /* reserved 121-130 */
	public static final short EM_TSK3000         = 131;        /* Altium TSK3000 */
	public static final short EM_RS08            = 132;        /* Freescale RS08 */
	public static final short EM_SHARC           = 133;        /* Analog Devices SHARC family */
	public static final short EM_ECOG2           = 134;        /* Cyan Technology eCOG2 */
	public static final short EM_SCORE7          = 135;        /* Sunplus S+core7 RISC */
	public static final short EM_DSP24           = 136;        /* New Japan Radio (NJR) 24-bit DSP */
	public static final short EM_VIDEOCORE3      = 137;        /* Broadcom VideoCore III */
	public static final short EM_LATTICEMICO32   = 138;        /* RISC for Lattice FPGA */
	public static final short EM_SE_C17          = 139;        /* Seiko Epson C17 */
	public static final short EM_TI_C6000        = 140;        /* Texas Instruments TMS320C6000 DSP */
	public static final short EM_TI_C2000        = 141;        /* Texas Instruments TMS320C2000 DSP */
	public static final short EM_TI_C5500        = 142;        /* Texas Instruments TMS320C55x DSP */
	public static final short EM_TI_ARP32        = 143;        /* Texas Instruments App. Specific RISC */
	public static final short EM_TI_PRU          = 144;        /* Texas Instruments Prog. Realtime Unit */
	                                                           /* reserved 145-159 */
	public static final short EM_MMDSP_PLUS      = 160;        /* STMicroelectronics 64bit VLIW DSP */
	public static final short EM_CYPRESS_M8C     = 161;        /* Cypress M8C */
	public static final short EM_R32C            = 162;        /* Renesas R32C */
	public static final short EM_TRIMEDIA        = 163;        /* NXP Semi. TriMedia */
	public static final short EM_QDSP6           = 164;        /* QUALCOMM DSP6 */
	public static final short EM_8051            = 165;        /* Intel 8051 and variants */
	public static final short EM_STXP7X          = 166;        /* STMicroelectronics STxP7x */
	public static final short EM_NDS32           = 167;        /* Andes Tech. compact code emb. RISC */
	public static final short EM_ECOG1X          = 168;        /* Cyan Technology eCOG1X */
	public static final short EM_MAXQ30          = 169;        /* Dallas Semi. MAXQ30 mc */
	public static final short EM_XIMO16          = 170;        /* New Japan Radio (NJR) 16-bit DSP */
	public static final short EM_MANIK           = 171;        /* M2000 Reconfigurable RISC */
	public static final short EM_CRAYNV2         = 172;        /* Cray NV2 vector architecture */
	public static final short EM_RX              = 173;        /* Renesas RX */
	public static final short EM_METAG           = 174;        /* Imagination Tech. META */
	public static final short EM_MCST_ELBRUS     = 175;        /* MCST Elbrus */
	public static final short EM_ECOG16          = 176;        /* Cyan Technology eCOG16 */
	public static final short EM_CR16            = 177;        /* National Semi. CompactRISC CR16 */
	public static final short EM_ETPU            = 178;        /* Freescale Extended Time Processing Unit */
	public static final short EM_SLE9X           = 179;        /* Infineon Tech. SLE9X */
	public static final short EM_L10M            = 180;        /* Intel L10M */
	public static final short EM_K10M            = 181;        /* Intel K10M */
	                                                           /* reserved 182 */
	public static final short EM_AARCH64         = 183;        /* ARM AARCH64 */
	                                                           /* reserved 184 */
	public static final short EM_AVR32           = 185;        /* Amtel 32-bit microprocessor */
	public static final short EM_STM8            = 186;        /* STMicroelectronics STM8 */
	public static final short EM_TILE64          = 187;        /* Tileta TILE64 */
	public static final short EM_TILEPRO         = 188;        /* Tilera TILEPro */
	public static final short EM_MICROBLAZE      = 189;        /* Xilinx MicroBlaze */
	public static final short EM_CUDA            = 190;        /* NVIDIA CUDA */
	public static final short EM_TILEGX          = 191;        /* Tilera TILE-Gx */
	public static final short EM_CLOUDSHIELD     = 192;        /* CloudShield */
	public static final short EM_COREA_1ST       = 193;        /* KIPO-KAIST Core-A 1st gen. */
	public static final short EM_COREA_2ND       = 194;        /* KIPO-KAIST Core-A 2nd gen. */
	public static final short EM_ARC_COMPACT2    = 195;        /* Synopsys ARCompact V2 */
	public static final short EM_OPEN8           = 196;        /* Open8 RISC */
	public static final short EM_RL78            = 197;        /* Renesas RL78 */
	public static final short EM_VIDEOCORE5      = 198;        /* Broadcom VideoCore V */
	public static final short EM_78KOR           = 199;        /* Renesas 78KOR */
	public static final short EM_56800EX         = 200;        /* Freescale 56800EX DSC */
	public static final short EM_BA1             = 201;        /* Beyond BA1 */
	public static final short EM_BA2             = 202;        /* Beyond BA2 */
	public static final short EM_XCORE           = 203;        /* XMOS xCORE */
	public static final short EM_MCHP_PIC        = 204;        /* Microchip 8-bit PIC(r) */
	                                                           /* reserved 205-209 */
	public static final short EM_KM32            = 210;        /* KM211 KM32 */
	public static final short EM_KMX32           = 211;        /* KM211 KMX32 */
	public static final short EM_EMX16           = 212;        /* KM211 KMX16 */
	public static final short EM_EMX8            = 213;        /* KM211 KMX8 */
	public static final short EM_KVARC           = 214;        /* KM211 KVARC */
	public static final short EM_CDP             = 215;        /* Paneve CDP */
	public static final short EM_COGE            = 216;        /* Cognitive Smart Memory Processor */
	public static final short EM_COOL            = 217;        /* Bluechip CoolEngine */
	public static final short EM_NORC            = 218;        /* Nanoradio Optimized RISC */
	public static final short EM_CSR_KALIMBA     = 219;        /* CSR Kalimba */
	public static final short EM_Z80             = 220;        /* Zilog Z80 */
	public static final short EM_VISIUM          = 221;        /* Controls and Data Services VISIUMcore */
	public static final short EM_FT32            = 222;        /* FTDI Chip FT32 */
	public static final short EM_MOXIE           = 223;        /* Moxie processor */
	public static final short EM_AMDGPU          = 224;        /* AMD GPU */
	                                                           /* reserved 225-242 */
	public static final short EM_RISCV           = 243;        /* RISC-V */

	public static final short EM_BPF             = 247;        /* Linux BPF -- in-kernel virtual machine */

	public static final short EM_NUM             = 248;

	public static final int SHT_NULL             = 0;          /* Section header table entry unused */
	public static final int SHT_PROGBITS         = 1;          /* Program data */
	public static final int SHT_SYMTAB           = 2;          /* Symbol table */
	public static final int SHT_STRTAB           = 3;          /* String table */
	public static final int SHT_RELA             = 4;          /* Relocation entries with addends */
	public static final int SHT_HASH             = 5;          /* Symbol hash table */
	public static final int SHT_DYNAMIC          = 6;          /* Dynamic linking information */
	public static final int SHT_NOTE             = 7;          /* Notes */
	public static final int SHT_NOBITS           = 8;          /* Program space with no data (bss) */
	public static final int SHT_REL              = 9;          /* Relocation entries, no addends */
	public static final int SHT_SHLIB            = 10;         /* Reserved */
	public static final int SHT_DYNSYM           = 11;         /* Dynamic linker symbol table */
	public static final int SHT_INIT_ARRAY       = 14;         /* Array of constructors */
	public static final int SHT_FINI_ARRAY       = 15;         /* Array of destructors */
	public static final int SHT_PREINIT_ARRAY    = 16;         /* Array of pre-constructors */
	public static final int SHT_GROUP            = 17;         /* Section group */
	public static final int SHT_SYMTAB_SHNDX     = 18;         /* Extended section indeces */
	public static final int SHT_NUM              = 19;         /* Number of defined types. */
	public static final int SHT_LOOS             = 0x60000000; /* Start OS-specific. */
	public static final int SHT_GNU_ATTRIBUTES   = 0x6ffffff5; /* Object attributes. */
	public static final int SHT_GNU_HASH         = 0x6ffffff6; /* GNU-style hash table. */
	public static final int SHT_GNU_LIBLIST      = 0x6ffffff7; /* Prelink library list */
	public static final int SHT_CHECKSUM         = 0x6ffffff8; /* Checksum for DSO content. */
	public static final int SHT_LOSUNW           = 0x6ffffffa; /* Sun-specific low bound. */
	public static final int SHT_SUNW_move        = 0x6ffffffa;
	public static final int SHT_SUNW_COMDAT      = 0x6ffffffb;
	public static final int SHT_SUNW_syminfo     = 0x6ffffffc;
	public static final int SHT_GNU_verdef       = 0x6ffffffd; /* Version definition section. */
	public static final int SHT_GNU_verneed      = 0x6ffffffe; /* Version needs section. */
	public static final int SHT_GNU_versym       = 0x6fffffff; /* Version symbol table. */
	public static final int SHT_HISUNW           = 0x6fffffff; /* Sun-specific high bound. */
	public static final int SHT_HIOS             = 0x6fffffff; /* End OS-specific type */
	public static final int SHT_LOPROC           = 0x70000000; /* Start of processor-specific */
	public static final int SHT_HIPROC           = 0x7fffffff; /* End of processor-specific */
	public static final int SHT_LOUSER           = 0x80000000; /* Start of application-specific */
	public static final int SHT_HIUSER           = 0x8fffffff; /* End of application-specific */

	public static final int SHF_WRITE            = (1 << 0);   /* Writable */
	public static final int SHF_ALLOC            = (1 << 1);   /* Occupies memory during execution */
	public static final int SHF_EXECINSTR        = (1 << 2);   /* Executable */
	public static final int SHF_MERGE            = (1 << 4);   /* Might be merged */
	public static final int SHF_STRINGS          = (1 << 5);   /* Contains nul-terminated strings */
	public static final int SHF_INFO_LINK        = (1 << 6);   /* `sh_info' contains SHT index */
	public static final int SHF_LINK_ORDER       = (1 << 7);   /* Preserve order after combining */
	public static final int SHF_OS_NONCONFORMING = (1 << 8);   /* Non-standard OS specific handling
	                                             =         ;      required */
	public static final int SHF_GROUP            = (1 << 9);   /* Section is member of a group. */
	public static final int SHF_TLS              = (1 << 10);  /* Section hold thread-local data. */
	public static final int SHF_COMPRESSED       = (1 << 11);  /* Section with compressed data. */
	public static final int SHF_MASKOS           = 0x0ff00000; /* OS-specific. */
	public static final int SHF_MASKPROC         = 0xf0000000; /* Processor-specific */
	public static final int SHF_ORDERED          = (1 << 30);  /* Special ordering requirement
	                                             =                (Solaris). */
	public static final int SHF_EXCLUDE          = (1 << 31);  /* Section is excluded unless
	                                                              referenced or allocated (Solaris).*/

	public static final int PT_NULL              = 0;          /* Program header table entry unused */
	public static final int PT_LOAD              = 1;          /* Loadable program segment */
	public static final int PT_DYNAMIC           = 2;          /* Dynamic linking information */
	public static final int PT_INTERP            = 3;          /* Program interpreter */
	public static final int PT_NOTE              = 4;          /* Auxiliary information */
	public static final int PT_SHLIB             = 5;          /* Reserved */
	public static final int PT_PHDR              = 6;          /* Entry for header table itself */
	public static final int PT_TLS               = 7;          /* Thread-local storage segment */
	public static final int PT_NUM               = 8;          /* Number of defined types */
	public static final int PT_LOOS              = 0x60000000; /* Start of OS-specific */
	public static final int PT_GNU_EH_FRAME      = 0x6474e550; /* GCC .eh_frame_hdr segment */
	public static final int PT_GNU_STACK         = 0x6474e551; /* Indicates stack executability */
	public static final int PT_GNU_RELRO         = 0x6474e552; /* Read-only after relocation */
	public static final int PT_LOSUNW            = 0x6ffffffa;
	public static final int PT_SUNWBSS           = 0x6ffffffa; /* Sun Specific segment */
	public static final int PT_SUNWSTACK         = 0x6ffffffb; /* Stack segment */
	public static final int PT_HISUNW            = 0x6fffffff;
	public static final int PT_HIOS              = 0x6fffffff; /* End of OS-specific */
	public static final int PT_LOPROC            = 0x70000000; /* Start of processor-specific */
	public static final int PT_HIPROC            = 0x7fffffff; /* End of processor-specific */

	public static final int PF_X                 = (1 << 0);   /* Segment is executable */
	public static final int PF_W                 = (1 << 1);   /* Segment is writable */
	public static final int PF_R                 = (1 << 2);   /* Segment is readable */
	public static final int PF_MASKOS            = 0x0ff00000; /* OS-specific */
	public static final int PF_MASKPROC          = 0xf0000000; /* Processor-specific */
	// @formatter:off

	private byte[] data;

	public byte ei_class;
	public byte ei_data;
	public byte ei_version;
	public byte ei_osabi;
	public byte ei_abiversion;
	public short e_type;
	public short e_machine;
	public int e_version;
	public long e_entry;
	public long e_phoff;
	public long e_shoff;
	public int e_flags;
	public short e_ehsize;
	public short e_phentsize;
	public short e_phnum;
	public short e_shentsize;
	public short e_shnum;
	public short e_shstrndx;

	public List<ProgramHeader> programHeaders;
	public List<Section> sections;

	public StringTable stringTable;
	public SymbolTable symtab;
	public SymbolTable dynsym;

	public Elf(byte[] data) throws IOException {
		int magic = Endianess.get32bitBE(data, 0);
		if(magic != MAGIC) {
			throw new IOException("Invalid magic");
		}

		this.data = data;

		ei_class = data[4];
		ei_data = data[5];
		ei_version = data[6];
		ei_osabi = data[7];
		ei_abiversion = data[8];

		if(ei_class == ELFCLASS32) {
			if(ei_data == ELFDATA2LSB) {
				e_type = Endianess.get16bitLE(data, 0x10);
				e_machine = Endianess.get16bitLE(data, 0x12);
				e_version = Endianess.get32bitLE(data, 0x14);
				e_entry = Endianess.get32bitLE(data, 0x18);
				e_phoff = Endianess.get32bitLE(data, 0x1C);
				e_shoff = Endianess.get32bitLE(data, 0x20);
				e_flags = Endianess.get32bitLE(data, 0x24);
				e_ehsize = Endianess.get16bitLE(data, 0x28);
				e_phentsize = Endianess.get16bitLE(data, 0x2A);
				e_phnum = Endianess.get16bitLE(data, 0x2C);
				e_shentsize = Endianess.get16bitLE(data, 0x2E);
				e_shnum = Endianess.get16bitLE(data, 0x30);
				e_shstrndx = Endianess.get16bitLE(data, 0x32);
			} else if(ei_data == ELFDATA2MSB) {
				e_type = Endianess.get16bitBE(data, 0x10);
				e_machine = Endianess.get16bitBE(data, 0x12);
				e_version = Endianess.get32bitBE(data, 0x14);
				e_entry = Endianess.get32bitBE(data, 0x18);
				e_phoff = Endianess.get32bitBE(data, 0x1C);
				e_shoff = Endianess.get32bitBE(data, 0x20);
				e_flags = Endianess.get32bitBE(data, 0x24);
				e_ehsize = Endianess.get16bitBE(data, 0x28);
				e_phentsize = Endianess.get16bitBE(data, 0x2A);
				e_phnum = Endianess.get16bitBE(data, 0x2C);
				e_shentsize = Endianess.get16bitBE(data, 0x2E);
				e_shnum = Endianess.get16bitBE(data, 0x30);
				e_shstrndx = Endianess.get16bitBE(data, 0x32);
			} else {
				throw new IOException("unknown ei_data: " + ei_data);
			}
		} else if(ei_class == ELFCLASS64) {
			if(ei_data == ELFDATA2LSB) {
				e_type = Endianess.get16bitLE(data, 0x10);
				e_machine = Endianess.get16bitLE(data, 0x12);
				e_version = Endianess.get32bitLE(data, 0x14);
				e_entry = Endianess.get64bitLE(data, 0x18);
				e_phoff = Endianess.get64bitLE(data, 0x20);
				e_shoff = Endianess.get64bitLE(data, 0x28);
				e_flags = Endianess.get32bitLE(data, 0x30);
				e_ehsize = Endianess.get16bitLE(data, 0x34);
				e_phentsize = Endianess.get16bitLE(data, 0x36);
				e_phnum = Endianess.get16bitLE(data, 0x38);
				e_shentsize = Endianess.get16bitLE(data, 0x3A);
				e_shnum = Endianess.get16bitLE(data, 0x3C);
				e_shstrndx = Endianess.get16bitLE(data, 0x3E);
			} else if(ei_data == ELFDATA2MSB) {
				e_type = Endianess.get16bitBE(data, 0x10);
				e_machine = Endianess.get16bitBE(data, 0x12);
				e_version = Endianess.get32bitBE(data, 0x14);
				e_entry = Endianess.get64bitBE(data, 0x18);
				e_phoff = Endianess.get64bitBE(data, 0x20);
				e_shoff = Endianess.get64bitBE(data, 0x28);
				e_flags = Endianess.get32bitBE(data, 0x30);
				e_ehsize = Endianess.get16bitBE(data, 0x34);
				e_phentsize = Endianess.get16bitBE(data, 0x36);
				e_phnum = Endianess.get16bitBE(data, 0x38);
				e_shentsize = Endianess.get16bitBE(data, 0x3A);
				e_shnum = Endianess.get16bitBE(data, 0x3C);
				e_shstrndx = Endianess.get16bitBE(data, 0x3E);
			} else {
				throw new IOException("unknown ei_data: " + ei_data);
			}
		} else {
			throw new IOException("unknown ei_class: " + ei_class);
		}

		programHeaders = new ArrayList<>();
		for(int i = 0; i < e_phnum; i++) {
			int off = (int) (e_phoff + i * e_phentsize);
			programHeaders.add(new ProgramHeader(this, off));
		}

		sections = new ArrayList<>();
		for(int i = 0; i < e_shnum; i++) {
			int off = (int) (e_shoff + i * e_shentsize);
			sections.add(new Section(this, off));
		}

		Section shstrndx = sections.get(e_shstrndx);
		stringTable = new StringTable(shstrndx);
		String shstrtabName = shstrndx.getName();
		if(!shstrtabName.equals(".shstrtab")) {
			throw new IOException("String table section has wrong name: '" + shstrtabName + "'");
		}
	}

	public byte[] getData() {
		return data;
	}

	public ProgramHeader getProgramHeader(int i) {
		return programHeaders.get(i);
	}

	public List<ProgramHeader> getProgramHeaders() {
		return Collections.unmodifiableList(programHeaders);
	}

	@SuppressWarnings("unchecked")
	public <T extends Section> T getSection(int i) {
		Section section = sections.get(i);

		// lazy section parsing
		if(section.getClass().equals(Section.class)) {
			switch(section.getType()) {
			case Elf.SHT_SYMTAB:
			case Elf.SHT_DYNSYM:
				section = new SymbolTable(section);
				sections.set(i, section);
				break;
			case Elf.SHT_STRTAB:
				section = new StringTable(section);
				sections.set(i, section);
				break;
			}
		}

		return (T) section;
	}

	public <T extends Section> T getSection(String name) {
		for(int i = 0; i < sections.size(); i++) {
			if(sections.get(i).getName().equals(name)) {
				return getSection(i);
			}
		}
		return null;
	}

	public Symbol getSymbol(String name) {
		if(symtab == null) {
			symtab = getSection(".symtab");
		}
		if(symtab == null) {
			return null;
		}
		return symtab.getSymbol(name);
	}

	public Symbol getDynamicSymbol(String name) {
		if(dynsym == null) {
			dynsym = getSection(".dynsym");
		}
		if(dynsym == null) {
			return null;
		}
		return dynsym.getSymbol(name);
	}

	public SymbolTable getSymbolTable() {
		if(symtab == null) {
			symtab = getSection(".symtab");
		}
		return symtab;
	}

	public SymbolTable getDynamicSymbolTable() {
		if(dynsym == null) {
			dynsym = getSection(".dynsym");
		}
		return dynsym;
	}

	public String getSectionHeaderString(int index) {
		return stringTable.getString(index);
	}

	public long getEntryPoint() {
		return e_entry;
	}
}
