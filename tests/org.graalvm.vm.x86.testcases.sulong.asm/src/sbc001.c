#include <stdio.h>
#include "flags.h"

void test_sbb(int a, int b, int cf) {
  char flags = cf ? CC_C : 0;
  char out_flags;
  int out;
  __asm__ volatile("movb %%al, %%ah\n"
		   "sahf\n"
		   "sbbl %[a], %[b]\n"
		   "lahf\n"
		   "movb %%ah, %%al"
		   :     "=a"(out_flags),
		     [b] "=r" (out)
		   : [a] "r"(a),
		         "1"(b),
		         "a"(flags));
  printf("%08x:%08x:%x:%08x:%x\n", a, b, cf, out, (out_flags & CC_C) ? 1 : 0);
}

int main() {
  test_sbb(0x00000000, 0x00000000, 0x0);
  test_sbb(0x00000000, 0x00000000, 0x1);
  test_sbb(0x00000d0c, 0x00000000, 0x1);
  test_sbb(0x00000d0c, 0x00000d0c, 0x1);
  test_sbb(0x00000000, 0x00000d0c, 0x1);
  test_sbb(0x00000d0c, 0x00000000, 0x0);
  test_sbb(0x00000d0c, 0x00000d0c, 0x0);
  test_sbb(0x00000000, 0x00000d0c, 0x0);
  test_sbb(0xffffffff, 0x00000000, 0x0);
  test_sbb(0xffffffff, 0x00000d0c, 0x0);
  test_sbb(0xffffffff, 0x80000000, 0x0);
  test_sbb(0xffffffff, 0xffffffff, 0x0);
  test_sbb(0xffffffff, 0x00000000, 0x1);
  test_sbb(0xffffffff, 0x00000d0c, 0x1);
  test_sbb(0xffffffff, 0x80000000, 0x1);
  test_sbb(0xffffffff, 0xffffffff, 0x1);
  test_sbb(0x80000000, 0x00000000, 0x0);
  test_sbb(0x80000000, 0x00000d0c, 0x0);
  test_sbb(0x80000000, 0x80000000, 0x0);
  test_sbb(0x80000000, 0xffffffff, 0x0);
  test_sbb(0x80000000, 0x00000000, 0x1);
  test_sbb(0x80000000, 0x00000d0c, 0x1);
  test_sbb(0x80000000, 0x80000000, 0x1);
  test_sbb(0x80000000, 0xffffffff, 0x1);
}
