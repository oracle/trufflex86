#include "qemu-common.h"

#define TEST_XCHG(op, size, opconst) {					       \
	long op0, op1;							       \
	op0 = i2l(0x12345678);						       \
	op1 = i2l(0xfbca7654);						       \
	asm(#op " %" size "0, %" size "1"				       \
			: "=q"(op0), opconst(op1)			       \
			: "0"(op0));					       \
	printf("%-10s A=" FMTLX " B=" FMTLX "\n", #op, op0, op1);	       \
}

#define TEST_CMPXCHG(op, size, opconst, eax) {				       \
	long op0, op1, op2, flags;					       \
	op0 = i2l(0x12345678);						       \
	op1 = i2l(0xfbca7654);						       \
	op2 = i2l(eax);							       \
	flags = 0;							       \
	asm("push %5\n\t"						       \
	    "popf\n\t" #op " %" size "0, %" size "1\n\t"		       \
	    "pushf\n\t"							       \
	    "pop %2\n\t"						       \
	    : "=q"(op0), opconst(op1), "=r"(flags)			       \
	    : "0"(op0), "a"(op2), "2"(flags));				       \
	printf("%-10s EAX=" FMTLX " A=" FMTLX " C=" FMTLX " F=%04lx\n", #op,   \
			op2, op0, op1, flags &CC_MASK);			       \
}

int main(void)
{
	TEST_XCHG(xchgq, "", "+q");
	TEST_XCHG(xchgl, "k", "+q");
	TEST_XCHG(xchgw, "w", "+q");
	TEST_XCHG(xchgb, "b", "+q");

	TEST_XCHG(xchgq, "", "=m");
	TEST_XCHG(xchgl, "k", "+m");
	TEST_XCHG(xchgw, "w", "+m");
	TEST_XCHG(xchgb, "b", "+m");

	TEST_XCHG(xaddq, "", "+q");
	TEST_XCHG(xaddl, "k", "+q");
	TEST_XCHG(xaddw, "w", "+q");
	TEST_XCHG(xaddb, "b", "+q");

	{
		int res;
		res = 0x12345678;
		asm("xaddl %1, %0" : "=r"(res) : "0"(res));
		printf("xaddl same res=%08x\n", res);
	}

	TEST_XCHG(xaddq, "", "+m");
	TEST_XCHG(xaddl, "k", "+m");
	TEST_XCHG(xaddw, "w", "+m");
	TEST_XCHG(xaddb, "b", "+m");

	TEST_CMPXCHG(cmpxchgq, "", "+q", 0xfbca7654);
	TEST_CMPXCHG(cmpxchgl, "k", "+q", 0xfbca7654);
	TEST_CMPXCHG(cmpxchgw, "w", "+q", 0xfbca7654);
	TEST_CMPXCHG(cmpxchgb, "b", "+q", 0xfbca7654);

	TEST_CMPXCHG(cmpxchgq, "", "+q", 0xfffefdfc);
	TEST_CMPXCHG(cmpxchgl, "k", "+q", 0xfffefdfc);
	TEST_CMPXCHG(cmpxchgw, "w", "+q", 0xfffefdfc);
	TEST_CMPXCHG(cmpxchgb, "b", "+q", 0xfffefdfc);

	TEST_CMPXCHG(cmpxchgq, "", "+m", 0xfbca7654);
	TEST_CMPXCHG(cmpxchgl, "k", "+m", 0xfbca7654);
	TEST_CMPXCHG(cmpxchgw, "w", "+m", 0xfbca7654);
	TEST_CMPXCHG(cmpxchgb, "b", "+m", 0xfbca7654);

	TEST_CMPXCHG(cmpxchgq, "", "+m", 0xfffefdfc);
	TEST_CMPXCHG(cmpxchgl, "k", "+m", 0xfffefdfc);
	TEST_CMPXCHG(cmpxchgw, "w", "+m", 0xfffefdfc);
	TEST_CMPXCHG(cmpxchgb, "b", "+m", 0xfffefdfc);

#if 0
	{
		uint64_t op0, op1, op2;
		long eax, edx;
		long i, eflags;

		for(i = 0; i < 2; i++) {
			op0 = 0x123456789abcdLL;
			eax = i2l(op0 & 0xffffffff);
			edx = i2l(op0 >> 32);
			if(i == 0)
				op1 = 0xfbca765423456LL;
			else
				op1 = op0;
			op2 = 0x6532432432434LL;
			asm("cmpxchg8b %2\n"
			    "pushf\n"
			    "pop %3\n"
			    : "=a" (eax),
			      "=d" (edx),
			      "=m" (op1),
			      "=g" (eflags)
			    : "0" (eax),
			      "1" (edx),
			      "m" (op1),
			      "b" ((int)op2),
			      "c" ((int)(op2 >> 32)));
			printf("cmpxchg8b: eax=" FMTLX " edx=" FMTLX " op1="
					FMT64X " CC=%02lx\n", eax, edx, op1,
					eflags & CC_Z);
		}
	}
#endif
	return 0;
}
