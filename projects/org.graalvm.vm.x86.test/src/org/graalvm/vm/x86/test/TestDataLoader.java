package org.graalvm.vm.x86.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.everyware.posix.elf.Elf;
import com.everyware.posix.elf.Section;
import com.everyware.util.ResourceLoader;

public class TestDataLoader {
    public static byte[] loadFile(String name) throws IOException {
        TestOptions.init();
        try (InputStream in = ResourceLoader.loadResource(TestDataLoader.class, name);
                        ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buf = new byte[256];
            int n;
            while ((n = in.read(buf)) != -1) {
                out.write(buf, 0, n);
            }
            return out.toByteArray();
        }
    }

    public static Elf load(String name) throws IOException {
        byte[] data = loadFile(name);
        assert data.length > 0;
        Elf elf = new Elf(data);
        assert elf.e_machine == Elf.EM_X86_64;
        assert elf.ei_class == Elf.ELFCLASS64;
        return elf;
    }

    public static byte[] getCode(String name) throws IOException {
        Elf elf = load(name);
        Section section = elf.getSection(".text");
        byte[] data = new byte[(int) section.getSize()];
        section.load(data);
        return data;
    }
}
