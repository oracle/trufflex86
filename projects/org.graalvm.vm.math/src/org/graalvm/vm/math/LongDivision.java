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
