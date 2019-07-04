/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
#include <immintrin.h>
#include "cpuid.h"

int main(void)
{
	return has_rdrand() ? 1 : 0;
}
