package org.graalvm.vm.posix.api.linux;

import org.graalvm.vm.posix.api.Errno;
import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.posix.api.PosixPointer;

public class Futex {
	// @formatter:off
	// Second argument to futex syscall
	public static final int FUTEX_WAIT              = 0;
	public static final int FUTEX_WAKE              = 1;
	public static final int FUTEX_FD                = 2;
	public static final int FUTEX_REQUEUE           = 3;
	public static final int FUTEX_CMP_REQUEUE       = 4;
	public static final int FUTEX_WAKE_OP           = 5;
	public static final int FUTEX_LOCK_PI           = 6;
	public static final int FUTEX_UNLOCK_PI         = 7;
	public static final int FUTEX_TRYLOCK_PI        = 8;
	public static final int FUTEX_WAIT_BITSET       = 9;
	public static final int FUTEX_WAKE_BITSET       = 10;
	public static final int FUTEX_WAIT_REQUEUE_PI   = 11;
	public static final int FUTEX_CMP_REQUEUE_PI    = 12;

	public static final int FUTEX_PRIVATE_FLAG      = 128;
	public static final int FUTEX_CLOCK_REALTIME    = 256;
	public static final int FUTEX_CMD_MASK          = ~(FUTEX_PRIVATE_FLAG | FUTEX_CLOCK_REALTIME);

	public static final int FUTEX_WAIT_PRIVATE      = (FUTEX_WAIT | FUTEX_PRIVATE_FLAG);
	public static final int FUTEX_WAKE_PRIVATE      = (FUTEX_WAKE | FUTEX_PRIVATE_FLAG);
	public static final int FUTEX_REQUEUE_PRIVATE   = (FUTEX_REQUEUE | FUTEX_PRIVATE_FLAG);
	public static final int FUTEX_CMP_REQUEUE_PRIVATE = (FUTEX_CMP_REQUEUE | FUTEX_PRIVATE_FLAG);
	public static final int FUTEX_WAKE_OP_PRIVATE   = (FUTEX_WAKE_OP | FUTEX_PRIVATE_FLAG);
	public static final int FUTEX_LOCK_PI_PRIVATE   = (FUTEX_LOCK_PI | FUTEX_PRIVATE_FLAG);
	public static final int FUTEX_UNLOCK_PI_PRIVATE = (FUTEX_UNLOCK_PI | FUTEX_PRIVATE_FLAG);
	public static final int FUTEX_TRYLOCK_PI_PRIVATE = (FUTEX_TRYLOCK_PI | FUTEX_PRIVATE_FLAG);
	public static final int FUTEX_WAIT_BITSET_PRIVATE       = (FUTEX_WAIT_BITSET | FUTEX_PRIVATE_FLAG);
	public static final int FUTEX_WAKE_BITSET_PRIVATE       = (FUTEX_WAKE_BITSET | FUTEX_PRIVATE_FLAG);
	public static final int FUTEX_WAIT_REQUEUE_PI_PRIVATE   = (FUTEX_WAIT_REQUEUE_PI | FUTEX_PRIVATE_FLAG);
	public static final int FUTEX_CMP_REQUEUE_PI_PRIVATE    = (FUTEX_CMP_REQUEUE_PI | FUTEX_PRIVATE_FLAG);

	/*
	 * Are there any waiters for this robust futex:
	 */
	public static final int FUTEX_WAITERS           = 0x80000000;

	/*
	 * The kernel signals via this bit that a thread holding a futex
	 * has exited without unlocking the futex. The kernel also does
	 * a FUTEX_WAKE on such futexes, after setting the bit, to wake
	 * up any possible waiters:
	 */
	public static final int FUTEX_OWNER_DIED        = 0x40000000;

	/*
	 * The rest of the robust-futex field is for the TID:
	 */
	public static final int FUTEX_TID_MASK          = 0x3fffffff;

	/*
	 * This limit protects against a deliberately circular list.
	 * (Not worth introducing an rlimit for it)
	 */
	public static final int ROBUST_LIST_LIMIT       = 2048;

	/*
	 * bitset with all bits set for the FUTEX_xxx_BITSET OPs to request a
	 * match of any bit.
	 */
	public static final int FUTEX_BITSET_MATCH_ANY  = 0xffffffff;


	public static final int FUTEX_OP_SET            = 0;       /* *(int *)UADDR2 = OPARG; */
	public static final int FUTEX_OP_ADD            = 1;       /* *(int *)UADDR2 += OPARG; */
	public static final int FUTEX_OP_OR             = 2;       /* *(int *)UADDR2 |= OPARG; */
	public static final int FUTEX_OP_ANDN           = 3;       /* *(int *)UADDR2 &= ~OPARG; */
	public static final int FUTEX_OP_XOR            = 4;       /* *(int *)UADDR2 ^= OPARG; */

