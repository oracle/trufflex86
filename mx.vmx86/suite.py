suite = {
  "mxversion" : "5.136.5",
  "name" : "core",
  "versionConflictResolution" : "latest",

  "imports" : {
    "suites" : [
      {
        "name" : "truffle",
        "subdir" : True,
        "version" : "56eba6bd09599d29d40b100177b419cde6668475",
        "urls" : [
          {"url" : "https://github.com/oracle/graal", "kind" : "git"},
          {"url" : "https://curio.ssw.jku.at/nexus/content/repositories/snapshots", "kind" : "binary"},
        ]
      }
    ]
  },

  "javac.lint.overrides" : "none",

  "licenses" : {
    "GPLv3" : {
      "name" : "GNU General Public License, version 3",
      "url" : "https://opensource.org/licenses/GPL-3.0",
    },
    "MPL-1.1" : {
      "name" : "Mozilla Public License 1.1",
      "url" : "https://opensource.org/licenses/MPL-1.1"
    },
    "MPL-2.0" : {
      "name" : "Mozilla Public License 2.0",
      "url" : "https://opensource.org/licenses/MPL-2.0"
    }
  },

  "libraries" : {
    "CORE" : {
      "path" : "lib/core.jar",
      "urls" : [
        "https://orakel.has.enough.coffee/repo/everyware/core.jar"
      ],
      "sha1" : "a01bac8dfe4a20f172e833c8e0b2a451dd0df99a",
    },

    "POSIX" : {
      "path" : "lib/posix.jar",
      "urls" : [
        "https://orakel.has.enough.coffee/repo/everyware/posix.jar"
      ],
      "sha1" : "349ee266279b64512aaa8933c11c854596f98b23",
    },

    "XED" : {
      "path" : "lib/xed.tar.gz",
      "urls" : [
        "https://orakel.has.enough.coffee/repo/xed-install-base-2018-06-29-lin-x86-64.tar.gz"
      ],
      "sha1" : "ec6ada3f0f3c8c71b57724b3d841d21d6c57fa67",
    },
  },

  "defaultLicense" : "GPLv3",

  "projects" : {
    "org.graalvm.vm.memory" : {
      "subDir" : "projects",
      "sourceDirs" : ["src"],
      "jniHeaders" : True,
      "dependencies" : [
        "CORE",
        "POSIX",
        "truffle:TRUFFLE_API",
        "truffle:TRUFFLE_NFI"
      ],
      "javaCompliance" : "1.8",
      "annotationProcessors" : ["truffle:TRUFFLE_DSL_PROCESSOR"],
      "workingSets" : "vmx86",
      "license" : "GPLv3",
    },

    "org.graalvm.vm.memory.native" : {
      "subDir" : "projects",
      "native" : True,
      "vpath" : True,
      "results" : [
        "bin/<lib:memory>",
      ],
      "buildDependencies" : [
        "org.graalvm.vm.memory",
        "XED"
      ],
      "buildEnv" : {
        "CFLAGS" : "-I<jnigen:org.graalvm.vm.memory>",
        "LIBMEMORY" : "<lib:memory>",
        "XED" : "<path:XED>",
        "XED_VERSION" : "xed-install-base-2018-06-29-lin-x86-64",
        "OS" : "<os>",
      },
      "license" : "GPLv3",
    },

    "org.graalvm.vm.x86" : {
      "subDir" : "projects",
      "sourceDirs" : ["src"],
      "dependencies" : [
        "org.graalvm.vm.memory",
        "CORE",
        "POSIX",
        "truffle:TRUFFLE_API",
        "truffle:TRUFFLE_NFI"
      ],
      "javaCompliance" : "1.8",
      "annotationProcessors" : ["truffle:TRUFFLE_DSL_PROCESSOR"],
      "workingSets" : "vmx86",
      "license" : "GPLv3",
    },

    "org.graalvm.vm.x86.testcases.asm" : {
      "subDir" : "tests",
      "class" : "VMX86TestSuite",
      "license" : "GPLv3"
    },

    "org.graalvm.vm.x86.testcases.c" : {
      "subDir" : "tests",
      "class" : "VMX86TestSuite",
      "license" : "GPLv3"
    },

    "org.graalvm.vm.x86.testcases.sulong.asm" : {
      "subDir" : "tests",
      "class" : "VMX86TestSuite",
      "license" : "GPLv3"
    },

    "benchmarksgame" : {
      "subDir" : "tests",
      "native" : True,
      "vpath" : True,
      "results" : [
        "bin/binarytrees.gcc",
        "bin/binarytrees.gcc-2",
        "bin/fasta.cint",
        "bin/fasta.gcc",
        "bin/fasta.gcc-4",
        "bin/fastaredux.gcc-2",
        "bin/fastaredux.gcc-3",
        "bin/fastaredux.gcc-5",
        "bin/fastaredux.gpp",
        "bin/fastaredux.gpp-2",
        "bin/fastaredux.gpp-5",
        "bin/fannkuchredux.cint",
        "bin/fannkuchredux.gcc",
        "bin/fannkuchredux.gcc-3",
        "bin/mandelbrot.cint",
        "bin/mandelbrot.gcc-2",
        "bin/mandelbrot.gcc-4",
        "bin/mandelbrot.gcc-8",
        "bin/mandelbrot.gcc-9",
        "bin/mandelbrot.gpp",
        "bin/mandelbrot.gpp-2",
        "bin/mandelbrot.gpp-3",
        "bin/mandelbrot.gpp-5",
        "bin/mandelbrot.gpp-6",
        "bin/mandelbrot.gpp-7",
        "bin/mandelbrot.gpp-8",
        "bin/mandelbrot.gpp-9",
        "bin/spectralnorm.cint",
        "bin/spectralnorm.gcc",
        "bin/spectralnorm.gcc-2",
        "bin/spectralnorm.gcc-3",
        "bin/spectralnorm.gcc-5",
        "bin/pidigits.cint-4",
        "bin/pidigits.gcc",
        "bin/pidigits.gcc-4",
        "bin/nbody.cint",
        "bin/nbody.gcc",
        "bin/nbody.gcc-2",
        "bin/nbody.gcc-3",
        "bin/nbody.gcc-4",
        "bin/nbody.gcc-6",
        "bin/nbody.gpp",
        "bin/nbody.gpp-2",
        "bin/nbody.gpp-3",
        "bin/nbody.gpp-5",
        "bin/nbody.gpp-6",
        "bin/nbody.gpp-7",
        "bin/nbody.gpp-8",
        "bin/meteor.cint",
        "bin/meteor.gcc",
        "bin/meteor.gpp",
        "bin/meteor.gpp-2",
        "bin/meteor.gpp-3",
        "bin/meteor.gpp-4",
        "bin/meteor.gpp-5",
        "bin/meteor.gpp-6",
        "bin/knucleotide.cint",
        "bin/knucleotide.gcc-8"
      ],
      "license" : "GPLv3"
    },

    "org.graalvm.vm.x86.test" : {
      "subDir" : "projects",
      "sourceDirs" : ["src"],
      "dependencies" : [
        "org.graalvm.vm.x86",
        "org.graalvm.vm.x86.testcases.asm",
        "org.graalvm.vm.x86.testcases.c",
        "org.graalvm.vm.x86.testcases.sulong.asm",
        "benchmarksgame",
        "mx:JUNIT",
      ],
      "javaCompliance" : "1.8",
      "workingSets" : "vmx86",
      "license" : "GPLv3",
    },

    "org.graalvm.vm.memory.test" : {
      "subDir" : "projects",
      "sourceDirs" : ["src"],
      "dependencies" : [
        "org.graalvm.vm.memory",
        "mx:JUNIT",
      ],
      "javaCompliance" : "1.8",
      "workingSets" : "vmx86",
      "license" : "GPLv3",
    },

    "org.graalvm.vm.x86.emu" : {
      "subDir" : "projects",
      "sourceDirs" : ["src"],
      "jniHeaders" : True,
      "dependencies" : [
        "org.graalvm.vm.memory",
        "org.graalvm.vm.x86",
        "CORE",
        "POSIX"
      ],
      "javaCompliance" : "1.8",
      "workingSets" : "vmx86",
      "license" : "GPLv3",
      "testProject" : True,
    },

    "org.graalvm.vm.x86.emu.native" : {
      "subDir" : "projects",
      "native" : True,
      "vpath" : True,
      "results" : [
        "bin/<lib:emu86>",
      ],
      "buildDependencies" : [
        "org.graalvm.vm.x86.emu",
      ],
      "buildEnv" : {
        "CFLAGS" : "-I<jnigen:org.graalvm.vm.x86.emu>",
        "LIBEMU86" : "<lib:emu86>",
        "OS" : "<os>",
      },
      "license" : "GPLv3",
      "testProject" : True,
    },
  },

  "distributions" : {
    "VM" : {
      "path" : "build/vmx86.jar",
      "subDir" : "vmx86",
      "sourcesPath" : "build/vmx86.src.zip",
      "dependencies" : [
        "org.graalvm.vm.x86",
      ],
      "distDependencies" : [
        "CORE",
        "POSIX",
        "truffle:TRUFFLE_API",
        "truffle:TRUFFLE_NFI"
      ]
    },

    "VM_MEMORY_NATIVE" : {
      "native" : True,
      "platformDependent" : True,
      "output" : "build",
      "dependencies" : [
        "org.graalvm.vm.memory.native"
      ],
    },

    "VM_TESTCASES" : {
      "native" : True,
      "platformDependent" : True,
      "output" : "mxbuild/testcases",
      "dependencies" : [
        "org.graalvm.vm.x86.testcases.asm",
        "org.graalvm.vm.x86.testcases.c",
        "org.graalvm.vm.x86.testcases.sulong.asm",
        "benchmarksgame",
      ],
      "javaProperties" : {
        "library.path" : "<path:VM_MEMORY_NATIVE>/<lib:memory>"
      },
    },

    "VM_TEST" : {
      "path" : "build/vmx86_test.jar",
      "subDir" : "vmx86",
      "sourcesPath" : "build/vmx86_test.src.zip",
      "dependencies" : [
        "org.graalvm.vm.x86.test",
        "org.graalvm.vm.memory.test"
      ],
      "distDependencies" : [
        "CORE",
        "POSIX",
        "VM",
        "VM_TESTCASES",
        "truffle:TRUFFLE_API",
        "truffle:TRUFFLE_NFI"
      ],
      "exclude" : [
        "mx:JUNIT"
      ],
      "javaProperties" : {
        "vmx86test.testSuitePath" : "<path:VM_TESTCASES>"
      },
    },

    "VM_EMU86_NATIVE" : {
      "native" : True,
      "platformDependent" : True,
      "output" : "build",
      "dependencies" : [
        "org.graalvm.vm.x86.emu.native"
      ],
    },

    "VM_EMU86" : {
      "path" : "build/vmx86emu.jar",
      "subDir" : "vmx86",
      "sourcesPath" : "build/vmx86emu.src.zip",
      "dependencies" : [
        "org.graalvm.vm.x86.emu"
      ],
      "distDependencies" : [
        "CORE",
        "POSIX",
        "VM"
      ],
    }
  }
}
