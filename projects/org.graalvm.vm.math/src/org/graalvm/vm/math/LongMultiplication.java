/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.graalvm.vm.math;

public class LongMultiplication {
    private static final long MASK32 = 0xFFFFFFFFL;
    private static final int SHIFT32 = 32;

    public static long multiplyHigh(long x, long y) {
        if (x < 0 || y < 0) {
            // Use technique from section 8-2 of Henry S. Warren, Jr.,
            // Hacker's Delight (2nd ed.) (Addison Wesley, 2013), 173-174.
            long x1 = x >> SHIFT32;
            long x2 = x & MASK32;
            long y1 = y >> SHIFT32;
            long y2 = y & MASK32;
            long z2 = x2 * y2;
            long t = x1 * y2 + (z2 >>> SHIFT32);
            long z1 = t & MASK32;
            long z0 = t >> SHIFT32;
            z1 += x2 * y1;
            return x1 * y1 + z0 + (z1 >> SHIFT32);
        } else {
            // Use Karatsuba technique with two base 2^32 digits.
            long x1 = x >>> SHIFT32;
            long y1 = y >>> SHIFT32;
            long x2 = x & MASK32;
            long y2 = y & MASK32;
            long a = x1 * y1;
            long b = x2 * y2;
            long c = (x1 + x2) * (y1 + y2);
            long k = c - a - b;
            return (((b >>> SHIFT32) + k) >>> SHIFT32) + a;
        }
    }

    public static long multiplyHighUnsigned(long x, long y) {
        long high = multiplyHigh(x, y);
        return high + (((x < 0) ? y : 0) + ((y < 0) ? x : 0));
    }

    public static final int mulhwu(int a, int b) {
        long prod = Integer.toUnsignedLong(a) * Integer.toUnsignedLong(b);
        return (int) (prod >>> 32);
    }
}
