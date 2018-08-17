package org.graalvm.vm.x86;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;

public abstract class AMD64Language extends TruffleLanguage<AMD64Context> {
    public static final String MIME_TYPE = "application/x-executable";

    protected FrameDescriptor fd = new FrameDescriptor();

    @Override
    protected AMD64Context createContext(Env env) {
        return new AMD64Context(this, env, fd);
    }

    @Override
    protected boolean isObjectOfLanguage(Object object) {
        return false;
    }

    public static ContextReference<AMD64Context> getCurrentContextReference() {
        return getCurrentLanguage(AMD64Language.class).getContextReference();
    }

    public static TruffleLanguage<AMD64Context> getCurrentLanguage() {
        return getCurrentLanguage(AMD64Language.class);
    }
}
