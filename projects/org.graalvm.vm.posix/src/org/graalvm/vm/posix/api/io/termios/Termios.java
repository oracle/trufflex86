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
package org.graalvm.vm.posix.api.io.termios;

import org.graalvm.vm.posix.api.PosixPointer;
import org.graalvm.vm.posix.api.Struct;

public class Termios implements Struct {
    public static final int NCCS = 19;

    public static final char VINTR = 0;
    public static final char VQUIT = 1;
    public static final char VERASE = 2;
    public static final char VKILL = 3;
    public static final char VEOF = 4;
    public static final char VMIN = 5;
    public static final char VEOL = 6;
    public static final char VTIME = 7;
    public static final char VEOL2 = 8;
    public static final char VSWTC = 9;
    public static final char VWERASE = 10;
    public static final char VREPRINT = 11;
    public static final char VSUSP = 12;
    public static final char VSTART = 13;
    public static final char VSTOP = 14;
    public static final char VLNEXT = 15;
    public static final char VDISCARD = 16;

    /* c_cc characters */
    public static final char _VINTR = 0;
    public static final char _VQUIT = 1;
    public static final char _VERASE = 2;
    public static final char _VKILL = 3;
    public static final char _VEOF = 4;
    public static final char _VMIN = 5;
    public static final char _VEOL = 6;
    public static final char _VTIME = 7;
    public static final char _VEOL2 = 8;
    public static final char _VSWTC = 9;

    // @formatter:off
    /* c_iflag bits */
    public static final int IGNBRK  = 0000001;
    public static final int BRKINT  = 0000002;
    public static final int IGNPAR  = 0000004;
    public static final int PARMRK  = 0000010;
    public static final int INPCK   = 0000020;
    public static final int ISTRIP  = 0000040;
    public static final int INLCR   = 0000100;
    public static final int IGNCR   = 0000200;
    public static final int ICRNL   = 0000400;
    public static final int IXON    = 0001000;
    public static final int IXOFF   = 0002000;
    public static final int IXANY   = 0004000;
    public static final int IUCLC   = 0010000;
    public static final int IMAXBEL = 0020000;
    public static final int IUTF8   = 0040000;

    /* c_oflag bits */
    public static final int OPOST   = 0000001;
    public static final int ONLCR   = 0000002;
    public static final int OLCUC   = 0000004;

    public static final int OCRNL   = 0000010;
    public static final int ONOCR   = 0000020;
    public static final int ONLRET  = 0000040;

    public static final int OFILL   = 00000100;
    public static final int OFDEL   = 00000200;
    public static final int NLDLY   = 00001400;
    public static final int   NL0   = 00000000;
    public static final int   NL1   = 00000400;
    public static final int   NL2   = 00001000;
    public static final int   NL3   = 00001400;
    public static final int TABDLY  = 00006000;
    public static final int   TAB0  = 00000000;
    public static final int   TAB1  = 00002000;
    public static final int   TAB2  = 00004000;
    public static final int   TAB3  = 00006000;
    public static final int CRDLY   = 00030000;
    public static final int   CR0   = 00000000;
    public static final int   CR1   = 00010000;
    public static final int   CR2   = 00020000;
    public static final int   CR3   = 00030000;
    public static final int FFDLY   = 00040000;
    public static final int   FF0   = 00000000;
    public static final int   FF1   = 00040000;
    public static final int BSDLY   = 00100000;
    public static final int   BS0   = 00000000;
    public static final int   BS1   = 00100000;
    public static final int VTDLY   = 00200000;
    public static final int   VT0   = 00000000;
    public static final int   VT1   = 00200000;

    public static final int XTABS   = 00006000;

    /* c_cflag bit meaning */
    public static final int CBAUD   = 0000377;
    public static final int  B0     = 0000000;         /* hang up */
    public static final int  B50    = 0000001;
    public static final int  B75    = 0000002;
    public static final int  B110   = 0000003;
    public static final int  B134   = 0000004;
    public static final int  B150   = 0000005;
    public static final int  B200   = 0000006;
    public static final int  B300   = 0000007;
    public static final int  B600   = 0000010;
    public static final int  B1200  = 0000011;
    public static final int  B1800  = 0000012;
    public static final int  B2400  = 0000013;
    public static final int  B4800  = 0000014;
    public static final int  B9600  = 0000015;
    public static final int  B19200 = 0000016;
    public static final int  B38400 = 0000017;
    public static final int  EXTA   = B19200;
    public static final int  EXTB   = B38400;
    public static final int CBAUDEX = 0000020;
    public static final int  B57600 = 00020;
    public static final int  B115200= 00021;
    public static final int  B230400= 00022;
    public static final int  B460800= 00023;
    public static final int  B500000= 00024;
    public static final int  B576000= 00025;
    public static final int  B921600= 00026;
    public static final int  B1000000=00027;
    public static final int  B1152000=00030;
    public static final int  B1500000=00031;
    public static final int  B2000000=00032;
    public static final int  B2500000=00033;
    public static final int  B3000000=00034;
    public static final int  B3500000=00035;
    public static final int  B4000000=00036;
    public static final int __MAX_BAUD=B4000000;

