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
package org.graalvm.vm.util;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import org.graalvm.vm.util.exception.Messages;
import org.graalvm.vm.util.log.Levels;
import org.graalvm.vm.util.log.Trace;

public class ResourceLoader {
    private static final Logger log = Trace.create(ResourceLoader.class);

    public static InputStream loadResource(Class<?> caller, String resourceName) {
        if (caller != null) {
            InputStream in = null;
            if (caller.getClassLoader() != null) {
                in = caller.getClassLoader().getResourceAsStream(resourceName);
                if (in != null) {
                    return in;
                }
            }
            in = caller.getResourceAsStream(resourceName);
            if (in != null) {
                return in;
            }
            if (caller.getSuperclass() != null) {
                return loadResource(caller.getSuperclass(), resourceName);
            }
            log.log(Levels.WARNING, Messages.NO_RESOURCE.format(caller.getCanonicalName(), resourceName));
        } else {
            log.log(Levels.WARNING, Messages.NO_RESOURCE.format("<unknown>", resourceName));
        }
        return null;
    }

    public static byte[] load(Class<?> clazz, String name) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        try (InputStream in = clazz.getResourceAsStream(name)) {
            if (in == null) {
                throw new FileNotFoundException("Resource \"" + name + "\" not found");
            }
            byte[] b = new byte[512];
            int n;
            while ((n = in.read(b)) != -1) {
                buf.write(b, 0, n);
            }
            return buf.toByteArray();
        }
    }

    public static String getClassBasePath(Class<?> javaClass) {
        return javaClass.getPackage().getName().replace(".", System.getProperty("file.separator"));
    }

    public static String getResourceBasePath(String resourceName) {
        String fileSeparator = System.getProperty("file.separator");
        String result = null;
        if (resourceName != null) {
            if (resourceName.indexOf(fileSeparator) != -1) {
                result = resourceName.substring(0, resourceName.lastIndexOf(fileSeparator));
            } else {
                result = "." + fileSeparator;
            }
        }
        return result;
    }

    public static String getClassName(Class<?> javaClass) {
        return javaClass.getCanonicalName();
    }
}
