package org.graalvm.vm.x86.test.trace;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.graalvm.vm.util.io.BEOutputStream;
import org.graalvm.vm.util.io.WordOutputStream;
import org.graalvm.vm.x86.node.debug.trace.LocationRecord;
import org.junit.Test;

public class LocationRecordTest {
    @Test
    public void test1() throws IOException {
        LocationRecord record = new LocationRecord(null, null, 42, 3141592, null, null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        WordOutputStream wordOut = new BEOutputStream(out);
        record.write(wordOut);
    }

    @Test
    public void test2() throws IOException {
        LocationRecord record = new LocationRecord(null, null, 42, 3141592, new byte[]{(byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF}, new String[]{"beef", "raw"});
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        WordOutputStream wordOut = new BEOutputStream(out);
        record.write(wordOut);
    }

    @Test
    public void test3() throws IOException {
        LocationRecord record = new LocationRecord("filename", "symbol", 42, 3141592, new byte[]{(byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF}, new String[]{"beef", "raw"});
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        WordOutputStream wordOut = new BEOutputStream(out);
        record.write(wordOut);
    }
}