    public static final int CSIZE    = 00001400;
    public static final int   CS5    = 00000000;
    public static final int   CS6    = 00000400;
    public static final int   CS7    = 00001000;
    public static final int   CS8    = 00001400;

    public static final int CSTOPB   = 00002000;
    public static final int CREAD    = 00004000;
    public static final int PARENB   = 00010000;
    public static final int PARODD   = 00020000;
    public static final int HUPCL    = 00040000;

    public static final int CLOCAL   = 00100000;
    public static final int CMSPAR   = 010000000000;         /* mark or space (stick) parity */
    public static final int CRTSCTS  = 020000000000;         /* flow control */

    /* c_lflag bits */
    public static final int ISIG     = 0x00000080;
    public static final int ICANON   = 0x00000100;
    public static final int XCASE    = 0x00004000;
    public static final int ECHO     = 0x00000008;
    public static final int ECHOE    = 0x00000002;
    public static final int ECHOK    = 0x00000004;
    public static final int ECHONL   = 0x00000010;
    public static final int NOFLSH   = 0x80000000;
    public static final int TOSTOP   = 0x00400000;
    public static final int ECHOCTL  = 0x00000040;
    public static final int ECHOPRT  = 0x00000020;
    public static final int ECHOKE   = 0x00000001;
    public static final int FLUSHO   = 0x00800000;
    public static final int PENDIN   = 0x20000000;
    public static final int IEXTEN   = 0x00000400;
    public static final int EXTPROC  = 0x10000000;

    /* Values for the ACTION argument to `tcflow'.  */
    public static final int TCOOFF        = 0;
    public static final int TCOON         = 1;
    public static final int TCIOFF        = 2;
    public static final int TCION         = 3;

    /* Values for the QUEUE_SELECTOR argument to `tcflush'.  */
    public static final int TCIFLUSH      = 0;
    public static final int TCOFLUSH      = 1;
    public static final int TCIOFLUSH     = 2;

    /* Values for the OPTIONAL_ACTIONS argument to `tcsetattr'.  */
    public static final int TCSANOW       = 0;
    public static final int TCSADRAIN     = 1;
    public static final int TCSAFLUSH     = 2;
    // @formatter:on

    public int c_iflag;
    public int c_oflag;
    public int c_cflag;
    public int c_lflag;
    public final char[] c_cc = new char[NCCS];
    public char c_line;
    public int c_ispeed;
    public int c_ospeed;

    public PosixPointer write(PosixPointer ptr) {
        if (c_cc == null) {
            throw new NullPointerException();
        }
        if (c_cc.length != NCCS) {
            throw new IllegalStateException();
        }
        PosixPointer p = ptr;
        p.setI32(c_iflag);
        p = p.add(4);
        p.setI32(c_oflag);
        p = p.add(4);
        p.setI32(c_cflag);
        p = p.add(4);
        p.setI32(c_lflag);
        p = p.add(4);
        for (char c : c_cc) {
            p.setI8((byte) c);
            p = p.add(1);
        }
        p.setI32(c_ispeed);
        p = p.add(4);
        p.setI32(c_ospeed);
        return p.add(4);
    }

    public static Termios getDefaultTerminal() {
        Termios termios = new Termios();
        termios.c_iflag = 0x00000500;
        termios.c_oflag = 0x00000005;
        termios.c_cflag = 0x000000bf;
        termios.c_lflag = 0x00008a3b;
        termios.c_ispeed = 0x0000000f;
        termios.c_ospeed = 0x0000000f;
        return termios;
    }

    public static Termios getDefaultFile() {
        Termios termios = new Termios();
        termios.c_ospeed = 0xf;
        return termios;
    }
}
