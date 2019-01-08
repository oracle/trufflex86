package org.graalvm.vm.x86;

public class InteropFunctionPointers {
    public final long loadLibrary;
    public final long releaseLibrary;
    public final long getSymbol;
    public final long truffleEnv;

    public InteropFunctionPointers(long loadLibrary, long releaseLibrary, long getSymbol, long truffleEnv) {
        this.loadLibrary = loadLibrary;
        this.releaseLibrary = releaseLibrary;
        this.getSymbol = getSymbol;
        this.truffleEnv = truffleEnv;
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

    public long getTruffleEnv() {
        return truffleEnv;
    }
}
