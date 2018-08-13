package org.graalvm.vm.x86.nfi.test;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.ForeignAccess;
import com.oracle.truffle.api.interop.InteropException;
import com.oracle.truffle.api.interop.Message;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.tck.TruffleRunner;
import com.oracle.truffle.tck.TruffleRunner.Inject;

@RunWith(TruffleRunner.class)
public class BasicNFITest extends NFITest {
    public static class TestFourtytwo extends NFITestRootNode {
        private final TruffleObject fourtytwo = lookupAndBind("fourtytwo", "():sint32");
        @Child private Node executeFourtytwo = Message.EXECUTE.createNode();

        @Override
        public Object executeTest(VirtualFrame frame) throws InteropException {
            return ForeignAccess.sendExecute(executeFourtytwo, fourtytwo);
        }
    }

    public static class TestInc extends NFITestRootNode {
        private final TruffleObject inc = lookupAndBind("inc", "(sint32):sint32");
        @Child private Node executeInc = Message.EXECUTE.createNode();

        @Override
        public Object executeTest(VirtualFrame frame) throws InteropException {
            return ForeignAccess.sendExecute(executeInc, inc, frame.getArguments()[0]);
        }
    }

    public static class TestAdd extends NFITestRootNode {
        private final TruffleObject add = lookupAndBind("add", "(sint32,sint32):sint32");
        @Child private Node executeAdd = Message.EXECUTE.createNode();

        @Override
        public Object executeTest(VirtualFrame frame) throws InteropException {
            return ForeignAccess.sendExecute(executeAdd, add, frame.getArguments()[0], frame.getArguments()[1]);
        }
    }

    @Test
    public void testFourtytwo(@Inject(TestFourtytwo.class) CallTarget target) {
        Object ret = target.call();
        Assert.assertEquals(42, ret);
    }

    @Test
    public void testInc(@Inject(TestInc.class) CallTarget target) {
        Object ret = target.call(42);
        Assert.assertEquals(43, ret);
    }

    @Test
    public void testAdd(@Inject(TestAdd.class) CallTarget target) {
        Object ret = target.call(17, 39);
        Assert.assertEquals(56, ret);
    }
}
