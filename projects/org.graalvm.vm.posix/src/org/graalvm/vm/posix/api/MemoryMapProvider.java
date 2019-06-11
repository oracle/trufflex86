package org.graalvm.vm.posix.api;

public interface MemoryMapProvider {
    byte[] getMemoryMap();
}
