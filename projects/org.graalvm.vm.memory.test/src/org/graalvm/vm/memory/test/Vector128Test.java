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
package org.graalvm.vm.memory.test;

import static org.junit.Assert.assertEquals;

import org.graalvm.vm.memory.vector.Vector128;
import org.junit.Test;

public class Vector128Test {
    @Test
    public void testSetI64v0() {
        Vector128 vec = new Vector128();
        vec.setI64(0, 0xC0DEBABEDEADBEEFL);
        vec.setI64(1, 0xDEADCAFEBEEFC0FEL);
        assertEquals("0xc0debabedeadbeefdeadcafebeefc0fe", vec.toString());
    }

    @Test
    public void testSetI64v1() {
        Vector128 vec = new Vector128();
        vec.setI64(0, 0xC0DEBABEDEADBEEFL);
        vec.setI64(1, 0xDEADCAFEBEEFC0FEL);
        assertEquals(0xC0DEBABE, vec.getI32(0));
        assertEquals(0xDEADBEEF, vec.getI32(1));
        assertEquals(0xDEADCAFE, vec.getI32(2));
        assertEquals(0xBEEFC0FE, vec.getI32(3));
    }

    @Test
    public void testSetI32v0() {
        Vector128 vec = new Vector128();
        vec.setI32(0, 0xC0DE0001);
        vec.setI32(1, 0xC0DE0002);
        vec.setI32(2, 0xC0DE0003);
        vec.setI32(3, 0xC0DE0004);
        assertEquals("0xc0de0001c0de0002c0de0003c0de0004", vec.toString());
    }

    @Test
    public void testSetI32v1() {
        Vector128 vec = new Vector128();
        vec.setI32(0, 0xC0DE0001);
        vec.setI32(1, 0xC0DE0002);
        vec.setI32(2, 0xC0DE0003);
        vec.setI32(3, 0xC0DE0004);
        assertEquals(0xC0DE0001C0DE0002L, vec.getI64(0));
        assertEquals(0xC0DE0003C0DE0004L, vec.getI64(1));
    }

    @Test
    public void testEq8() {
        Vector128 a = new Vector128(0x45781296ABCDE789L, 0x123456789ABCDEF0L);
        Vector128 b = new Vector128(0x45781296ABFFE789L, 0x123466779ABCDEF0L);
        Vector128 eq = a.eq8(b);
        assertEquals(0b1111101111001111L, eq.byteMaskMSB());
    }

    // 8bit access
    @Test
    public void testGetI8() {
        Vector128 vec = new Vector128(0x45781296ABCDE789L, 0x123456789ABCDEF0L);
        assertEquals((byte) 0x45, vec.getI8(0));
        assertEquals((byte) 0x78, vec.getI8(1));
        assertEquals((byte) 0x12, vec.getI8(2));
        assertEquals((byte) 0x96, vec.getI8(3));
        assertEquals((byte) 0xAB, vec.getI8(4));
        assertEquals((byte) 0xCD, vec.getI8(5));
        assertEquals((byte) 0xE7, vec.getI8(6));
        assertEquals((byte) 0x89, vec.getI8(7));
        assertEquals((byte) 0x12, vec.getI8(8));
        assertEquals((byte) 0x34, vec.getI8(9));
        assertEquals((byte) 0x56, vec.getI8(10));
        assertEquals((byte) 0x78, vec.getI8(11));
        assertEquals((byte) 0x9A, vec.getI8(12));
        assertEquals((byte) 0xBC, vec.getI8(13));
        assertEquals((byte) 0xDE, vec.getI8(14));
        assertEquals((byte) 0xF0, vec.getI8(15));
    }

    // 16bit access
    @Test
    public void testGetI16() {
        Vector128 vec = new Vector128(0x45781296ABCDE789L, 0x123456789ABCDEF0L);
        assertEquals((short) 0x4578, vec.getI16(0));
        assertEquals((short) 0x1296, vec.getI16(1));
        assertEquals((short) 0xABCD, vec.getI16(2));
        assertEquals((short) 0xE789, vec.getI16(3));
        assertEquals((short) 0x1234, vec.getI16(4));
        assertEquals((short) 0x5678, vec.getI16(5));
        assertEquals((short) 0x9ABC, vec.getI16(6));
        assertEquals((short) 0xDEF0, vec.getI16(7));
    }

