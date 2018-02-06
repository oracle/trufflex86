package org.graalvm.vm.x86;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.debug.DebuggerTags;
import com.oracle.truffle.api.instrumentation.ProvidedTags;
import com.oracle.truffle.api.instrumentation.StandardTags;

@TruffleLanguage.Registration(id = "amd64", name = "AMD64VM", version = "0.1", mimeType = AMD64Language.MIME_TYPE)
@ProvidedTags({StandardTags.CallTag.class, StandardTags.StatementTag.class, StandardTags.RootTag.class,
                DebuggerTags.AlwaysHalt.class})
public class AMD64Language extends TruffleLanguage<AMD64Context> {
    public static final String MIME_TYPE = "application/x-executable";

    public static final String NAME = "amd64";

    @Override
    protected AMD64Context createContext(Env env) {
        return null;
    }

    @Override
    protected Object getLanguageGlobal(AMD64Context context) {
        return null;
    }

    @Override
    protected boolean isObjectOfLanguage(Object object) {
        return false;
    }
}
