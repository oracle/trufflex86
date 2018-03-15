package org.graalvm.vm.x86.test.runner;

import org.junit.Ignore;
import org.junit.Test;

public class SulongAsm {
    @Ignore("not yet implemented")
    @Test
    public void adc001() throws Exception {
        String stdout = "00000000:00000000:0:00000000:0:0\n" +
                        "00000000:00000000:1:00000001:0:0\n" +
                        "00000d0c:00000000:1:00000d0d:0:0\n" +
                        "00000d0c:00000d0c:1:00001a19:0:0\n" +
                        "00000000:00000d0c:1:00000d0d:0:0\n" +
                        "00000d0c:00000000:0:00000d0c:0:0\n" +
                        "00000d0c:00000d0c:0:00001a18:0:0\n" +
                        "00000000:00000d0c:0:00000d0c:0:0\n" +
                        "ffffffff:00000000:0:ffffffff:0:0\n" +
                        "ffffffff:00000001:0:00000000:1:0\n" +
                        "ffffffff:00000d0c:0:00000d0b:1:0\n" +
                        "ffffffff:80000000:0:7fffffff:1:1\n" +
                        "ffffffff:ffffffff:0:fffffffe:1:0\n" +
                        "ffffffff:00000000:1:00000000:1:0\n" +
                        "ffffffff:00000001:1:00000001:1:0\n" +
                        "ffffffff:00000d0c:1:00000d0c:1:0\n" +
                        "ffffffff:80000000:1:80000000:1:0\n" +
                        "ffffffff:ffffffff:1:ffffffff:1:0\n" +
                        "80000000:00000000:0:80000000:0:0\n" +
                        "80000000:00000d0c:0:80000d0c:0:0\n" +
                        "80000000:80000000:0:00000000:1:1\n" +
                        "80000000:ffffffff:0:7fffffff:1:1\n" +
                        "80000000:00000000:1:80000001:0:0\n" +
                        "80000000:00000d0c:1:80000d0d:0:0\n" +
                        "80000000:80000000:1:00000001:1:1\n" +
                        "80000000:ffffffff:1:80000000:1:0\n";
        TestRunner.run("adc001.elf", new String[0], "", stdout, "", 0);
    }

    @Test
    public void add001() throws Exception {
        String stdout = "00000000:00000000:00000000:0:0\n" +
                        "00000000:00000d0c:00000d0c:0:0\n" +
                        "00000d0c:00000000:00000d0c:0:0\n" +
                        "00000d0c:00000d0c:00001a18:0:0\n" +
                        "ffffffff:00000000:ffffffff:0:0\n" +
                        "ffffffff:00000001:00000000:1:0\n" +
                        "ffffffff:00000d0c:00000d0b:1:0\n" +
                        "ffffffff:80000000:7fffffff:1:1\n" +
                        "ffffffff:ffffffff:fffffffe:1:0\n" +
                        "80000000:00000000:80000000:0:0\n" +
                        "80000000:00000d0c:80000d0c:0:0\n" +
                        "80000000:80000000:00000000:1:1\n" +
                        "80000000:ffffffff:7fffffff:1:1\n";
        TestRunner.run("add001.elf", new String[0], "", stdout, "", 0);
    }

    @Test
    public void bsf001() throws Exception {
        TestRunner.run("bsf001.elf", new String[0], "", "", "", 1);
    }

    @Test
    public void bsf002() throws Exception {
        TestRunner.run("bsf002.elf", new String[0], "", "", "", 1);
    }

    @Test
    public void bsf003() throws Exception {
        TestRunner.run("bsf003.elf", new String[0], "", "", "", 1);
    }

    @Test
    public void bsr001() throws Exception {
        TestRunner.run("bsr001.elf", new String[0], "", "", "", 1);
    }

    @Test
    public void bsr002() throws Exception {
        TestRunner.run("bsr002.elf", new String[0], "", "", "", 1);
    }

    @Test
    public void bsr003() throws Exception {
        TestRunner.run("bsr003.elf", new String[0], "", "", "", 1);
    }

    @Test
    public void cpuid002() throws Exception {
        TestRunner.run("cpuid002.elf", new String[0], "", "'VMX86onGraal'\n", "", 0);
    }

    @Test
    public void lea001() throws Exception {
        TestRunner.run("lea001.elf", new String[0], "", "", "", 1);
    }

    @Ignore("not yet implemented")
    @Test
    public void push001() throws Exception {
        TestRunner.run("push001.elf", new String[0], "", "", "", 1);
    }

    @Test
    public void push002() throws Exception {
        TestRunner.run("push002.elf", new String[0], "", "", "", 1);
    }

    @Test
    public void stosb001() throws Exception {
        TestRunner.run("stosb001.elf", new String[0], "", "", "", 1);
    }

    @Ignore("not yet implemented")
    @Test
    public void stosb002() throws Exception {
        TestRunner.run("stosb002.elf", new String[0], "", "buf: CC CC 42 42 42 42 42 42 42 42 42 42 CC CC CC CC\n", "", 1);
    }

