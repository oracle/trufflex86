package org.graalvm.vm.x86.node;

import org.graalvm.vm.x86.AMD64Context;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.nodes.RootNode;

public abstract class AMD64RootNode extends RootNode {
    private final TruffleLanguage<AMD64Context> language;

    protected AMD64RootNode(TruffleLanguage<AMD64Context> language, FrameDescriptor fd) {
        super(language, fd);
        this.language = language;
    }

    @SuppressWarnings("unchecked")
    protected ContextReference<AMD64Context> getContextReference() {
        return getLanguage(language.getClass()).getContextReference();
    }

    public TruffleLanguage<AMD64Context> getAMD64Language() {
        return language;
    }
}
