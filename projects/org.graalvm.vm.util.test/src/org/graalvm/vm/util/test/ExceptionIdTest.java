package org.graalvm.vm.util.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.graalvm.vm.util.exception.ExceptionId;
import org.graalvm.vm.util.exception.ExceptionIdRegistry;
import org.graalvm.vm.util.exception.Messages;
import org.graalvm.vm.util.log.Levels;
import org.junit.Test;

public class ExceptionIdTest {
	public static final ExceptionId exception1 = new ExceptionId("CEVT0001W", "Test warning 1");
	public static final ExceptionId exception2 = new ExceptionId("CEVCT0001I", "Test info 1");
	public static final ExceptionId exception3 = new ExceptionId("CEVCT0002I", "Test info 2 with param {0}");

	@Test
	public void testSubsystemId1() {
		assertEquals("CEVT", exception1.getMessageSubsystem());
	}

	@Test
	public void testSubsystemId2() {
		assertEquals("CEVCT", exception2.getMessageSubsystem());
	}

	@Test
	public void testMessageId1() {
		assertEquals("0001", exception1.getMessageId());
	}

	@Test
	public void testMessageId2() {
		assertEquals("0001", exception2.getMessageId());
	}

	@Test
	public void testLevel1() {
		assertEquals(Levels.WARNING, exception1.getLevel());
	}

	@Test
	public void testLevel2() {
		assertEquals(Levels.INFO, exception2.getLevel());
	}

	@Test
	public void testId1() {
		assertEquals("CEVT0001W", exception1.getId());
	}

	@Test
	public void testId2() {
		assertEquals("CEVCT0001I", exception2.getId());
	}

	@Test
	public void testMessage1() {
		assertEquals("Test warning 1", exception1.getMessage());
	}

	@Test
	public void testMessage2() {
		assertEquals("Test info 1", exception2.getMessage());
	}

	@Test
	public void testFormat1() {
		assertEquals("CEVT0001W: Test warning 1", exception1.format());
	}

	@Test
	public void testFormat2() {
		assertEquals("CEVCT0001I: Test info 1", exception2.format());
	}

	@Test
	public void testFormat3() {
		assertEquals("CEVCT0001I: Test info 1: test", exception2.format("test"));
	}

	@Test
	public void testFormat4() {
		assertEquals("CEVCT0001I: Test info 1: test, junit", exception2.format("test", "junit"));
	}

	@Test
	public void testFormat5() {
		assertEquals("CEVCT0001I: Test info 1: test, junit, java", exception2.format("test", "junit", "java"));
	}

	@Test
	public void testFormat6() {
		assertEquals("CEVCT0002I: Test info 2 with param test", exception3.format("test"));
	}

	@Test
	public void testFormat7() {
		assertEquals("CEVCT0002I: Test info 2 with param test", exception3.format("test", "junit"));
	}

	@Test
	public void testResourceNotFound() {
		assertEquals("CEVU0012W: Resource \"the-resource.xml\" not found for class com.everyware.the.clazz",
				Messages.NO_RESOURCE.format("com.everyware.the.clazz", "the-resource.xml"));
	}

	@Test
	public void testMessageRegistry() {
		Set<ExceptionId> ids = ExceptionIdRegistry.getExceptionIds();
		assertTrue(ids.contains(exception1));
		assertTrue(ids.contains(exception2));
		assertTrue(ids.contains(exception3));
		assertTrue(ids.contains(Messages.NO_RESOURCE));
	}
}
