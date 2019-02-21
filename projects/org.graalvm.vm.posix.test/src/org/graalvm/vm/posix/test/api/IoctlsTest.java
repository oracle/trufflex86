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
package org.graalvm.vm.posix.test.api;

import static org.junit.Assert.assertEquals;

import org.graalvm.vm.posix.api.io.Ioctl;
import org.graalvm.vm.posix.api.io.Ioctls;
import org.junit.Test;

public class IoctlsTest {
    public static final int TCGETS = Ioctl._IOR('t', 19, 44);
    public static final int TCSETS = Ioctl._IOW('t', 20, 44);
    public static final int TCSETSW = Ioctl._IOW('t', 21, 44);
    public static final int TCSETSF = Ioctl._IOW('t', 22, 44);

    public static final int TIOCSWINSZ = Ioctl._IOW('t', 103, 8);
    public static final int TIOCGWINSZ = Ioctl._IOR('t', 104, 8);
    public static final int TIOCSTART = Ioctl._IO('t', 110); /* start output, like ^Q */
    public static final int TIOCSTOP = Ioctl._IO('t', 111); /* stop output, like ^S */
    public static final int TIOCOUTQ = Ioctl._IOR('t', 115, 4); /* output queue size */

    @Test
    public void testIoctlValues() {
        assertEquals(TCGETS, Ioctls.TCGETS);
        assertEquals(TCSETS, Ioctls.TCSETS);
        assertEquals(TCSETSW, Ioctls.TCSETSW);
        assertEquals(TCSETSF, Ioctls.TCSETSF);

        assertEquals(TIOCSWINSZ, Ioctls.TIOCSWINSZ);
        assertEquals(TIOCGWINSZ, Ioctls.TIOCGWINSZ);
        assertEquals(TIOCSTART, Ioctls.TIOCSTART);
        assertEquals(TIOCSTOP, Ioctls.TIOCSTOP);
        assertEquals(TIOCOUTQ, Ioctls.TIOCOUTQ);
    }
}
