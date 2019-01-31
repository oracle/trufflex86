package org.graalvm.vm.x86;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.graalvm.vm.posix.elf.Elf;
import org.graalvm.vm.x86.node.InterpreterStartNode;
import org.graalvm.vm.x86.node.init.InitializerNode;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.debug.DebuggerTags;
import com.oracle.truffle.api.instrumentation.ProvidedTags;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.source.Source;

@TruffleLanguage.Registration(id = "amd64", name = "AMD64VM", version = "0.1", mimeType = Vmx86.MIME_TYPE)
@ProvidedTags({StandardTags.CallTag.class, StandardTags.StatementTag.class, StandardTags.RootTag.class, DebuggerTags.AlwaysHalt.class})
public class Vmx86 extends AMD64Language {
    public static final String NAME = "amd64";

    @Override
    protected CallTarget parse(ParsingRequest request) throws Exception {
        String path = request.getSource().getPath();
        if (path != null) {
            byte[] data = Files.readAllBytes(Paths.get(path));
            Elf elf = new Elf(data);
            if (elf.ei_class != Elf.ELFCLASS64) {
                throw new IllegalArgumentException("32bit binary not supported");
            }
            if (elf.e_machine != Elf.EM_X86_64) {
                throw new IllegalArgumentException("only x86_64 is supported");
            }
            InterpreterStartNode interpreter = new InterpreterStartNode(this, fd, path);
            return Truffle.getRuntime().createCallTarget(interpreter);
        } else if (InitializerNode.BINARY != null) {
            Source src = request.getSource();
            InterpreterStartNode interpreter = new InterpreterStartNode(this, fd, src.getCharacters().toString());
            return Truffle.getRuntime().createCallTarget(interpreter);
        } else {
            throw new IllegalArgumentException("Source type is not supported");
        }
    }

}
