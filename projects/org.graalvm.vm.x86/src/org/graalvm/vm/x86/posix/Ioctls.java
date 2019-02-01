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
package org.graalvm.vm.x86.posix;

import java.util.HashMap;
import java.util.Map;

public class Ioctls {
    private static final Map<Integer, Integer> ioctls = new HashMap<>();

    public static final int TCGETS = 0x5401;
    public static final int TCSETS = 0x5402;
    public static final int TCSETSW = 0x5403;
    public static final int TCSETSF = 0x5404;
    public static final int TIOCGWINSZ = 0x5413;
    public static final int TIOCOUTQ = 0x5411;
    public static final int TIOCSWINSZ = 0x5414;
    public static final int FIONCLEX = 0x5450;
    public static final int FIOCLEX = 0x5451;

    static {
        ioctls.put(TCGETS, org.graalvm.vm.posix.api.io.Ioctls.TCGETS);
        ioctls.put(TCSETS, org.graalvm.vm.posix.api.io.Ioctls.TCSETS);
        ioctls.put(TCSETSW, org.graalvm.vm.posix.api.io.Ioctls.TCSETSF);
        ioctls.put(TCSETSW, org.graalvm.vm.posix.api.io.Ioctls.TCSETSW);
        ioctls.put(TIOCGWINSZ, org.graalvm.vm.posix.api.io.Ioctls.TIOCGWINSZ);
        ioctls.put(TIOCOUTQ, org.graalvm.vm.posix.api.io.Ioctls.TIOCOUTQ);
        ioctls.put(TIOCSWINSZ, org.graalvm.vm.posix.api.io.Ioctls.TIOCSWINSZ);
        ioctls.put(FIONCLEX, org.graalvm.vm.posix.api.io.Ioctls.FIONCLEX);
        ioctls.put(FIOCLEX, org.graalvm.vm.posix.api.io.Ioctls.FIOCLEX);
    }

    public static int translate(int request) {
        Integer ioctl = ioctls.get(request);
        if (ioctl != null) {
            return ioctl;
        }

        int dir = Ioctl._IOC_DIR(request);
        int type = Ioctl._IOC_TYPE(request);
        int nr = Ioctl._IOC_NR(request);
        int size = Ioctl._IOC_SIZE(request);
        return org.graalvm.vm.posix.api.io.Ioctl._IOC(dir, type, nr, size);
    }
}
