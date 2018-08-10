package org.graalvm.vm.x86.node;

import org.graalvm.vm.x86.AMD64Context;
import org.graalvm.vm.x86.AMD64Language;
import org.graalvm.vm.x86.ArchitecturalState;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

public class InterpreterStartNode extends AMD64RootNode {
    private final String programName;

    @Child private InterpreterRootNode interpreter;

    public InterpreterStartNode(TruffleLanguage<AMD64Context> language, FrameDescriptor fd, String programName) {
        super(language, fd);
        this.programName = programName;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        CompilerDirectives.transferToInterpreterAndInvalidate();
        TruffleLanguage<AMD64Context> language = getLanguage(AMD64Language.class);
        ArchitecturalState state = language.getContextReference().get().getState();
        interpreter = insert(new InterpreterRootNode(state, programName));
        return interpreter.execute(frame);
    }

    @Override
    public String getName() {
        return "[InterpreterStart]";
    }
}
