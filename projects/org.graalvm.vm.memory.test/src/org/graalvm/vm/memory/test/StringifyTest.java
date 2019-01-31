package org.graalvm.vm.memory.test;

import static org.junit.Assert.assertEquals;

import org.graalvm.vm.memory.util.Stringify;
import org.graalvm.vm.posix.elf.Elf;
import org.junit.Test;

public class StringifyTest {
    @Test
    public void testI8_1() {
        assertEquals("A", Stringify.i8((byte) 'A'));
    }

    @Test
    public void testI8_2() {
        assertEquals("\\r", Stringify.i8((byte) 0x0D));
    }

    @Test
    public void testI8_3() {
        assertEquals("\\n", Stringify.i8((byte) 0x0A));
    }

    @Test
    public void testI8_4() {
        assertEquals("\\x1b", Stringify.i8((byte) 0x1B));
    }

    @Test
    public void testI16_1() {
        assertEquals("A\\n", Stringify.i16((short) 0x410A));
    }

    @Test
    public void testI16_2() {
        assertEquals("MZ", Stringify.i16((short) 0x4D5A));
    }

    @Test
    public void testI16_3() {
        assertEquals("pe", Stringify.i16((short) 0x7065));
    }

    @Test
    public void testI32_1() {
        assertEquals("\\x7fELF", Stringify.i32(Elf.MAGIC));
    }
}
