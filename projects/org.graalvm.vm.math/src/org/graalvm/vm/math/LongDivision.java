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
package org.graalvm.vm.math;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class LongDivision {
	public static class Result {
		private static final long INVALID = 0x8000000000000000L;

		public static final Result OVERFLOW = new Result(INVALID, INVALID);

		public final long quotient;
		public final long remainder;

		Result(long quotient, long remainder) {
			this.quotient = quotient;
			this.remainder = remainder;
		}

		public boolean isInvalid() {
			return quotient == INVALID && remainder == INVALID;
		}

		@Override
		public boolean equals(Object o) {
			if(o == null) {
				return false;
			}
			if(!(o instanceof Result)) {
				return false;
			}
			Result r = (Result) o;
			return r.quotient == quotient && r.remainder == remainder;
		}

		@Override
		public int hashCode() {
			return (int) (quotient ^ remainder);
		}

		@Override
		public String toString() {
			return "Result[q=" + quotient + ",r=" + remainder + "]";
		}
	}

	public static Result divu128by64(long a1, long a0, long b) {
		if(a1 == 0 && a0 > 0 && b > 0) {
			return new Result(a0 / b, a0 % b);
		}

		BigInteger x = u128(a1, a0);
		BigInteger y = u64(b);
		BigInteger[] result = x.divideAndRemainder(y);

		BigInteger q = result[0];
		BigInteger r = result[1];
		if(q.bitLength() > 64) {
			return Result.OVERFLOW;
		} else {
			return new Result(q.longValue(), r.longValue());
		}
	}

	public static Result divs128by64(long a1, long a0, long b) {
		if(a1 == 0 && a0 > 0) {
			return new Result(a0 / b, a0 % b);
		}

		BigInteger x = s128(a1, a0);
		BigInteger y = BigInteger.valueOf(b);
		BigInteger[] result = x.divideAndRemainder(y);

		BigInteger q = result[0];
		BigInteger r = result[1];
		if(q.bitCount() > 64) {
			return Result.OVERFLOW;
		} else {
			return new Result(q.longValue(), r.longValue());
		}
	}

	private static BigInteger u64(long x) {
		byte[] bytes = new byte[Long.BYTES + 1];
		ByteBuffer.wrap(bytes, 1, Long.BYTES).order(ByteOrder.BIG_ENDIAN).putLong(x);
		return new BigInteger(bytes);
	}

	private static BigInteger u128(long h, long l) {
		byte[] bytes = new byte[2 * Long.BYTES + 1];
		ByteBuffer.wrap(bytes, 1, 2 * Long.BYTES).order(ByteOrder.BIG_ENDIAN).putLong(h).putLong(l);
		return new BigInteger(bytes);
	}

	private static BigInteger s128(long h, long l) {
		byte[] bytes = new byte[2 * Long.BYTES];
		ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).putLong(h).putLong(l);
		return new BigInteger(bytes);
	}
}
