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
package org.graalvm.vm.util.io;

public class Endianess {
	public static short get16bitBE(byte[] data) {
		return get16bitBE(data, 0);
	}

	public static short get16bitBE(byte[] data, int offset) {
		return (short) (Byte.toUnsignedInt(data[offset]) << 8 | Byte.toUnsignedInt(data[offset + 1]));
	}

	public static int get16bitBEu(byte[] data) {
		return Short.toUnsignedInt(get16bitBE(data));
	}

	public static int get16bitBEu(byte[] data, int offset) {
		return Short.toUnsignedInt(get16bitBE(data, offset));
	}

	public static int get32bitBE(byte[] data) {
		return get32bitBE(data, 0);
	}

	public static long get32bitBEu(byte[] data) {
		return Integer.toUnsignedLong(get32bitBE(data));
	}

	public static int get32bitBE(byte[] data, int offset) {
		return Byte.toUnsignedInt(data[offset]) << 24 | Byte.toUnsignedInt(data[offset + 1]) << 16 |
				Byte.toUnsignedInt(data[offset + 2]) << 8 | Byte.toUnsignedInt(data[offset + 3]);
	}

	public static long get32bitBEu(byte[] data, int offset) {
		return Integer.toUnsignedLong(get32bitBE(data, offset));
	}

	public static long get64bitBE(byte[] data) {
		return get64bitBE(data, 0);
	}

	public static long get64bitBE(byte[] data, int offset) {
		return Byte.toUnsignedLong(data[offset]) << 56 |
				Byte.toUnsignedLong(data[offset + 1]) << 48 |
				Byte.toUnsignedLong(data[offset + 2]) << 40 |
				Byte.toUnsignedLong(data[offset + 3]) << 32 |
				Byte.toUnsignedLong(data[offset + 4]) << 24 |
				Byte.toUnsignedLong(data[offset + 5]) << 16 |
				Byte.toUnsignedLong(data[offset + 6]) << 8 |
				Byte.toUnsignedLong(data[offset + 7]);
	}

	public static short get16bitLE(byte[] data) {
		return get16bitLE(data, 0);
	}

	public static short get16bitLE(byte[] data, int offset) {
		return (short) (Byte.toUnsignedInt(data[offset]) | Byte.toUnsignedInt(data[offset + 1]) << 8);
	}

	public static int get32bitLE(byte[] data) {
		return get32bitLE(data, 0);
	}

	public static int get32bitLE(byte[] data, int offset) {
		return Byte.toUnsignedInt(data[offset]) | Byte.toUnsignedInt(data[offset + 1]) << 8 |
				Byte.toUnsignedInt(data[offset + 2]) << 16 | Byte.toUnsignedInt(data[offset + 3]) << 24;
	}

	public static long get64bitLE(byte[] data) {
		return get64bitLE(data, 0);
	}

	public static long get64bitLE(byte[] data, int offset) {
		return Byte.toUnsignedLong(data[offset]) |
				Byte.toUnsignedLong(data[offset + 1]) << 8 |
				Byte.toUnsignedLong(data[offset + 2]) << 16 |
				Byte.toUnsignedLong(data[offset + 3]) << 24 |
				Byte.toUnsignedLong(data[offset + 4]) << 32 |
				Byte.toUnsignedLong(data[offset + 5]) << 40 |
				Byte.toUnsignedLong(data[offset + 6]) << 48 |
				Byte.toUnsignedLong(data[offset + 7]) << 56;
	}

	public static byte[] set16bitBE(byte[] data, int offset, short value) {
		data[offset] = (byte) (value >> 8);
		data[offset + 1] = (byte) value;
		return data;
	}

	public static byte[] set32bitBE(byte[] data, int offset, int value) {
		data[offset] = (byte) (value >> 24);
		data[offset + 1] = (byte) (value >> 16);
		data[offset + 2] = (byte) (value >> 8);
		data[offset + 3] = (byte) value;
		return data;
	}

	public static byte[] set32bitBE(byte[] data, int offset, float value) {
		int bits = Float.floatToRawIntBits(value);
		return set32bitBE(data, offset, bits);
	}

	public static byte[] set64bitBE(byte[] data, int offset, long value) {
		data[offset] = (byte) (value >> 56);
		data[offset + 1] = (byte) (value >> 48);
		data[offset + 2] = (byte) (value >> 40);
		data[offset + 3] = (byte) (value >> 32);
		data[offset + 4] = (byte) (value >> 24);
		data[offset + 5] = (byte) (value >> 16);
		data[offset + 6] = (byte) (value >> 8);
		data[offset + 7] = (byte) value;
		return data;
	}

	public static byte[] set64bitBE(byte[] data, int offset, double value) {
		long bits = Double.doubleToRawLongBits(value);
		return set64bitBE(data, offset, bits);
	}

	public static byte[] set16bitLE(byte[] data, int offset, short value) {
		data[offset] = (byte) value;
		data[offset + 1] = (byte) (value >> 8);
		return data;
	}

	public static byte[] set32bitLE(byte[] data, int offset, int value) {
		data[offset] = (byte) value;
		data[offset + 1] = (byte) (value >> 8);
		data[offset + 2] = (byte) (value >> 16);
		data[offset + 3] = (byte) (value >> 24);
		return data;
	}

	public static byte[] set32bitLE(byte[] data, int offset, float value) {
		int bits = Float.floatToRawIntBits(value);
		return set32bitLE(data, offset, bits);
	}

	public static byte[] set64bitLE(byte[] data, int offset, long value) {
		data[offset] = (byte) value;
		data[offset + 1] = (byte) (value >> 8);
		data[offset + 2] = (byte) (value >> 16);
		data[offset + 3] = (byte) (value >> 24);
		data[offset + 4] = (byte) (value >> 32);
		data[offset + 5] = (byte) (value >> 40);
		data[offset + 6] = (byte) (value >> 48);
		data[offset + 7] = (byte) (value >> 56);
		return data;
	}

	public static byte[] set64bitLE(byte[] data, int offset, double value) {
		long bits = Double.doubleToRawLongBits(value);
		return set64bitLE(data, offset, bits);
	}

	public static byte[] set16bitBE(byte[] data, short value) {
		return set16bitBE(data, 0, value);
	}

	public static byte[] set32bitBE(byte[] data, int value) {
		return set32bitBE(data, 0, value);
	}

	public static byte[] set32bitBE(byte[] data, float value) {
		return set32bitBE(data, 0, value);
	}

	public static byte[] set64bitBE(byte[] data, long value) {
		return set64bitBE(data, 0, value);
	}

	public static byte[] set64bitBE(byte[] data, double value) {
		return set64bitBE(data, 0, value);
	}

	public static byte[] set16bitLE(byte[] data, short value) {
		return set16bitLE(data, 0, value);
	}

	public static byte[] set32bitLE(byte[] data, int value) {
		return set32bitLE(data, 0, value);
	}

	public static byte[] set32bitLE(byte[] data, float value) {
		return set32bitLE(data, 0, value);
	}

	public static byte[] set64bitLE(byte[] data, long value) {
		return set64bitLE(data, 0, value);
	}

	public static byte[] set64bitLE(byte[] data, double value) {
		return set64bitLE(data, 0, value);
	}
}
