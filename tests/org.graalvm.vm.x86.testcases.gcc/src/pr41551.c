/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
/* { dg-do compile } */
/* { dg-options "-O2" } */

/* Make sure we do not ICE.  */

__extension__ typedef __UINTPTR_TYPE__ uintptr_t;

int main(void)
{
 int var, *p = &var;
 return (double)(uintptr_t)(p);
}