	public static final int FUTEX_OP_OPARG_SHIFT    = 8;       /* Use (1 << OPARG) instead of OPARG.  */

	public static final int FUTEX_OP_CMP_EQ         = 0;       /* if (oldval == CMPARG) wake */
	public static final int FUTEX_OP_CMP_NE         = 1;       /* if (oldval != CMPARG) wake */
	public static final int FUTEX_OP_CMP_LT         = 2;       /* if (oldval < CMPARG) wake */
	public static final int FUTEX_OP_CMP_LE         = 3;       /* if (oldval <= CMPARG) wake */
	public static final int FUTEX_OP_CMP_GT         = 4;       /* if (oldval > CMPARG) wake */
	public static final int FUTEX_OP_CMP_GE         = 5;       /* if (oldval >= CMPARG) wake */

	/* FUTEX_WAKE_OP will perform atomically
	   int oldval = *(int *)UADDR2;
	   *(int *)UADDR2 = oldval OP OPARG;
	   if (oldval CMP CMPARG)
	     wake UADDR2;  */

	public static int FUTEX_OP(int op, int oparg, int cmp, int cmparg) {
		return (((op & 0xf) << 28) | ((cmp & 0xf) << 24) | ((oparg & 0xfff) << 12) | (cmparg & 0xfff));
	}
	// @formatter:on

	@SuppressWarnings("unused")
	public int futex(PosixPointer uaddr, int futex_op, int val, PosixPointer timeout, PosixPointer uaddr2, int val3)
			throws PosixException {
		switch(futex_op) {
		case FUTEX_WAKE:
		case FUTEX_WAKE_PRIVATE:
			return 0;
		default:
			throw new PosixException(Errno.ENOSYS);
		}
	}

	public static String op(int futex_op) {
		switch(futex_op) {
		case FUTEX_WAIT:
			return "FUTEX_WAIT";
		case FUTEX_WAKE:
			return "FUTEX_WAKE";
		case FUTEX_FD:
			return "FUTEX_FD";
		case FUTEX_REQUEUE:
			return "FUTEX_REQUEUE";
		case FUTEX_CMP_REQUEUE:
			return "FUTEX_CMP_REQUEUE";
		case FUTEX_WAKE_OP:
			return "FUTEX_WAKE_OP";
		case FUTEX_LOCK_PI:
			return "FUTEX_LOCK_PI";
		case FUTEX_UNLOCK_PI:
			return "FUTEX_UNLOCK_PI";
		case FUTEX_TRYLOCK_PI:
			return "FUTEX_TRYLOCK_PI";
		case FUTEX_WAIT_BITSET:
			return "FUTEX_WAIT_BITSET";
		case FUTEX_WAKE_BITSET:
			return "FUTEX_WAKE_BITSET";
		case FUTEX_WAIT_REQUEUE_PI:
			return "FUTEX_WAIT_REQUEUE_PI";
		case FUTEX_CMP_REQUEUE_PI:
			return "FUTEX_CMP_REQUEUE_PI";
		case FUTEX_WAIT_PRIVATE:
			return "FUTEX_WAIT_PRIVATE";
		case FUTEX_WAKE_PRIVATE:
			return "FUTEX_WAKE_PRIVATE";
		case FUTEX_REQUEUE_PRIVATE:
			return "FUTEX_REQUEUE_PRIVATE";
		case FUTEX_CMP_REQUEUE_PRIVATE:
			return "FUTEX_CMP_REQUEUE_PRIVATE";
		case FUTEX_WAKE_OP_PRIVATE:
			return "FUTEX_CMP_REQUEUE_PRIVATE";
		case FUTEX_LOCK_PI_PRIVATE:
			return "FUTEX_LOCK_PI_PRIVATE";
		case FUTEX_UNLOCK_PI_PRIVATE:
			return "FUTEX_UNLOCK_PI_PRIVATE";
		case FUTEX_TRYLOCK_PI_PRIVATE:
			return "FUTEX_TRYLOCK_PI_PRIVATE";
		case FUTEX_WAIT_BITSET_PRIVATE:
			return "FUTEX_WAIT_BITSET_PRIVATE";
		case FUTEX_WAKE_BITSET_PRIVATE:
			return "FUTEX_WAKE_BITSET_PRIVATE";
		case FUTEX_WAIT_REQUEUE_PI_PRIVATE:
			return "FUTEX_WAIT_REQUEUE_PI_PRIVATE";
		case FUTEX_CMP_REQUEUE_PI_PRIVATE:
			return "FUTEX_CMP_REQUEUE_PI_PRIVATE";
		default:
			return Integer.toString(futex_op);
		}
	}
}
