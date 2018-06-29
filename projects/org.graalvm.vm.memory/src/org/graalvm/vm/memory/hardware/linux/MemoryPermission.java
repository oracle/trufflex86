package org.graalvm.vm.memory.hardware.linux;

public class MemoryPermission {
    private boolean read;
    private boolean write;
    private boolean execute;

    public MemoryPermission(String perms) {
        read = perms.contains("r");
        write = perms.contains("w");
        execute = perms.contains("x");
    }

    public boolean isRead() {
        return read;
    }

    public boolean isWrite() {
        return write;
    }

    public boolean isExecute() {
        return execute;
    }

    @Override
    public String toString() {
        return (read ? "r" : "-") + (write ? "w" : "-") + (execute ? "x" : "-");
    }
}
