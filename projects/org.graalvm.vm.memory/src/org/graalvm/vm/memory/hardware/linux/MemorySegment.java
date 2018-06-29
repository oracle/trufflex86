package org.graalvm.vm.memory.hardware.linux;

public class MemorySegment {
    public final long start;
    public final long end;
    public final String rawPermissions;
    public final MemoryPermission permissions;
    public final long offset;
    public final String name;

    public MemorySegment(long start, long end, String permissions, long offset, String name) {
        this.start = start;
        this.end = end;
        this.rawPermissions = permissions;
        this.permissions = new MemoryPermission(permissions);
        this.offset = offset;
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("%x-%x %s %08x %s", start, end, rawPermissions, offset, name);
    }
}
