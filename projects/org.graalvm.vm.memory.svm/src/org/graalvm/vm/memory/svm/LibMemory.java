package org.graalvm.vm.memory.svm;

import org.graalvm.nativeimage.Platform;
import org.graalvm.nativeimage.Platforms;
import org.graalvm.nativeimage.StackValue;
import org.graalvm.nativeimage.c.CContext;
import org.graalvm.nativeimage.c.function.CFunction;
import org.graalvm.nativeimage.c.function.CLibrary;
import org.graalvm.nativeimage.c.type.CIntPointer;
import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.word.PointerBase;
import org.graalvm.word.WordFactory;

@CLibrary("memory")
@Platforms(Platform.LINUX.class)
@CContext(LibMemoryHeaderDirectives.class)
public class LibMemory {
    @CFunction
    protected static native PointerBase MEM_setup_segv_handler(PointerBase lo, PointerBase hi, CIntPointer err);

    public static long setupSegvHandler(long lo, long hi) throws PosixException {
        CIntPointer err = StackValue.get(CIntPointer.class);
        PointerBase result = MEM_setup_segv_handler(WordFactory.pointer(lo), WordFactory.pointer(hi), err);
        if (result.rawValue() == -1) {
            if (err.read() == 0) {
                return 0;
            } else {
                throw new PosixException(ErrnoTranslator.translate(err.read()));
            }
        }
        return result.rawValue();
    }
}