    @Test
    public void seto001() throws Exception {
        TestRunner.run("seto001.elf", new String[0], "", "", "", 0);
    }

    @Test
    public void seto002() throws Exception {
        TestRunner.run("seto002.elf", new String[0], "", "", "", 0);
    }

    @Test
    public void seto003() throws Exception {
        TestRunner.run("seto003.elf", new String[0], "", "", "", 0);
    }

    @Test
    public void seto004() throws Exception {
        TestRunner.run("seto004.elf", new String[0], "", "", "", 0);
    }

    @Test
    public void seto005() throws Exception {
        TestRunner.run("seto005.elf", new String[0], "", "", "", 0);
    }

    @Test
    public void seto006() throws Exception {
        TestRunner.run("seto006.elf", new String[0], "", "", "", 0);
    }

    @Test
    public void seto007() throws Exception {
        TestRunner.run("seto007.elf", new String[0], "", "", "", 0);
    }

    @Test
    public void seto008() throws Exception {
        TestRunner.run("seto008.elf", new String[0], "", "", "", 0);
    }

    @Test
    public void seto009() throws Exception {
        TestRunner.run("seto009.elf", new String[0], "", "", "", 1);
    }

    @Test
    public void seto010() throws Exception {
        TestRunner.run("seto010.elf", new String[0], "", "", "", 1);
    }

    @Test
    public void qemuBsx() throws Exception {
        String stdout = "bsrw       A=0000000000000000 R=0000000012345678 1\n" +
                        "bsrw       A=0000000012340128 R=0000000012340008 0\n" +
                        "bsfw       A=0000000000000000 R=0000000012345678 1\n" +
                        "bsfw       A=0000000012340128 R=0000000012340003 0\n" +
                        "bsrl       A=0000000000000000 R=0000000012345678 1\n" +
                        "bsrl       A=0000000000340128 R=0000000000000015 0\n" +
                        "bsfl       A=0000000000000000 R=0000000012345678 1\n" +
                        "bsfl       A=0000000000340128 R=0000000000000003 0\n" +
                        "bsrq       A=0000000000000000 R=0000000012345678 1\n" +
                        "bsrq       A=0000003401281234 R=0000000000000025 0\n" +
                        "bsfq       A=0000000000000000 R=0000000012345678 1\n" +
                        "bsfq       A=0000003401281234 R=0000000000000002 0\n";
        TestRunner.run("qemu-bsx.elf", new String[0], "", stdout, "", 0);
    }

