package org.graalvm.vm.x86.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public class PropertyTest {
    @Test
    public void test() {
        assertNotNull(TestOptions.PATH);
        File f = new File(TestOptions.PATH);
        assertTrue(f.exists());
        assertTrue(f.isDirectory());
    }
}
