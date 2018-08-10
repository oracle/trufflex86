package org.graalvm.vm.x86.posix;

@SuppressWarnings("serial")
public class InteropInitException extends InteropException {
    private final long loadLibrary;
    private final long releaseLibrary;
    private final long getSymbol;

    public InteropInitException(long loadLibrary, long releaseLibrary, long getSymbol) {
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