    @Test
    public void qemuLea() throws Exception {
        String stdout = "lea 0x4000 = 0000000000004000\n" +
                        "lea (%%eax) = 0000000000000001\n" +
                        "lea (%%ebx) = 0000000000000002\n" +
                        "lea (%%ecx) = 0000000000000004\n" +
                        "lea (%%edx) = 0000000000000008\n" +
                        "lea (%%esi) = 0000000000000010\n" +
                        "lea (%%edi) = 0000000000000020\n" +
                        "lea 0x40(%%eax) = 0000000000000041\n" +
                        "lea 0x40(%%ebx) = 0000000000000042\n" +
                        "lea 0x40(%%ecx) = 0000000000000044\n" +
                        "lea 0x40(%%edx) = 0000000000000048\n" +
                        "lea 0x40(%%esi) = 0000000000000050\n" +
                        "lea 0x40(%%edi) = 0000000000000060\n" +
                        "lea 0x4000(%%eax) = 0000000000004001\n" +
                        "lea 0x4000(%%ebx) = 0000000000004002\n" +
                        "lea 0x4000(%%ecx) = 0000000000004004\n" +
                        "lea 0x4000(%%edx) = 0000000000004008\n" +
                        "lea 0x4000(%%esi) = 0000000000004010\n" +
                        "lea 0x4000(%%edi) = 0000000000004020\n" +
                        "lea (%%eax, %%ecx) = 0000000000000005\n" +
                        "lea (%%ebx, %%edx) = 000000000000000a\n" +
                        "lea (%%ecx, %%ecx) = 0000000000000008\n" +
                        "lea (%%edx, %%ecx) = 000000000000000c\n" +
                        "lea (%%esi, %%ecx) = 0000000000000014\n" +
                        "lea (%%edi, %%ecx) = 0000000000000024\n" +
                        "lea 0x40(%%eax, %%ecx) = 0000000000000045\n" +
                        "lea 0x4000(%%ebx, %%edx) = 000000000000400a\n" +
                        "lea (%%ecx, %%ecx, 2) = 000000000000000c\n" +
                        "lea (%%edx, %%ecx, 4) = 0000000000000018\n" +
                        "lea (%%esi, %%ecx, 8) = 0000000000000030\n" +
                        "lea (,%%eax, 2) = 0000000000000002\n" +
                        "lea (,%%ebx, 4) = 0000000000000008\n" +
                        "lea (,%%ecx, 8) = 0000000000000020\n" +
                        "lea 0x40(,%%eax, 2) = 0000000000000042\n" +
                        "lea 0x40(,%%ebx, 4) = 0000000000000048\n" +
                        "lea 0x40(,%%ecx, 8) = 0000000000000060\n" +
                        "lea -10(%%ecx, %%ecx, 2) = 0000000000000002\n" +
                        "lea -10(%%edx, %%ecx, 4) = 000000000000000e\n" +
                        "lea -10(%%esi, %%ecx, 8) = 0000000000000026\n" +
                        "lea 0x4000(%%ecx, %%ecx, 2) = 000000000000400c\n" +
                        "lea 0x4000(%%edx, %%ecx, 4) = 0000000000004018\n" +
                        "lea 0x4000(%%esi, %%ecx, 8) = 0000000000004030\n" +
                        "lea 0x4000 = 0000000000004000\n" +
                        "lea 0x4000(%%rip) = 0000000000404baa\n" +
                        "lea (%%rax) = 0000abcc00000001\n" +
                        "lea (%%rbx) = 0000abcf00000002\n" +
                        "lea (%%rcx) = 0000abc900000004\n" +
                        "lea (%%rdx) = 0000abc500000008\n" +
                        "lea (%%rsi) = 0000abdd00000010\n" +
                        "lea (%%rdi) = 0000abed00000020\n" +
                        "lea 0x40(%%rax) = 0000abcc00000041\n" +
                        "lea 0x40(%%rbx) = 0000abcf00000042\n" +
                        "lea 0x40(%%rcx) = 0000abc900000044\n" +
                        "lea 0x40(%%rdx) = 0000abc500000048\n" +
                        "lea 0x40(%%rsi) = 0000abdd00000050\n" +
                        "lea 0x40(%%rdi) = 0000abed00000060\n" +
                        "lea 0x4000(%%rax) = 0000abcc00004001\n" +
                        "lea 0x4000(%%rbx) = 0000abcf00004002\n" +
                        "lea 0x4000(%%rcx) = 0000abc900004004\n" +
                        "lea 0x4000(%%rdx) = 0000abc500004008\n" +
                        "lea 0x4000(%%rsi) = 0000abdd00004010\n" +
                        "lea 0x4000(%%rdi) = 0000abed00004020\n" +
                        "lea (%%rax, %%rcx) = 0001579500000005\n" +
                        "lea (%%rbx, %%rdx) = 000157940000000a\n" +
                        "lea (%%rcx, %%rcx) = 0001579200000008\n" +
                        "lea (%%rdx, %%rcx) = 0001578e0000000c\n" +
                        "lea (%%rsi, %%rcx) = 000157a600000014\n" +
                        "lea (%%rdi, %%rcx) = 000157b600000024\n" +
                        "lea 0x40(%%rax, %%rcx) = 0001579500000045\n" +
                        "lea 0x4000(%%rbx, %%rdx) = 000157940000400a\n" +
                        "lea (%%rcx, %%rcx, 2) = 0002035b0000000c\n" +
                        "lea (%%rdx, %%rcx, 4) = 00035ae900000018\n" +
                        "lea (%%rsi, %%rcx, 8) = 00060a2500000030\n" +
                        "lea (,%%rax, 2) = 0001579800000002\n" +
                        "lea (,%%rbx, 4) = 0002af3c00000008\n" +
                        "lea (,%%rcx, 8) = 00055e4800000020\n" +
                        "lea 0x40(,%%rax, 2) = 0001579800000042\n" +
                        "lea 0x40(,%%rbx, 4) = 0002af3c00000048\n" +
                        "lea 0x40(,%%rcx, 8) = 00055e4800000060\n" +
                        "lea -10(%%rcx, %%rcx, 2) = 0002035b00000002\n" +
                        "lea -10(%%rdx, %%rcx, 4) = 00035ae90000000e\n" +
                        "lea -10(%%rsi, %%rcx, 8) = 00060a2500000026\n" +
                        "lea 0x4000(%%rcx, %%rcx, 2) = 0002035b0000400c\n" +
                        "lea 0x4000(%%rdx, %%rcx, 4) = 00035ae900004018\n" +
                        "lea 0x4000(%%rsi, %%rcx, 8) = 00060a2500004030\n";
        TestRunner.run("qemu-lea.elf", new String[0], "", stdout, "", 0);
    }

    @Test
    public void syscall001() throws Exception {
        TestRunner.run("syscall001.elf", new String[0], "", "Hello world!\n", "", 42);
    }

    @Test
    public void syscall002() throws Exception {
        TestRunner.run("syscall002.elf", new String[0], "", "Hello world!\n", "", 42);
    }

    @Test
    public void syscall_getuid001() throws Exception {
        TestRunner.run("syscall-getuid001.elf", new String[0], "", "uid: 1000\n", "", 0);
    }

    @Test
    public void syscall_getgid001() throws Exception {
        TestRunner.run("syscall-getgid001.elf", new String[0], "", "gid: 1000\n", "", 0);
    }
}
