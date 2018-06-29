package org.graalvm.vm.x86.emu;

import java.io.IOException;

import org.graalvm.vm.memory.JavaVirtualMemory;
import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.memory.hardware.linux.MemoryMap;
import org.graalvm.vm.memory.hardware.linux.MemorySegment;
import org.graalvm.vm.x86.posix.PosixEnvironment;

public class Emu86 {
    public static void main(String[] args) throws IOException {
        System.loadLibrary("emu86");
        try (Ptrace ptrace = new Ptrace()) {
            System.out.printf("pid: %s\n", ptrace.getPid());

            VirtualMemory mem = new JavaVirtualMemory();
            PosixEnvironment env = new PosixEnvironment(mem, "x86_64");

            MemoryMap map = new MemoryMap(ptrace.getPid());

            for (MemorySegment s : map.getSegments()) {
                if (s.permissions.isRead() && s.permissions.isWrite()) {
                    ptrace.write(s.start, 0x0A46454542L); // "BEEF\n"
                    ptrace.syscall(1, 1, s.start, 5, 0, 0, 0);
                    return;
                }
            }
        }
    }
}
