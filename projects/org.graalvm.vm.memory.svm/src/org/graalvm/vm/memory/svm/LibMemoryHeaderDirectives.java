package org.graalvm.vm.memory.svm;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.graalvm.nativeimage.c.CContext.Directives;

import com.oracle.svm.core.c.ProjectHeaderFile;

public class LibMemoryHeaderDirectives implements Directives {
    @Override
    public List<String> getHeaderFiles() {
        return Collections.singletonList("<libmemory.h>");
    }

    @Override
    public List<String> getOptions() {
        String libmemoryHeader = ProjectHeaderFile.resolve("org.graalvm.vm.memory.native", "include/libmemory.h");
        String libmemoryPath = new File(libmemoryHeader.substring(1)).getParent();
        return Collections.singletonList("-I" + libmemoryPath);
    }
}
