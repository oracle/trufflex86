package org.graalvm.vm.x86.nfi;

public class InteropFunctionPointers {
    public final long loadLibrary;
    public final long releaseLibrary;
    public final long getSymbol;

    public InteropFunctionPointers(long loadLibrary, long releaseLibrary, long getSymbol) {
        this.loadLibrary = loadLibrary;
        this.releaseLibrary = releaseLibrary;
        this.getSymbol = getSymbol;
    }

    public long getLoadLibrary() {
        return loadLibrary;
    }

    public long getReleaseLibrary() {
        return releaseLibrary;
    }

    public long getSymbol() {
        return getSymbol;
    }
}
