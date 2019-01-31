package org.graalvm.vm.x86;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileTypeDetector;

import org.graalvm.vm.posix.elf.Elf;
import org.graalvm.vm.util.io.Endianess;

public final class ElfFileDetector extends FileTypeDetector {
    @Override
    public String probeContentType(Path path) throws IOException {
        try (InputStream in = Files.newInputStream(path)) {
            byte[] header = new byte[4];
            if (in.read(header) != 4) {
                return null;
            }
            if (Endianess.get32bitBE(header) == Elf.MAGIC) {
                return AMD64Language.MIME_TYPE;
            } else {
                return null;
            }
        }
    }
}
