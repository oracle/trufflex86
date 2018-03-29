package org.graalvm.vm.memory.exception;

import org.graalvm.vm.memory.Memory;
import org.graalvm.vm.memory.MemoryPage;

public class SegmentationViolation extends ArrayIndexOutOfBoundsException {
    private static final long serialVersionUID = 6904011763641924860L;

    private final MemoryPage page;
    private final Memory memory;
    private final long offset;

    public SegmentationViolation(long offset) {
        super((int) offset);
        this.page = null;
        this.memory = null;
        this.offset = offset;
    }

    public SegmentationViolation(Memory memory, long offset) {
        super((int) offset);
        this.page = null;
        this.memory = memory;
        this.offset = offset;
    }

    public SegmentationViolation(MemoryPage page, long offset) {
        super((int) offset);
        this.page = page;
        this.memory = null;
        this.offset = offset;
    }

    public long getAddress() {
        return offset;
    }

    @Override
    public String toString() {
        if (page != null) {
            return String.format(
                            "Invalid memory access at 0x%016X (page base: 0x%016X, page end: 0x%019X)",
                            offset, page.getBase(), page.getEnd());
        } else if (memory != null) {
            return String.format("Invalid memory access at 0x%016X", offset);
        } else {
            return String.format("Invalid memory access at 0x%016X", offset);
        }
    }
}
