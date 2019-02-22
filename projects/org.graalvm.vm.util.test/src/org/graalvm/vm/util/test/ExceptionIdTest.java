/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
        assertEquals("UTIL0002W: Resource \"the-resource.xml\" not found for class com.everyware.the.clazz",
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
