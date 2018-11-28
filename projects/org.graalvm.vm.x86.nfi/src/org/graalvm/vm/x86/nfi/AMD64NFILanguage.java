package org.graalvm.vm.x86.nfi;

import org.graalvm.vm.x86.AMD64Language;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.nfi.types.NativeLibraryDescriptor;
import com.oracle.truffle.nfi.types.Parser;

@TruffleLanguage.Registration(id = "amd64nfi", name = "nfi-amd64", version = "0.1", mimeType = AMD64NFILanguage.MIME_TYPE, internal = true)
public class AMD64NFILanguage extends AMD64Language {
    public static final String MIME_TYPE = "trufflenfi/amd64nfi";

    @Override
    protected CallTarget parse(ParsingRequest request) throws Exception {
        CharSequence library = request.getSource().getCharacters();
        NativeLibraryDescriptor descriptor = Parser.parseLibraryDescriptor(library);
        return Truffle.getRuntime().createCallTarget(new AMD64LibraryNode(this, fd, descriptor));
    }
}
