package org.graalvm.vm.util.test;

import static org.junit.Assert.assertEquals;

import org.graalvm.vm.util.StringUtils;
import org.junit.Test;

public class StringUtilsTest {
	@Test
	public void testRepeat1() {
		assertEquals("     ", StringUtils.repeat(" ", 5));
	}

	@Test
	public void testRepeat2() {
		assertEquals("-------", StringUtils.repeat("-", 7));
	}

	@Test
	public void testRepeat3() {
		assertEquals("+-+-+-+-+-+-", StringUtils.repeat("+-", 6));
	}

	@Test
	public void testFit1() {
		assertEquals("Hell", StringUtils.fit("Hello World", 4));
	}

	@Test
	public void testFit2() {
		assertEquals("com...tils", StringUtils.fit("com.everyware.util.StringUtils", 10));
	}

	@Test
	public void testFit3() {
		assertEquals("com...tils", StringUtils.fit("com.everyware.utils.StringUtils", 10));
	}

	@Test
	public void testFit4() {
		assertEquals("com....tils", StringUtils.fit("com.everyware.util.StringUtils", 11));
	}

	@Test
	public void testFit5() {
		assertEquals("com....tils", StringUtils.fit("com.everyware.utils.StringUtils", 11));
	}

	@Test
	public void testFit6() {
		assertEquals("Hello World   ", StringUtils.fit("Hello World", 14));
	}

	@Test
	public void testPad1() {
		assertEquals("H...", StringUtils.pad("Hello World", 4));
	}

	@Test
	public void testPad2() {
		assertEquals("Hel", StringUtils.pad("Hello World", 3));
	}

	@Test
	public void testPad3() {
		assertEquals("Hello", StringUtils.pad("Hello", 5));
	}

	@Test
	public void testPad4() {
		assertEquals("Hell...", StringUtils.pad("Hello World", 7));
	}

	@Test
	public void testPad5() {
		assertEquals("Hello World   ", StringUtils.pad("Hello World", 14));
	}

	@Test
	public void testRpad1() {
		assertEquals("rld", StringUtils.rpad("Hello World", 3));
	}

	@Test
	public void testRpad2() {
		assertEquals("...orld", StringUtils.rpad("Hello World", 7));
	}

	@Test
	public void testRpad3() {
		assertEquals("...d", StringUtils.rpad("Hello World", 4));
	}

	@Test
	public void testTab1() {
		assertEquals("te      st", StringUtils.tab("te\tst", 8));
	}

	@Test
	public void testTab2() {
		assertEquals("        test", StringUtils.tab("\ttest", 8));
	}

	@Test
	public void testTab3() {
		assertEquals("ttttttt est", StringUtils.tab("ttttttt\test", 8));
	}

	@Test
	public void testTab4() {
		assertEquals("tttttttt        est", StringUtils.tab("tttttttt\test", 8));
	}
}
