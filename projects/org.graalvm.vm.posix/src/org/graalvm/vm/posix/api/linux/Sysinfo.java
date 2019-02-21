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
package org.graalvm.vm.posix.api.linux;

import org.graalvm.vm.posix.api.PosixPointer;
import org.graalvm.vm.posix.api.Struct;

// Linux only API
public class Sysinfo implements Struct {
    public long uptime;
    public long[] loads = new long[3];
    public long totalram;
    public long freeram;
    public long sharedram;
    public long bufferram;
    public long totalswap;
    public long freeswap;
    public short procs;
    public long totalhigh;
    public long freehigh;
    public int mem_unit;

    public static final int SI_LOAD_SHIFT = 16;

    public static final int FSHIFT = 11; /* nr of bits of precision */
    public static final int FIXED_1 = (1 << FSHIFT); /* 1.0 as fixed-point */

    public static long LOAD_INT(long x) {
        return x >>> FSHIFT;
    }

    public static long LOAD_FRAC(long x) {
        return LOAD_INT((x & (FIXED_1 - 1)) * 100);
    }

    public static long get_avenrun(long load, long offset, long shift) {
        return (load >>> shift) - offset;
    }

    public static double fp(long load) {
        return load / (double) (1 << SI_LOAD_SHIFT);
    }

    public static long avenrun(long load) {
        long avenrun = Sysinfo.get_avenrun(load, 0, Sysinfo.SI_LOAD_SHIFT - Sysinfo.FSHIFT);
        return avenrun + (Sysinfo.FIXED_1 / 200);
    }

    public static long load(long integer, long fract) {
        long frac = ((fract + 1) << Sysinfo.FSHIFT) / 100;
        long value = (integer << FSHIFT) | frac;

        if (LOAD_FRAC(value) > fract || LOAD_FRAC(value) == 0 && fract > 0) {
            frac = (fract << Sysinfo.FSHIFT) / 100;
            value = (integer << FSHIFT) | frac;
        }

        long avenrun = get_avenrun(value, FIXED_1 / 200, 0);
        return avenrun << (SI_LOAD_SHIFT - FSHIFT);
    }

    public static long load(String load) {
        int dot = load.indexOf('.');
        if (dot == -1) {
            throw new IllegalArgumentException("no dot");
        }
        String integer = load.substring(0, dot);
        String frac = load.substring(dot + 1);
        return load(Integer.parseInt(integer), Integer.parseInt(frac));
    }

    @Override
    public PosixPointer write32(PosixPointer ptr) {
        PosixPointer p = ptr;
        p.setI32((int) uptime);
        p = p.add(4);
        p.setI32((int) loads[0]);
        p = p.add(4);
        p.setI32((int) loads[1]);
        p = p.add(4);
        p.setI32((int) loads[2]);
        p = p.add(4);
        p.setI32((int) totalram);
        p = p.add(4);
        p.setI32((int) freeram);
        p = p.add(4);
        p.setI32((int) sharedram);
        p = p.add(4);
        p.setI32((int) bufferram);
        p = p.add(4);
        p.setI32((int) totalswap);
        p = p.add(4);
        p.setI32((int) freeswap);
        p = p.add(4);
        p.setI16(procs);
        p = p.add(4); // 2 bytes padding
        p.setI32((int) totalhigh);
        p = p.add(4);
        p.setI32((int) freehigh);
        p = p.add(4);
        p.setI32(mem_unit);
        return ptr.add(64);
    }

    @Override
    public PosixPointer write64(PosixPointer ptr) {
        PosixPointer p = ptr;
        p.setI64(uptime);
        p = p.add(8);
        p.setI64(loads[0]);
        p = p.add(8);
        p.setI64(loads[1]);
        p = p.add(8);
        p.setI64(loads[2]);
        p = p.add(8);
        p.setI64(totalram);
        p = p.add(8);
        p.setI64(freeram);
        p = p.add(8);
        p.setI64(sharedram);
        p = p.add(8);
        p.setI64(bufferram);
        p = p.add(8);
        p.setI64(totalswap);
        p = p.add(8);
        p.setI64(freeswap);
        p = p.add(8);
        p.setI16(procs);
        p = p.add(4); // 2 bytes padding
        p = p.add(4); // 4 bytes alignment
        p.setI64(totalhigh);
        p = p.add(8);
        p.setI64(freehigh);
        p = p.add(8);
        p.setI32(mem_unit);
        return ptr.add(112);
    }
}
