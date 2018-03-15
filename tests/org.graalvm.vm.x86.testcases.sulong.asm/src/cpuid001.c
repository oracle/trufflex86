#include <immintrin.h>
#include "cpuid.h"

int main(void)
{
	return has_rdrand() ? 1 : 0;
}
