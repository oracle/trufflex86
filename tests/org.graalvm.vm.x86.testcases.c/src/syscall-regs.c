#include <stdio.h>
#include <stdint.h>

#define	asm __asm__

void asm_sc0(int64_t* rip, int64_t* rflags)
{
	long id = 0;
	long a1 = 0;
	long a2 = 0;
	long a3 = 0;
	long result;
	register int64_t rcx asm("rcx");
	register int64_t r11 asm("r11");

	asm volatile("syscall" : "=a"(result)
			       : "a"(id), "D"(a1), "S"(a2), "d"(a3),
				 "r"(rcx), "r"(r11)
			       : "memory");

	*rip = rcx;
	*rflags = r11;
}

int main(void)
{
	int64_t rcx;
	int64_t rfl;

	asm_sc0(&rcx, &rfl);

	printf("rcx = %016lx\nr11 = %016lx\n", rcx, rfl);

	return 0;
}
