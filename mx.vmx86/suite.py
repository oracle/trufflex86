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
      "sha1" : "33883f4c13c6a72670b38e576f84f3bd753bdfa4",
    },

    "POSIX" : {
      "path" : "lib/posix.jar",
      "urls" : [
        "https://orakel.has.enough.coffee/repo/everyware/posix.jar"
      ],
      "sha1" : "535e5700ba0f041218e124f49c078854d3d23300",
    },
  },

  "defaultLicense" : "GPLv3",

  "projects" : {
    "org.graalvm.vm.memory" : {
      "subDir" : "projects",
      "sourceDirs" : ["src"],
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
    }
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
    }
  }
}
