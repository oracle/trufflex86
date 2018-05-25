#include <stdio.h>

int main(void)
{
	int i;
	const double values[] = { 0.0, 1.0, 3.141592653587, 4.2, 8.92, 10.0, 12.24, 25.57, 1997.9771 };
	const int cnt = sizeof(values) / sizeof(*values);
	for(i = 0; i < cnt; i++) {
		printf("values[%d] = %f\n", i, values[i]);
	}
	return 0;
}
