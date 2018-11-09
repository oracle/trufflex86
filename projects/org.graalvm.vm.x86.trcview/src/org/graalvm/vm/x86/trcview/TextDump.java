package org.graalvm.vm.x86.trcview;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

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
