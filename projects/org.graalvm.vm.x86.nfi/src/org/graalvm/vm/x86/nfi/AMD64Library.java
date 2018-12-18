package org.graalvm.vm.x86.nfi;

import org.graalvm.vm.x86.AMD64Context;

import com.oracle.truffle.api.TruffleLanguage.ContextReference;
import com.oracle.truffle.api.interop.ForeignAccess;
import com.oracle.truffle.api.interop.TruffleObject;

public class AMD64Library implements TruffleObject {
    private final long loadLibrary;
    private final long releaseLibrary;
    private final long getSymbol;

    private final long handle;

    private final ContextReference<AMD64Context> ctxref;

    private final String name;

    public AMD64Library(ContextReference<AMD64Context> ctxref, long loadLibrary, long releaseLibrary, long getSymbol, long handle, String name) {
        this.ctxref = ctxref;
        this.loadLibrary = loadLibrary;
        this.releaseLibrary = releaseLibrary;
        this.getSymbol = getSymbol;
        this.handle = handle;
        this.name = name;
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

    public long getHandle() {
        return handle;
    }

    public ContextReference<AMD64Context> getContextReference() {
        return ctxref;
    }

    public String getName() {
        return name;
    }

    public ForeignAccess getForeignAccess() {
        return AMD64LibraryMessageResolutionForeign.ACCESS;
    }

    @Override
    public String toString() {
        return "AMD64Library[" + name + "]";
    }
}
