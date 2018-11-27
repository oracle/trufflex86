package org.graalvm.vm.x86.nfi;

import java.util.List;

import org.graalvm.vm.x86.AMD64Context;
import org.graalvm.vm.x86.node.AMD64RootNode;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

public class AMD64FunctionCallTarget extends AMD64RootNode {
    @Child private AMD64FunctionCallNode target;

    public AMD64FunctionCallTarget(TruffleLanguage<AMD64Context> language, FrameDescriptor fd) {
        super(language, fd);
        target = new AMD64FunctionCallNode(AMD64NFILanguage.getCurrentContextReference().get().getState());
    }

    @Override
    public Object execute(VirtualFrame frame) {
        AMD64Function func = (AMD64Function) frame.getArguments()[0];
        Object[] args = (Object[]) frame.getArguments()[1];
        @SuppressWarnings("unchecked")
        List<Object> objects = (List<Object>) frame.getArguments()[2];
        return target.execute(frame, func, args, objects);
    }
}
