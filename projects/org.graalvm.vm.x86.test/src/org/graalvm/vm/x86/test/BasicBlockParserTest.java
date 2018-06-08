package org.graalvm.vm.x86.test;

import static org.junit.Assert.assertEquals;

import org.graalvm.vm.x86.isa.CodeSegmentReader;
import org.graalvm.vm.x86.node.flow.AMD64BasicBlock;
import org.graalvm.vm.x86.node.flow.AMD64BasicBlockParser;
import org.junit.Test;

import com.everyware.posix.elf.Elf;
import com.everyware.posix.elf.ProgramHeader;

public class BasicBlockParserTest {
    @Test
    public void test() throws Exception {
        Elf elf = TestDataLoader.load("bin/helloworld.elf");
        String ref = "00000000004000b0:\n" +
                        "xor\teax,eax\n" +
                        "inc\teax\n" +
                        "mov\tedi,eax\n" +
                        "mov\trsi,0x6000cd\n" +
                        "mov\tedx,0xd\n" +
                        "syscall\n";
        for (ProgramHeader hdr : elf.getProgramHeaders()) {
            if (elf.getEntryPoint() >= hdr.getVirtualAddress() && elf.getEntryPoint() < (hdr.getVirtualAddress() + hdr.getMemorySize())) {
                CodeSegmentReader r = new CodeSegmentReader(hdr);
                r.setPC(elf.getEntryPoint());
                AMD64BasicBlock block = AMD64BasicBlockParser.parse(r);
                assertEquals(ref, block.toString());
                break;
            }
        }
    }
}
