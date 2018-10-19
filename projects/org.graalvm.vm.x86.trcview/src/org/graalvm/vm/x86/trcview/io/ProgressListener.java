package org.graalvm.vm.x86.trcview.io;

@FunctionalInterface
public interface ProgressListener {
    void progressUpdate(long value);
}