    @Test
    public void testSetI16v0() {
        Vector128 vec = new Vector128(0x45781296ABCDE789L, 0x123456789ABCDEF0L);
        vec.setI16(0, (short) 0x4224);

        assertEquals((short) 0x4224, vec.getI16(0));
        assertEquals((short) 0x1296, vec.getI16(1));
        assertEquals((short) 0xABCD, vec.getI16(2));
        assertEquals((short) 0xE789, vec.getI16(3));
        assertEquals((short) 0x1234, vec.getI16(4));
        assertEquals((short) 0x5678, vec.getI16(5));
        assertEquals((short) 0x9ABC, vec.getI16(6));
        assertEquals((short) 0xDEF0, vec.getI16(7));
    }

    @Test
    public void testSetI16v1() {
        Vector128 vec = new Vector128(0x45781296ABCDE789L, 0x123456789ABCDEF0L);
        vec.setI16(1, (short) 0x4224);

        assertEquals((short) 0x4578, vec.getI16(0));
        assertEquals((short) 0x4224, vec.getI16(1));
        assertEquals((short) 0xABCD, vec.getI16(2));
        assertEquals((short) 0xE789, vec.getI16(3));
        assertEquals((short) 0x1234, vec.getI16(4));
        assertEquals((short) 0x5678, vec.getI16(5));
        assertEquals((short) 0x9ABC, vec.getI16(6));
        assertEquals((short) 0xDEF0, vec.getI16(7));
    }

    @Test
    public void testSetI16v2() {
        Vector128 vec = new Vector128(0x45781296ABCDE789L, 0x123456789ABCDEF0L);
        vec.setI16(2, (short) 0x4224);

        assertEquals((short) 0x4578, vec.getI16(0));
        assertEquals((short) 0x1296, vec.getI16(1));
        assertEquals((short) 0x4224, vec.getI16(2));
        assertEquals((short) 0xE789, vec.getI16(3));
        assertEquals((short) 0x1234, vec.getI16(4));
        assertEquals((short) 0x5678, vec.getI16(5));
        assertEquals((short) 0x9ABC, vec.getI16(6));
        assertEquals((short) 0xDEF0, vec.getI16(7));
    }

    @Test
    public void testSetI16v3() {
        Vector128 vec = new Vector128(0x45781296ABCDE789L, 0x123456789ABCDEF0L);
        vec.setI16(3, (short) 0x4224);

        assertEquals((short) 0x4578, vec.getI16(0));
        assertEquals((short) 0x1296, vec.getI16(1));
        assertEquals((short) 0xABCD, vec.getI16(2));
        assertEquals((short) 0x4224, vec.getI16(3));
        assertEquals((short) 0x1234, vec.getI16(4));
        assertEquals((short) 0x5678, vec.getI16(5));
        assertEquals((short) 0x9ABC, vec.getI16(6));
        assertEquals((short) 0xDEF0, vec.getI16(7));
    }

    @Test
    public void testSetI16v4() {
        Vector128 vec = new Vector128(0x45781296ABCDE789L, 0x123456789ABCDEF0L);
        vec.setI16(4, (short) 0x4224);

        assertEquals((short) 0x4578, vec.getI16(0));
        assertEquals((short) 0x1296, vec.getI16(1));
        assertEquals((short) 0xABCD, vec.getI16(2));
        assertEquals((short) 0xE789, vec.getI16(3));
        assertEquals((short) 0x4224, vec.getI16(4));
        assertEquals((short) 0x5678, vec.getI16(5));
        assertEquals((short) 0x9ABC, vec.getI16(6));
        assertEquals((short) 0xDEF0, vec.getI16(7));
    }

    @Test
    public void testSetI16v5() {
        Vector128 vec = new Vector128(0x45781296ABCDE789L, 0x123456789ABCDEF0L);
        vec.setI16(5, (short) 0x4224);

        assertEquals((short) 0x4578, vec.getI16(0));
        assertEquals((short) 0x1296, vec.getI16(1));
        assertEquals((short) 0xABCD, vec.getI16(2));
        assertEquals((short) 0xE789, vec.getI16(3));
        assertEquals((short) 0x1234, vec.getI16(4));
        assertEquals((short) 0x4224, vec.getI16(5));
        assertEquals((short) 0x9ABC, vec.getI16(6));
        assertEquals((short) 0xDEF0, vec.getI16(7));
    }

