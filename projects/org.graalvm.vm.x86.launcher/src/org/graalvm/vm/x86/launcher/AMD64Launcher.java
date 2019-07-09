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
package org.graalvm.vm.x86.launcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.graalvm.launcher.AbstractLanguageLauncher;
import org.graalvm.nativeimage.ImageInfo;
import org.graalvm.nativeimage.RuntimeOptions;
import org.graalvm.options.OptionCategory;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.graalvm.vm.util.log.Trace;

public class AMD64Launcher extends AbstractLanguageLauncher {
    public static void main(String[] args) {
        Trace.setupConsoleApplication(Level.INFO);
        if (ImageInfo.inImageCode()) {
            // set default thresholds
            RuntimeOptions.set("TruffleOSRCompilationThreshold", 10);
            RuntimeOptions.set("TruffleCompilationThreshold", 10);
        }
        new AMD64Launcher().launch(args);
    }

    private String[] programArgs;
    private String file;
    private VersionAction versionAction = VersionAction.None;

    @Override
    protected void launch(Context.Builder contextBuilder) {
        System.exit(execute(contextBuilder));
    }

    @Override
    protected String getLanguageId() {
        return "amd64";
    }

    @Override
    protected List<String> preprocessArguments(List<String> arguments, Map<String, String> polyglotOptions) {
        final List<String> unrecognizedOptions = new ArrayList<>();

        ListIterator<String> iterator = arguments.listIterator();
        while (iterator.hasNext()) {
            String option = iterator.next();
            if (option.length() < 2 || !option.startsWith("-")) {
                iterator.previous();
                break;
            }
            // Ignore fall through
            switch (option) {
                case "--": // --
                    break;
                case "--show-version":
                    versionAction = VersionAction.PrintAndContinue;
                    break;
                case "--version":
                    versionAction = VersionAction.PrintAndExit;
                    break;
                default:
                    // options with argument
                    String argument;
                    int equalsIndex = option.indexOf('=');
                    if (equalsIndex > 0) {
                        argument = option.substring(equalsIndex + 1);
                    } else if (iterator.hasNext()) {
                        argument = iterator.next();
                    } else {
                        argument = null;
                    }
                    // ignore unknown options
                    unrecognizedOptions.add(option);
                    if (equalsIndex < 0 && argument != null) {
                        iterator.previous();
                    }
                    break;
            }
        }

        // collect the file:
        if (file == null && iterator.hasNext()) {
            file = iterator.next();
        }

        // collect the program args:
        if (iterator.previousIndex() < 0) {
            programArgs = new String[0];
        } else {
            List<String> programArgumentsList = arguments.subList(iterator.previousIndex(), arguments.size());
            programArgs = programArgumentsList.toArray(new String[programArgumentsList.size()]);
        }

        return unrecognizedOptions;
    }

    @Override
    protected void validateArguments(Map<String, String> polyglotOptions) {
        if (file == null && versionAction != VersionAction.PrintAndExit) {
            throw abort("No program file provided.", 1);
        }
    }

    @Override
    protected void printHelp(OptionCategory maxCategory) {
        // @formatter:off
        System.out.println("Usage: vmx86 [OPTIONS]... FILE [PROGRAM ARGS]");
        System.out.println("Run x86_64 programs for Linux on the GraalVM's x86_64 interpreter.\n");
        System.out.println("Mandatory arguments to long options are mandatory for short options too.\n");
        System.out.println("Options:");
        printOption("--version",         "print the version and exit");
        printOption("--show-version",    "print the version and continue");
        // @formatter:on
    }

    @Override
    protected void collectArguments(Set<String> args) {
        // @formatter:off
        args.addAll(Arrays.asList(
                        "--version",
                        "--show-version"));
        // @formatter:on
    }

    protected static void printOption(String option, String description) {
        String opt;
        if (option.length() >= 22) {
            System.out.println(String.format("%s%s", "  ", option));
            opt = "";
        } else {
            opt = option;
        }
        System.out.println(String.format("  %-22s%s", opt, description));
    }

    protected int execute(Context.Builder contextBuilder) {
        contextBuilder.arguments(getLanguageId(), programArgs);
        contextBuilder.allowCreateThread(true);
        try (Context context = contextBuilder.build()) {
            runVersionAction(versionAction, context.getEngine());
            Value result = context.eval(Source.newBuilder(getLanguageId(), file, "<path>").build());
            return result.asInt();
        } catch (PolyglotException e) {
            if (e.isExit()) {
                return e.getExitStatus();
            } else if (!e.isInternalError()) {
                printStackTraceSkipTrailingHost(e);
                return -1;
            } else {
                throw e;
            }
        } catch (IOException e) {
            throw abort(e);
        }
    }

    private static void printStackTraceSkipTrailingHost(PolyglotException e) {
        List<PolyglotException.StackFrame> stackTrace = new ArrayList<>();
        for (PolyglotException.StackFrame s : e.getPolyglotStackTrace()) {
            stackTrace.add(s);
        }
        // remove trailing host frames
        for (ListIterator<PolyglotException.StackFrame> iterator = stackTrace.listIterator(stackTrace.size()); iterator.hasPrevious();) {
            PolyglotException.StackFrame s = iterator.previous();
            if (s.isHostFrame()) {
                iterator.remove();
            } else {
                break;
            }
        }
        System.out.println(e.isHostException() ? e.asHostException().toString() : e.getMessage());
        for (PolyglotException.StackFrame s : stackTrace) {
            System.out.println("\tat " + s);
        }
    }
}
