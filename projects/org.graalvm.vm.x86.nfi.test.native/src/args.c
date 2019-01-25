int tenargs(int a0, int a1, int a2, int a3, int a4, int a5, int a6, int a7, int a8, int a9)
{
	int i;
	int result = 0;
	int args[] = { a0, a1, a2, a3, a4, a5, a6, a7, a8, a9 };
	for(i = 0; i < 10; i++) {
		result <<= 1;
		result += args[i];
	}
	return result;
}
