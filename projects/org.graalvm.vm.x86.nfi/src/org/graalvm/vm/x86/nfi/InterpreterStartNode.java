package org.graalvm.vm.x86.nfi;

import java.io.File;
import java.io.IOException;

import org.graalvm.vm.x86.AMD64Context;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.InteropFunctionPointers;
import org.graalvm.vm.x86.node.AMD64Node;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.VirtualFrame;

public class InterpreterStartNode extends AMD64Node {
    @Child private InterpreterRootNode interpreter;

    @TruffleBoundary
    private static String getLibnfiPath() {
        String path = System.getProperty("vmx86.libnfi");
        if (path != null) {
            return path;
        } else {
            File f = new File("build/libnfi.so");
            if (f.exists()) {
                try {
                    return f.getCanonicalPath();
                } catch (IOException e) {
                    return null;
                }
            } else {
                f = new File("../../build/libnfi.so");
                try {
                    return f.getCanonicalPath();
                } catch (IOException e) {
                    return null;
                }
            }
        }
    }

    public InteropFunctionPointers execute(VirtualFrame frame) {
        CompilerDirectives.transferToInterpreterAndInvalidate();
        TruffleLanguage<AMD64Context> language = getRootNode().getLanguage(AMD64NFILanguage.class);
        ArchitecturalState state = language.getContextReference().get().getState();
        interpreter = insert(new InterpreterRootNode(state, getLibnfiPath()));
        return interpreter.executeInit(frame);
    }
}
