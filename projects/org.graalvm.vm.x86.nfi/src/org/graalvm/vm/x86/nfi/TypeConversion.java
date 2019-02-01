/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.graalvm.vm.x86.nfi;

import org.graalvm.vm.memory.MemoryOptions;
import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.x86.AMD64Context;
import org.graalvm.vm.x86.AMD64Language;
import org.graalvm.vm.x86.nfi.TypeConversionFactory.AsF32NodeGen;
import org.graalvm.vm.x86.nfi.TypeConversionFactory.AsF64NodeGen;
import org.graalvm.vm.x86.nfi.TypeConversionFactory.AsI64NodeGen;
import org.graalvm.vm.x86.nfi.TypeConversionFactory.AsPointerNodeGen;
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

    abstract static class IsNullNode extends TypeConversion {
        abstract boolean execute(Object arg);

        @Specialization
        protected boolean executeTruffleObject(TruffleObject obj, @Cached("createIsNull()") Node isNull) {
            return checkNull(isNull, obj);
        }
    }

    abstract static class AsI64Node extends TypeConversion {
        abstract long execute(Object arg);

        @Specialization
        protected long executeI8(byte arg) {
            return arg;
        }

        @Specialization
        protected long executeI16(short arg) {
            return arg;
        }

        @Specialization
        protected long executeI32(int arg) {
            return arg;
        }

        @Specialization
        protected long executeI64(long arg) {
            return arg;
        }

        @Specialization
        protected long executeI1(boolean arg) {
            return arg ? 1 : 0;
        }

        @Specialization
        protected long executeF32(float arg) {
            return (long) arg;
        }

        @Specialization
        protected long executeF64(double arg) {
            return (long) arg;
        }

        @Specialization
        protected long executeChar(char arg) {
            return arg;
        }

        @Specialization(guards = "checkIsPointer(isPointer, arg)")
        @SuppressWarnings("unused")
        protected long serializePointer(TruffleObject arg,
                        @Cached("createIsPointer()") Node isPointer,
                        @Cached("createAsPointer()") Node asPointer) {
            try {
                long pointer = ForeignAccess.sendAsPointer(asPointer, arg);
                return pointer;
            } catch (UnsupportedMessageException ex) {
                CompilerDirectives.transferToInterpreter();
                throw ex.raise();
            }
        }

        @Specialization(guards = "checkNull(isNull, arg)")
        @SuppressWarnings("unused")
        long nullAsPointer(TruffleObject arg, @Cached("createIsNull()") Node isNull) {
            return 0;
        }

        protected static boolean checkIsPointer(Node isPointer, TruffleObject object) {
            return ForeignAccess.sendIsPointer(isPointer, object);
        }

        static Node createIsPointer() {
            return Message.IS_POINTER.createNode();
        }

        static Node createAsPointer() {
            return Message.AS_POINTER.createNode();
        }

        static Node createToNative() {
            return Message.TO_NATIVE.createNode();
        }

        static AsI64Node createRecursive() {
            return AsI64NodeGen.create();
        }

        @SuppressWarnings("unused")
        @Specialization(guards = "checkIsBoxed(isBoxed, arg)")
        protected long unbox(TruffleObject arg,
                        @Cached("createIsBoxed()") Node isBoxed,
                        @Cached("createUnbox()") Node unbox,
                        @Cached("createRecursive()") AsI64Node recursive) {
            try {
                Object value = ForeignAccess.sendUnbox(unbox, arg);
                return recursive.execute(value);
            } catch (UnsupportedMessageException ex) {
                CompilerDirectives.transferToInterpreter();
                throw ex.raise();
            }
        }

        protected static boolean checkIsBoxed(Node isBoxed, TruffleObject object) {
            return ForeignAccess.sendIsBoxed(isBoxed, object);
        }

        static Node createIsBoxed() {
            return Message.IS_BOXED.createNode();
        }

        static Node createUnbox() {
            return Message.UNBOX.createNode();
        }
    }

    abstract static class AsF32Node extends TypeConversion {
        abstract float execute(Object arg);

        @Specialization
        protected float executeI1(boolean arg) {
            return arg ? 1 : 0;
        }

        @Specialization
        protected float executeI8(byte arg) {
            return arg;
        }

        @Specialization
        protected float executeI16(char arg) {
            return arg;
        }

        @Specialization
        protected float executeI16(short arg) {
            return arg;
        }

        @Specialization
        protected float executeI32(int arg) {
            return arg;
        }

        @Specialization
        protected float executeI64(long arg) {
            return arg;
        }

        @Specialization
        protected float executeF32(float arg) {
            return arg;
        }

        @Specialization
        protected float executeF64(double arg) {
            return (float) arg;
        }

        static AsF32Node createRecursive() {
            return AsF32NodeGen.create();
        }

        @SuppressWarnings("unused")
        @Specialization(guards = "checkIsBoxed(isBoxed, arg)")
        protected float unbox(TruffleObject arg,
                        @Cached("createIsBoxed()") Node isBoxed,
                        @Cached("createUnbox()") Node unbox,
                        @Cached("createRecursive()") AsF32Node recursive) {
            try {
                Object value = ForeignAccess.sendUnbox(unbox, arg);
                return recursive.execute(value);
            } catch (UnsupportedMessageException ex) {
                CompilerDirectives.transferToInterpreter();
                throw ex.raise();
            }
        }

        protected static boolean checkIsBoxed(Node isBoxed, TruffleObject object) {
            return ForeignAccess.sendIsBoxed(isBoxed, object);
        }

        static Node createIsBoxed() {
            return Message.IS_BOXED.createNode();
        }

        static Node createUnbox() {
            return Message.UNBOX.createNode();
        }
    }

    abstract static class AsF64Node extends TypeConversion {
        abstract double execute(Object arg);

        @Specialization
        protected double executeI1(boolean arg) {
            return arg ? 1 : 0;
        }

        @Specialization
        protected double executeI8(byte arg) {
            return arg;
        }

        @Specialization
        protected double executeI16(char arg) {
            return arg;
        }

        @Specialization
        protected double executeI16(short arg) {
            return arg;
        }

        @Specialization
        protected double executeI32(int arg) {
            return arg;
        }

        @Specialization
        protected double executeI64(long arg) {
            return arg;
        }

        @Specialization
        protected double executeF32(float arg) {
            return arg;
        }

        @Specialization
        protected double executeF64(double arg) {
            return arg;
        }

        static AsF64Node createRecursive() {
            return AsF64NodeGen.create();
        }

        @SuppressWarnings("unused")
        @Specialization(guards = "checkIsBoxed(isBoxed, arg)")
        protected double unbox(TruffleObject arg,
                        @Cached("createIsBoxed()") Node isBoxed,
                        @Cached("createUnbox()") Node unbox,
                        @Cached("createRecursive()") AsF64Node recursive) {
            try {
                Object value = ForeignAccess.sendUnbox(unbox, arg);
                return recursive.execute(value);
            } catch (UnsupportedMessageException ex) {
                CompilerDirectives.transferToInterpreter();
                throw ex.raise();
            }
        }

        protected static boolean checkIsBoxed(Node isBoxed, TruffleObject object) {
            return ForeignAccess.sendIsBoxed(isBoxed, object);
        }

        static Node createIsBoxed() {
            return Message.IS_BOXED.createNode();
        }

        static Node createUnbox() {
            return Message.UNBOX.createNode();
        }
    }

    abstract static class AsPointerNode extends TypeConversion {
        protected static final boolean MEM_MAP_NATIVE = MemoryOptions.MEM_MAP_NATIVE.get();

        abstract NativePointer execute(Object arg);

        @Specialization
        protected NativePointer fromNativePointer(NativePointer arg) {
            return arg;
        }

        @Specialization
        protected NativePointer fromString(AMD64String arg) {
            return new NativePointer(arg.ptr);
        }

        @Specialization
        protected NativePointer fromLong(long arg, @Cached("getVirtualMemory()") VirtualMemory mem) {
            if (MEM_MAP_NATIVE && arg > 0) {
                return new NativePointer(mem.toMappedNative(arg));
            }
            return new NativePointer(arg);
        }

        @Specialization(guards = "checkIsPointer(isPointer, arg)")
        @SuppressWarnings("unused")
        protected NativePointer serializePointer(TruffleObject arg,
                        @Cached("createIsPointer()") Node isPointer,
                        @Cached("createAsPointer()") Node asPointer,
                        @Cached("getVirtualMemory()") VirtualMemory mem) {
            try {
                long pointer = ForeignAccess.sendAsPointer(asPointer, arg);
                if (MEM_MAP_NATIVE && pointer > 0) {
                    return new NativePointer(mem.toMappedNative(pointer));
                }
                return new NativePointer(pointer);
            } catch (UnsupportedMessageException ex) {
                CompilerDirectives.transferToInterpreter();
                throw ex.raise();
            }
        }

        @Specialization(guards = "checkNull(isNull, arg)")
        @SuppressWarnings("unused")
        NativePointer nullAsPointer(TruffleObject arg, @Cached("createIsNull()") Node isNull) {
            return new NativePointer(0);
        }

        @Specialization(guards = "!checkNull(isNull, arg)", replaces = "serializePointer")
        @SuppressWarnings("unused")
        protected NativePointer transitionToNative(TruffleObject arg,
                        @Cached("createIsNull()") Node isNull,
                        @Cached("createToNative()") Node toNative,
                        @Cached("createRecursive()") AsPointerNode recursive) {
            try {
                Object nativeObj = ForeignAccess.sendToNative(toNative, arg);
                return recursive.execute(nativeObj);
            } catch (UnsupportedMessageException ex) {
                CompilerDirectives.transferToInterpreter();
                throw UnsupportedTypeException.raise(ex, new Object[]{arg});
            }
        }

        protected static boolean checkIsPointer(Node isPointer, TruffleObject object) {
            return ForeignAccess.sendIsPointer(isPointer, object);
        }

        static Node createIsPointer() {
            return Message.IS_POINTER.createNode();
        }

        static Node createAsPointer() {
            return Message.AS_POINTER.createNode();
        }

        static Node createToNative() {
            return Message.TO_NATIVE.createNode();
        }

        static AsPointerNode createRecursive() {
            return AsPointerNodeGen.create();
        }

        static VirtualMemory getVirtualMemory() {
            AMD64Context ctx = AMD64Language.getCurrentContextReference().get();
            return ctx.getMemory();
        }
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
