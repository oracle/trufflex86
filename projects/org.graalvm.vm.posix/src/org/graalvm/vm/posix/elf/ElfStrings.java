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

import java.util.HashMap;
import java.util.Map;

public class ElfStrings {
    private static final Map<Short, String> ELF_TYPE;
    private static final Map<Short, String> ELF_MACHINE;

    static {
        ELF_TYPE = new HashMap<>();
        ELF_TYPE.put(Elf.ET_NONE, "None");
        ELF_TYPE.put(Elf.ET_REL, "REL");
        ELF_TYPE.put(Elf.ET_EXEC, "EXEC");
        ELF_TYPE.put(Elf.ET_DYN, "DYN");
        ELF_TYPE.put(Elf.ET_CORE, "CORE");

        ELF_MACHINE = new HashMap<>();
        ELF_MACHINE.put(Elf.EM_NONE, "No machine");
        ELF_MACHINE.put(Elf.EM_M32, "AT&T WE 32100");
        ELF_MACHINE.put(Elf.EM_SPARC, "SUN SPARC");
        ELF_MACHINE.put(Elf.EM_386, "Intel 80386");
        ELF_MACHINE.put(Elf.EM_68K, "Motorola m68k family");
        ELF_MACHINE.put(Elf.EM_88K, "Motorola m88k family");
        ELF_MACHINE.put(Elf.EM_IAMCU, "Intel MCU");
        ELF_MACHINE.put(Elf.EM_860, "Intel 80860");
        ELF_MACHINE.put(Elf.EM_MIPS, "MIPS R3000 big-endian");
        ELF_MACHINE.put(Elf.EM_S370, "IBM System/370");
        ELF_MACHINE.put(Elf.EM_MIPS_RS3_LE, "MIPS R3000 little-endian");
        ELF_MACHINE.put(Elf.EM_PARISC, "HPPA");
        ELF_MACHINE.put(Elf.EM_VPP500, "Fujitsu VPP500");
        ELF_MACHINE.put(Elf.EM_SPARC32PLUS, "Sun's \"v8plus\"");
        ELF_MACHINE.put(Elf.EM_960, "Intel 80960");
        ELF_MACHINE.put(Elf.EM_PPC, "PowerPC");
        ELF_MACHINE.put(Elf.EM_PPC64, "PowerPC 64-bit");
        ELF_MACHINE.put(Elf.EM_S390, "IBM S390");
        ELF_MACHINE.put(Elf.EM_SPU, "IBM SPU/SPC");
        ELF_MACHINE.put(Elf.EM_V800, "NEC V800 series");
        ELF_MACHINE.put(Elf.EM_FR20, "Fujitsu FR20");
        ELF_MACHINE.put(Elf.EM_RH32, "TRW RH-32");
        ELF_MACHINE.put(Elf.EM_RCE, "Motorola RCE");
        ELF_MACHINE.put(Elf.EM_ARM, "ARM");
        ELF_MACHINE.put(Elf.EM_FAKE_ALPHA, "Digital Alpha");
        ELF_MACHINE.put(Elf.EM_SH, "Hitachi SH");
        ELF_MACHINE.put(Elf.EM_SPARCV9, "SPARC v9 64-bit");
        ELF_MACHINE.put(Elf.EM_TRICORE, "Siemens Tricore");
        ELF_MACHINE.put(Elf.EM_ARC, "Argonaut RISC Core");
        ELF_MACHINE.put(Elf.EM_H8_300, "Hitachi H8/300");
        ELF_MACHINE.put(Elf.EM_H8_300H, "Hitachi H8/300H");
        ELF_MACHINE.put(Elf.EM_H8S, "Hitachi H8S");
        ELF_MACHINE.put(Elf.EM_H8_500, "Hitachi H8/500");
        ELF_MACHINE.put(Elf.EM_IA_64, "Intel Merced");
        ELF_MACHINE.put(Elf.EM_MIPS_X, "Stanford MIPS-X");
        ELF_MACHINE.put(Elf.EM_COLDFIRE, "Motorola Coldfire");
        ELF_MACHINE.put(Elf.EM_68HC12, "Motorola M68HC12");
        ELF_MACHINE.put(Elf.EM_MMA, "Fujitsu MMA Multimedia Accelerator");
        ELF_MACHINE.put(Elf.EM_PCP, "Siemens PCP");
        ELF_MACHINE.put(Elf.EM_NCPU, "Sony nCPU embeeded RISC");
        ELF_MACHINE.put(Elf.EM_NDR1, "Denso NDR1 microprocessor");
        ELF_MACHINE.put(Elf.EM_STARCORE, "Motorola Start*Core processor");
        ELF_MACHINE.put(Elf.EM_ME16, "Toyota ME16 processor");
        ELF_MACHINE.put(Elf.EM_ST100, "STMicroelectronic ST100 processor");
        ELF_MACHINE.put(Elf.EM_TINYJ, "Advanced Logic Corp. Tinyj emb.fam");
        ELF_MACHINE.put(Elf.EM_X86_64, "AMD x86-64 architecture");
        ELF_MACHINE.put(Elf.EM_PDSP, "Sony DSP Processor");
        ELF_MACHINE.put(Elf.EM_PDP10, "Digital PDP-10");
        ELF_MACHINE.put(Elf.EM_PDP11, "Digital PDP-11");
        ELF_MACHINE.put(Elf.EM_FX66, "Siemens FX66 microcontroller");
        ELF_MACHINE.put(Elf.EM_ST9PLUS, "STMicroelectronics ST9+ 8/16 mc");
        ELF_MACHINE.put(Elf.EM_ST7, "STmicroelectronics ST7 8 bit mc");
        ELF_MACHINE.put(Elf.EM_68HC16, "Motorola MC68HC16 microcontroller");
        ELF_MACHINE.put(Elf.EM_68HC11, "Motorola MC68HC11 microcontroller");
        ELF_MACHINE.put(Elf.EM_68HC08, "Motorola MC68HC08 microcontroller");
        ELF_MACHINE.put(Elf.EM_68HC05, "Motorola MC68HC05 microcontroller");
        ELF_MACHINE.put(Elf.EM_SVX, "Silicon Graphics SVx");
        ELF_MACHINE.put(Elf.EM_ST19, "STMicroelectronics ST19 8 bit mc");
        ELF_MACHINE.put(Elf.EM_VAX, "Digital VAX");
        ELF_MACHINE.put(Elf.EM_CRIS, "Axis Communications 32-bit emb.proc");
        ELF_MACHINE.put(Elf.EM_JAVELIN, "Infineon Technologies 32-bit emb.proc");
        ELF_MACHINE.put(Elf.EM_FIREPATH, "Element 14 64-bit DSP Processor");
        ELF_MACHINE.put(Elf.EM_ZSP, "LSI Logic 16-bit DSP Processor");
        ELF_MACHINE.put(Elf.EM_MMIX, "Donald Knuth's educational 64-bit proc");
        ELF_MACHINE.put(Elf.EM_HUANY, "Harvard University machine-independent object files");
        ELF_MACHINE.put(Elf.EM_PRISM, "SiTera Prism");
        ELF_MACHINE.put(Elf.EM_AVR, "Atmel AVR 8-bit microcontroller");
        ELF_MACHINE.put(Elf.EM_FR30, "Fujitsu FR30");
        ELF_MACHINE.put(Elf.EM_D10V, "Mitsubishi D10V");
        ELF_MACHINE.put(Elf.EM_D30V, "Mitsubishi D30V");
        ELF_MACHINE.put(Elf.EM_V850, "NEC v850");
        ELF_MACHINE.put(Elf.EM_M32R, "Mitsubishi M32R");
        ELF_MACHINE.put(Elf.EM_MN10300, "Matsushita MN10300");
        ELF_MACHINE.put(Elf.EM_MN10200, "Matsushita MN10200");
        ELF_MACHINE.put(Elf.EM_OPENRISC, "OpenRISC 32-bit embedded processor");
        ELF_MACHINE.put(Elf.EM_ARC_COMPACT, "ARC International ARCompact");
        ELF_MACHINE.put(Elf.EM_XTENSA, "Tensilica Xtensa Architecture");
        ELF_MACHINE.put(Elf.EM_VIDEOCORE, "Alphamosaic VideoCore");
        ELF_MACHINE.put(Elf.EM_TMM_GPP, "Thompson Multimedia General Purpose Proc");
        ELF_MACHINE.put(Elf.EM_NS32K, "National Semi. 32000");
        ELF_MACHINE.put(Elf.EM_TPC, "Tenor Network TPC");
        ELF_MACHINE.put(Elf.EM_SNP1K, "Trebia SNP 1000");
        ELF_MACHINE.put(Elf.EM_ST200, "STMicroelectronics ST200");
        ELF_MACHINE.put(Elf.EM_IP2K, "Ubicom IP2xxx");
        ELF_MACHINE.put(Elf.EM_MAX, "MAX processor");
        ELF_MACHINE.put(Elf.EM_CR, "National Semi. CompactRISC");
        ELF_MACHINE.put(Elf.EM_F2MC16, "Fujitsu F2MC16");
        ELF_MACHINE.put(Elf.EM_MSP430, "Texas Instruments msp430");
        ELF_MACHINE.put(Elf.EM_BLACKFIN, "Analog Devices Blackfin DSP");
        ELF_MACHINE.put(Elf.EM_SE_C33, "Seiko Epson S1C33 family");
        ELF_MACHINE.put(Elf.EM_SEP, "Sharp embedded microprocessor");
        ELF_MACHINE.put(Elf.EM_ARCA, "Arca RISC");
        ELF_MACHINE.put(Elf.EM_UNICORE, "PKU-Unity & MPRC Peking Uni. mc series");
        ELF_MACHINE.put(Elf.EM_EXCESS, "eXcess configurable cpu");
        ELF_MACHINE.put(Elf.EM_DXP, "Icera Semi. Deep Execution Processor");
        ELF_MACHINE.put(Elf.EM_ALTERA_NIOS2, "Altera Nios II");
        ELF_MACHINE.put(Elf.EM_CRX, "National Semi. CompactRISC CRX");
        ELF_MACHINE.put(Elf.EM_XGATE, "Motorola XGATE");
        ELF_MACHINE.put(Elf.EM_C166, "Infineon C16x/XC16x");
        ELF_MACHINE.put(Elf.EM_M16C, "Renesas M16C");
        ELF_MACHINE.put(Elf.EM_DSPIC30F, "Microchip Technology dsPIC30F");
        ELF_MACHINE.put(Elf.EM_CE, "Freescale Communication Engine RISC");
        ELF_MACHINE.put(Elf.EM_M32C, "Renesas M32C");
        ELF_MACHINE.put(Elf.EM_TSK3000, "Altium TSK3000");
        ELF_MACHINE.put(Elf.EM_RS08, "Freescale RS08");
        ELF_MACHINE.put(Elf.EM_SHARC, "Analog Devices SHARC family");
        ELF_MACHINE.put(Elf.EM_ECOG2, "Cyan Technology eCOG2");
        ELF_MACHINE.put(Elf.EM_SCORE7, "Sunplus S+core7 RISC");
        ELF_MACHINE.put(Elf.EM_DSP24, "New Japan Radio (NJR) 24-bit DSP");
        ELF_MACHINE.put(Elf.EM_VIDEOCORE3, "Broadcom VideoCore III");
        ELF_MACHINE.put(Elf.EM_LATTICEMICO32, "RISC for Lattice FPGA");
        ELF_MACHINE.put(Elf.EM_SE_C17, "Seiko Epson C17");
        ELF_MACHINE.put(Elf.EM_TI_C6000, "Texas Instruments TMS320C6000 DSP");
        ELF_MACHINE.put(Elf.EM_TI_C2000, "Texas Instruments TMS320C2000 DSP");
        ELF_MACHINE.put(Elf.EM_TI_C5500, "Texas Instruments TMS320C55x DSP");
        ELF_MACHINE.put(Elf.EM_TI_ARP32, "Texas Instruments App. Specific RISC");
        ELF_MACHINE.put(Elf.EM_TI_PRU, "Texas Instruments Prog. Realtime Unit");
        ELF_MACHINE.put(Elf.EM_MMDSP_PLUS, "STMicroelectronics 64bit VLIW DSP");
        ELF_MACHINE.put(Elf.EM_CYPRESS_M8C, "Cypress M8C");
        ELF_MACHINE.put(Elf.EM_R32C, "Renesas R32C");
        ELF_MACHINE.put(Elf.EM_TRIMEDIA, "NXP Semi. TriMedia");
        ELF_MACHINE.put(Elf.EM_QDSP6, "QUALCOMM DSP6");
        ELF_MACHINE.put(Elf.EM_8051, "Intel 8051 and variants");
        ELF_MACHINE.put(Elf.EM_STXP7X, "STMicroelectronics STxP7x");
        ELF_MACHINE.put(Elf.EM_STXP7X, "STMicroelectronics STxP7x");
        ELF_MACHINE.put(Elf.EM_NDS32, "Andes Tech. compact code emb. RISC");
        ELF_MACHINE.put(Elf.EM_ECOG1X, "Cyan Technology eCOG1X");
        ELF_MACHINE.put(Elf.EM_MAXQ30, "Dallas Semi. MAXQ30 mc");
        ELF_MACHINE.put(Elf.EM_XIMO16, "New Japan Radio (NJR) 16-bit DSP");
        ELF_MACHINE.put(Elf.EM_MANIK, "M2000 Reconfigurable RISC");
        ELF_MACHINE.put(Elf.EM_CRAYNV2, "Cray NV2 vector architecture");
        ELF_MACHINE.put(Elf.EM_RX, "Renesas RX");
        ELF_MACHINE.put(Elf.EM_METAG, "Imagination Tech. META");
        ELF_MACHINE.put(Elf.EM_MCST_ELBRUS, "MCST Elbrus");
        ELF_MACHINE.put(Elf.EM_ECOG16, "Cyan Technology eCOG16");
        ELF_MACHINE.put(Elf.EM_CR16, "National Semi. CompactRISC CR16");
        ELF_MACHINE.put(Elf.EM_ETPU, "Freescale Extended Time Processing Unit");
        ELF_MACHINE.put(Elf.EM_SLE9X, "Infineon Tech. SLE9X");
        ELF_MACHINE.put(Elf.EM_L10M, "Intel L10M");
        ELF_MACHINE.put(Elf.EM_K10M, "Intel K10M");
        ELF_MACHINE.put(Elf.EM_AARCH64, "ARM AArch64");
        ELF_MACHINE.put(Elf.EM_AVR32, "Amtel 32-bit microprocessor");
        ELF_MACHINE.put(Elf.EM_STM8, "STMicroelectronics STM8");
        ELF_MACHINE.put(Elf.EM_TILE64, "Tileta TILE64");
        ELF_MACHINE.put(Elf.EM_TILEPRO, "Tilera TILEPro");
        ELF_MACHINE.put(Elf.EM_MICROBLAZE, "Xilinx MicroBlaze");
        ELF_MACHINE.put(Elf.EM_CUDA, "NVIDIA CUDA");
        ELF_MACHINE.put(Elf.EM_TILEGX, "Tilera TILE-Gx");
        ELF_MACHINE.put(Elf.EM_CLOUDSHIELD, "CloudShield");
        ELF_MACHINE.put(Elf.EM_COREA_1ST, "KIPO-KAIST Core-A 1st gen.");
        ELF_MACHINE.put(Elf.EM_COREA_2ND, "KIPO-KAIST Core-A 2nd gen.");
        ELF_MACHINE.put(Elf.EM_ARC_COMPACT2, "Synopsys ARCompact V2");
        ELF_MACHINE.put(Elf.EM_OPEN8, "Open8 RISC");
        ELF_MACHINE.put(Elf.EM_RL78, "Renesas RL78");
        ELF_MACHINE.put(Elf.EM_VIDEOCORE5, "Broadcom VideoCore V");
        ELF_MACHINE.put(Elf.EM_78KOR, "Renesas 78KOR");
        ELF_MACHINE.put(Elf.EM_56800EX, "Freescale 56800EX DSC");
        ELF_MACHINE.put(Elf.EM_BA1, "Beyond BA1");
        ELF_MACHINE.put(Elf.EM_BA2, "Beyond BA2");
        ELF_MACHINE.put(Elf.EM_XCORE, "XMOS xCORE");
        ELF_MACHINE.put(Elf.EM_MCHP_PIC, "Microchip 8-bit PIC(r)");
        ELF_MACHINE.put(Elf.EM_KM32, "KM211 KM32");
        ELF_MACHINE.put(Elf.EM_KMX32, "KM211 KMX32");
        ELF_MACHINE.put(Elf.EM_EMX16, "KM211 KMX16");
        ELF_MACHINE.put(Elf.EM_EMX8, "KM211 KMX8");
        ELF_MACHINE.put(Elf.EM_KVARC, "KM211 KVARC");
        ELF_MACHINE.put(Elf.EM_CDP, "Paneve CDP");
        ELF_MACHINE.put(Elf.EM_COGE, "Cognitive Smart Memory Processor");
        ELF_MACHINE.put(Elf.EM_COOL, "Bluechip CoolEngine");
        ELF_MACHINE.put(Elf.EM_NORC, "Nanoradio Optimized RISC");
        ELF_MACHINE.put(Elf.EM_CSR_KALIMBA, "CSR Kalimba");
        ELF_MACHINE.put(Elf.EM_Z80, "Zilog Z80");
        ELF_MACHINE.put(Elf.EM_VISIUM, "Controls and Data Services VISIUMcore");
        ELF_MACHINE.put(Elf.EM_FT32, "FTDI Chip FT32");
        ELF_MACHINE.put(Elf.EM_MOXIE, "Moxie processor");
        ELF_MACHINE.put(Elf.EM_AMDGPU, "AMD GPU");
        ELF_MACHINE.put(Elf.EM_RISCV, "RISC-V");
        ELF_MACHINE.put(Elf.EM_BPF, "Linux BPF");
    }

    public static String getElfType(short e_type) {
        String type = ELF_TYPE.get(e_type);
        if (type != null) {
            return type;
        } else {
            return String.format("0x%x", e_type);
        }
    }

    public static String getElfMachine(short e_machine) {
        String machine = ELF_MACHINE.get(e_machine);
        if (machine != null) {
            return machine;
        } else {
            return String.format("0x%04x", e_machine);
        }
    }
}
