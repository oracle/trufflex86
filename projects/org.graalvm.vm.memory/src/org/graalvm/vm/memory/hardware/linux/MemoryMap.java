package org.graalvm.vm.memory.hardware.linux;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemoryMap {
    private List<MemorySegment> segments;

    public MemoryMap() throws IOException {
        this("/proc/self/maps");
    }

    public MemoryMap(int pid) throws IOException {
        this("/proc/" + pid + "/maps");
    }

    public MemoryMap(String filename) throws IOException {
        segments = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(filename));
        for (String line : lines) {
            String[] tokens = line.split("\\s+");
            String[] addrs = tokens[0].split("-");
            long start = Long.parseUnsignedLong(addrs[0], 16);
            long end = Long.parseUnsignedLong(addrs[1], 16);
            String permissions = tokens[1];
            long offset = Long.parseUnsignedLong(tokens[2], 16);
            String name = "";
            if (tokens.length == 6) {
                name = tokens[5];
            }
            MemorySegment segment = new MemorySegment(start, end, permissions, offset, name);
            segments.add(segment);
        }
    }

    public List<MemorySegment> getSegments() {
        return Collections.unmodifiableList(segments);
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (MemorySegment segment : segments) {
            buf.append(segment).append("\n");
        }
        if (buf.length() > 0) {
            return buf.substring(0, buf.length() - 1);
        } else {
            return buf.toString();
        }
    }
}
