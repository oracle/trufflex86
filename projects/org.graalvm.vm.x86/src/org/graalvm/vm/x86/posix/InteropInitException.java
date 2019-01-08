package org.graalvm.vm.x86.posix;

@SuppressWarnings("serial")
public class InteropInitException extends InteropException {
    private final long loadLibrary;
    private final long releaseLibrary;
    private final long getSymbol;
    private final long truffleEnv;

    public InteropInitException(long loadLibrary, long releaseLibrary, long getSymbol, long truffleEnv) {
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
