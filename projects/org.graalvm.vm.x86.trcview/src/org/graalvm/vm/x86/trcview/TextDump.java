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
import org.graalvm.vm.x86.node.debug.trace.Record;
import org.graalvm.vm.x86.node.debug.trace.StepRecord;
import org.graalvm.vm.x86.node.debug.trace.SystemLogRecord;

public class TextDump {
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Usage: trcdump in.trc out.log");
            System.exit(1);
        }
        try (InputStream fin = new BufferedInputStream(new FileInputStream(args[0]));
                        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(args[1])));
                        ExecutionTraceReader in = new ExecutionTraceReader(fin)) {
            Record record;
            while ((record = in.read()) != null) {
                if (record instanceof MemoryEventRecord) {
                    out.println(record.toString());
                } else if (record instanceof SystemLogRecord) {
                    out.println(record.toString());
                } else if (record instanceof CallArgsRecord) {
                    out.println(record.toString());
                } else if (record instanceof StepRecord) {
                    StepRecord step = (StepRecord) record;
                    out.println("----------------");
                    out.println(step.getLocation());
                    out.println();
                    out.println(step.getState());
                }
            }
        }
    }
}
