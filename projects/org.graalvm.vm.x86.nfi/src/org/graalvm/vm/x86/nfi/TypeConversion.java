package org.graalvm.vm.x86.nfi;

import org.graalvm.vm.x86.nfi.TypeConversionFactory.AsStringNodeGen;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.ForeignAccess;
import com.oracle.truffle.api.interop.Message;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.nodes.Node;

public class TypeConversion extends Node {
    protected static Node createIsNull() {
        return Message.IS_NULL.createNode();
    }

    protected static boolean checkNull(Node isNull, TruffleObject object) {
        return ForeignAccess.sendIsNull(isNull, object);
    }

    abstract static class AsStringNode extends TypeConversion {

        final boolean acceptAnything;

        AsStringNode(boolean acceptAnything) {
            this.acceptAnything = acceptAnything;
        }

        abstract String execute(Object arg);

        @Specialization
        String stringAsString(String str) {
            return str;
        }

        @Specialization(guards = "checkNull(isNull, arg)")
        @SuppressWarnings("unused")
        String nullAsString(TruffleObject arg,
                        @Cached("createIsNull()") Node isNull) {
            return null;
        }

        @Specialization(guards = "checkIsBoxed(isBoxed, arg)")
        @SuppressWarnings("unused")
        String boxedAsString(TruffleObject arg,
                        @Cached("createIsBoxed()") Node isBoxed,
                        @Cached("createUnbox()") Node unbox,
                        @Cached("createRecursive()") AsStringNode asString) {
            try {
                Object unboxed = ForeignAccess.sendUnbox(unbox, arg);
                return asString.execute(unboxed);
            } catch (UnsupportedMessageException ex) {
                CompilerDirectives.transferToInterpreter();
                throw UnsupportedTypeException.raise(ex, new Object[]{arg});
            }
        }

        @Specialization(guards = {"acceptAnything", "isOther(arg)"})
        @TruffleBoundary
        String otherAsString(Object arg) {
            return arg.toString();
        }

        protected static Node createIsBoxed() {
            return Message.IS_BOXED.createNode();
        }

        protected static Node createUnbox() {
            return Message.UNBOX.createNode();
        }

        protected static boolean checkIsBoxed(Node isBoxed, TruffleObject arg) {
            return ForeignAccess.sendIsBoxed(isBoxed, arg);
        }

        protected static boolean isOther(Object obj) {
            return !(obj instanceof String) && !(obj instanceof TruffleObject);
        }

        protected AsStringNode createRecursive() {
            return AsStringNodeGen.create(acceptAnything);
        }
    }
}
