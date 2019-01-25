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

    public static class TestAddF32 extends NFITestRootNode {
        private final TruffleObject add = lookupAndBind("addF32", "(float,float):float");
        @Child private Node executeAdd = Message.EXECUTE.createNode();

        @Override
        public Object executeTest(VirtualFrame frame) throws InteropException {
            return ForeignAccess.sendExecute(executeAdd, add, frame.getArguments()[0], frame.getArguments()[1]);
        }
    }

    public static class TestAddToF32 extends NFITestRootNode {
        private final TruffleObject add = lookupAndBind("addToF32", "(sint32,sint32):float");
        @Child private Node executeAdd = Message.EXECUTE.createNode();

        @Override
        public Object executeTest(VirtualFrame frame) throws InteropException {
            return ForeignAccess.sendExecute(executeAdd, add, frame.getArguments()[0], frame.getArguments()[1]);
        }
    }

    public static class TestAddF64 extends NFITestRootNode {
        private final TruffleObject add = lookupAndBind("addF64", "(double,double):double");
        @Child private Node executeAdd = Message.EXECUTE.createNode();

        @Override
        public Object executeTest(VirtualFrame frame) throws InteropException {
            return ForeignAccess.sendExecute(executeAdd, add, frame.getArguments()[0], frame.getArguments()[1]);
        }
    }

    public static class TestAddToF64 extends NFITestRootNode {
        private final TruffleObject add = lookupAndBind("addToF64", "(sint32,sint32):double");
        @Child private Node executeAdd = Message.EXECUTE.createNode();

        @Override
        public Object executeTest(VirtualFrame frame) throws InteropException {
            return ForeignAccess.sendExecute(executeAdd, add, frame.getArguments()[0], frame.getArguments()[1]);
        }
    }

    public static class TestTenargs extends NFITestRootNode {
        private final TruffleObject tenargs = lookupAndBind("tenargs", "(sint32,sint32,sint32,sint32,sint32,sint32,sint32,sint32,sint32,sint32):sint32");
        @Child private Node executeTenargs = Message.EXECUTE.createNode();

        @Override
        public Object executeTest(VirtualFrame frame) throws InteropException {
            return ForeignAccess.sendExecute(executeTenargs, tenargs, frame.getArguments());
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

    @Test
    public void testAddF32(@Inject(TestAddF32.class) CallTarget target) {
        Object ret = target.call(17.5f, 39.2f);
        Assert.assertEquals(56.7f, ret);
    }

    @Test
    public void testAddToF32(@Inject(TestAddToF32.class) CallTarget target) {
        Object ret = target.call(17, 39);
        Assert.assertEquals(56f, ret);
    }

    @Test
    public void testAddF64(@Inject(TestAddF64.class) CallTarget target) {
        Object ret = target.call(17.5, 39.2);
        Assert.assertEquals(56.7, ret);
    }

    @Test
    public void testAddToF64(@Inject(TestAddToF64.class) CallTarget target) {
        Object ret = target.call(17, 39);
        Assert.assertEquals(56.0, ret);
    }

    @Test
    public void testTenargs(@Inject(TestTenargs.class) CallTarget target) {
        int[] args = {17, 39, 2, 71, 68, 633215858, 1316, 465, 64562, 684233};
        int result = 0;
        for (int i = 0; i < args.length; i++) {
            result <<= 1;
            result += args[i];
        }
        Object[] oargs = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            oargs[i] = args[i];
        }
        Object ret = target.call(oargs);
        Assert.assertEquals(result, ret);
    }
}
