package org.graalvm.vm.math.test;

import static org.junit.Assert.assertEquals;

import org.graalvm.vm.math.Addition;
import org.junit.Test;

public class AdditionTest {
    private static void test(int x, int y, boolean cf, boolean expectedCf) {
        boolean c = Addition.carry(x, y, cf);
        assertEquals(expectedCf, c);
    }

    @Test
    public void test() {
        test(0x00000000, 0x00000000, false, false);
        test(0x00000000, 0x00000000, true, false);
        test(0x00000d0c, 0x00000000, true, false);
        test(0x00000d0c, 0x00000d0c, true, false);
        test(0x00000000, 0x00000d0c, true, false);
        test(0x00000d0c, 0x00000000, false, false);
        test(0x00000d0c, 0x00000d0c, false, false);
        test(0x00000000, 0x00000d0c, false, false);
        test(0xffffffff, 0x00000000, false, false);
        test(0xffffffff, 0x00000001, false, true);
        test(0xffffffff, 0x00000d0c, false, true);
        test(0xffffffff, 0x80000000, false, true);
        test(0xffffffff, 0xffffffff, false, true);
        test(0xffffffff, 0x00000000, true, true);
        test(0xffffffff, 0x00000001, true, true);
        test(0xffffffff, 0x00000d0c, true, true);
        test(0xffffffff, 0x80000000, true, true);
        test(0xffffffff, 0xffffffff, true, true);
        test(0x80000000, 0x00000000, false, false);
        test(0x80000000, 0x00000d0c, false, false);
        test(0x80000000, 0x80000000, false, true);
        test(0x80000000, 0xffffffff, false, true);
        test(0x80000000, 0x00000000, true, false);
        test(0x80000000, 0x00000d0c, true, false);
        test(0x80000000, 0x80000000, true, true);
        test(0x80000000, 0xffffffff, true, true);
    }
}