    @Test
    public void testSetI16v6() {
        Vector128 vec = new Vector128(0x45781296ABCDE789L, 0x123456789ABCDEF0L);
        vec.setI16(6, (short) 0x4224);

        assertEquals((short) 0x4578, vec.getI16(0));
        assertEquals((short) 0x1296, vec.getI16(1));
        assertEquals((short) 0xABCD, vec.getI16(2));
        assertEquals((short) 0xE789, vec.getI16(3));
        assertEquals((short) 0x1234, vec.getI16(4));
        assertEquals((short) 0x5678, vec.getI16(5));
        assertEquals((short) 0x4224, vec.getI16(6));
        assertEquals((short) 0xDEF0, vec.getI16(7));
    }

    @Test
    public void testSetI16v7() {
        Vector128 vec = new Vector128(0x45781296ABCDE789L, 0x123456789ABCDEF0L);
        vec.setI16(7, (short) 0x4224);

        assertEquals((short) 0x4578, vec.getI16(0));
        assertEquals((short) 0x1296, vec.getI16(1));
        assertEquals((short) 0xABCD, vec.getI16(2));
        assertEquals((short) 0xE789, vec.getI16(3));
        assertEquals((short) 0x1234, vec.getI16(4));
        assertEquals((short) 0x5678, vec.getI16(5));
        assertEquals((short) 0x9ABC, vec.getI16(6));
        assertEquals((short) 0x4224, vec.getI16(7));
    }

    @Test
    public void testEq8v0() {
        Vector128 values = new Vector128(0x6854000a21646c72L, 0x6f77206f6c6c6548L);
        Vector128 eq = values.eq8(Vector128.ZERO);
        Vector128 ref = new Vector128(0x0000ff0000000000L, 0x0000000000000000L);
        assertEquals(ref, eq);
    }

    @Test
    public void testByteMaskMSBv0() {
        Vector128 value = new Vector128(0x0000ff0000000000L, 0x0000000000000000L);
        long mask = value.byteMaskMSB();
        long ref = 0x0000000000002000;
        assertEquals(ref, mask);
    }

    @Test
    public void testByteMaskMSBv1() {
        Vector128 values = new Vector128(0x6854000a21646c72L, 0x6f77206f6c6c6548L);
        Vector128 eq = values.eq8(Vector128.ZERO);
        long mask = eq.byteMaskMSB();
        long ref = 0x0000000000002000;
        assertEquals(ref, mask);
    }

    @Test
    public void testShlBytesv0() {
        Vector128 vec = new Vector128(0x0011223344556677L, 0x8899AABBCCDDEEFFL);
        Vector128 shifted = vec.shlBytes(1);
        Vector128 ref = new Vector128(0x1122334455667788L, 0x99AABBCCDDEEFF00L);
        assertEquals(ref, shifted);
    }

    @Test
    public void testShlBytesv1() {
        Vector128 vec = new Vector128(0x0011223344556677L, 0x8899AABBCCDDEEFFL);
        Vector128 shifted = vec.shlBytes(2);
        Vector128 ref = new Vector128(0x2233445566778899L, 0xAABBCCDDEEFF0000L);
        assertEquals(ref, shifted);
    }

    @Test
    public void testShlBytesv2() {
        Vector128 vec = new Vector128(0x0011223344556677L, 0x8899AABBCCDDEEFFL);
        Vector128 shifted = vec.shlBytes(3);
        Vector128 ref = new Vector128(0x33445566778899AAL, 0xBBCCDDEEFF000000L);
        assertEquals(ref, shifted);
    }

    @Test
    public void testGe() {
        Vector128 vec1 = new Vector128(0x4014000000000000L, 0x4014000000000000L);
        Vector128 vec2 = new Vector128(0x7ff8000000000000L, 0x4014000000000000L);
        Vector128 act = vec1.geF64(vec2);
        Vector128 ref = new Vector128(0xffffffffffffffffL, 0xffffffffffffffffL);
        assertEquals(ref, act);
    }

    @Test
    public void testGt8() {
        Vector128 vec1 = new Vector128(0x0000000000000000L, 0x0000000000000000L);
        Vector128 vec2 = new Vector128(0xf00df409f805fc01L, 0xf00df409f805fc01L);
        Vector128 act = vec1.gt8(vec2);
        Vector128 ref = new Vector128(0xff00ff00ff00ff00L, 0xff00ff00ff00ff00L);
        assertEquals(ref, act);
    }

    @Test
    public void testShrPackedI16() {
        Vector128 vec = new Vector128(0xffffffffffffffffL, 0xffffffffffffffffL);
        Vector128 act = vec.shrPackedI16(8);
        Vector128 ref = new Vector128(0x00ff00ff00ff00ffL, 0x00ff00ff00ff00ffL);
        assertEquals(ref, act);
    }
}
