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
package org.graalvm.vm.x86.trcview;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import org.graalvm.vm.x86.node.debug.trace.BrkRecord;
import org.graalvm.vm.x86.node.debug.trace.CallArgsRecord;
import org.graalvm.vm.x86.node.debug.trace.ExecutionTraceReader;
import org.graalvm.vm.x86.node.debug.trace.MemoryEventRecord;
import org.graalvm.vm.x86.node.debug.trace.MmapRecord;
import org.graalvm.vm.x86.node.debug.trace.MprotectRecord;
import org.graalvm.vm.x86.node.debug.trace.MunmapRecord;
import org.graalvm.vm.x86.node.debug.trace.Record;
import org.graalvm.vm.x86.node.debug.trace.StepRecord;
import org.graalvm.vm.x86.node.debug.trace.SystemLogRecord;

public class TextDump {
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage: trcdump in.trc out.log [record types]");
            System.exit(1);
        }
        boolean dumpMemory = true;
        boolean dumpLog = true;
        boolean dumpCalls = true;
        boolean dumpPC = true;
        boolean dumpState = true;
        boolean dumpMman = true;
        if (args.length > 2) {
            dumpMemory = false;
            dumpLog = false;
            dumpCalls = false;
            dumpPC = false;
            dumpState = false;
            dumpMman = false;
            for (int i = 2; i < args.length; i++) {
                switch (args[i]) {
                    case "+mem":
                        dumpMemory = true;
                        break;
                    case "-mem":
                        dumpMemory = false;
                        break;
                    case "+mman":
                        dumpMman = true;
                        break;
                    case "-mman":
                        dumpMman = false;
                        break;
                    case "+log":
                        dumpLog = true;
                        break;
                    case "-log":
                        dumpLog = false;
                        break;
                    case "+calls":
                        dumpCalls = true;
                        break;
                    case "-calls":
                        dumpCalls = false;
                        break;
                    case "+pc":
                        dumpPC = true;
                        break;
                    case "-pc":
                        dumpPC = false;
                        break;
                    case "+state":
                        dumpState = true;
                        break;
                    case "-state":
                        dumpState = false;
                        break;
                    case "+step":
                        dumpPC = true;
                        dumpState = true;
                        break;
                    case "-step":
                        dumpPC = false;
                        dumpState = false;
                        break;
                    default:
                        System.err.println("Unknown option: " + args[i]);
                        System.exit(1);
                }
            }
        }
        try (InputStream fin = new BufferedInputStream(new FileInputStream(args[0]));
                        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(args[1])));
                        ExecutionTraceReader in = new ExecutionTraceReader(fin)) {
            Record record;
            while ((record = in.read()) != null) {
                if (record instanceof MemoryEventRecord) {
                    if (dumpMemory) {
                        out.println(record.toString());
                    }
                } else if (record instanceof SystemLogRecord) {
                    if (dumpLog) {
                        out.println(record.toString());
                    }
                } else if (record instanceof CallArgsRecord) {
                    if (dumpCalls) {
                        out.println(record.toString());
                    }
                } else if (record instanceof MmapRecord) {
                    if (dumpMman) {
                        out.println(record.toString());
                    }
                } else if (record instanceof MunmapRecord) {
                    if (dumpMman) {
                        out.println(record.toString());
                    }
                } else if (record instanceof MprotectRecord) {
                    if (dumpMman) {
                        out.println(record.toString());
                    }
                } else if (record instanceof BrkRecord) {
                    if (dumpMman) {
                        out.println(record.toString());
                    }
                } else if (record instanceof StepRecord) {
                    StepRecord step = (StepRecord) record;
                    if (dumpState) {
                        out.println("----------------");
                    }
                    if (dumpPC) {
                        out.println(step.getLocation());
                    }
                    if (dumpState) {
                        out.println();
                        out.println(step.getState());
                    }
                }
            }
        }
    }
}
