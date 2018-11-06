import os

import mx
import mx_subst
import mx_sdk
import mx_unittest

_suite = mx.suite('vmx86')
_root = os.path.join(_suite.dir, 'projects')
_build = os.path.join(_suite.dir, 'build');

class VMX86TestSuite(mx.NativeProject):
    def __init__(self, suite, name, deps, workingSets, subDir, results=None, output=None, **args):
        d = os.path.join(suite.dir, subDir) # use common Makefile for all test suites
        mx.NativeProject.__init__(self, suite, name, subDir, [], deps, workingSets, results, output, d, **args)
        self.vpath = True

    def getTests(self):
        if not hasattr(self, '_tests'):
            self._tests = []
            root = os.path.join(self.dir, self.name)
            src = os.path.join(root, 'src')
            lib = os.path.join(root, 'lib')
            for path, _, files in os.walk(src):
                for f in files:
                    absPath = os.path.join(path, f)
                    relPath = os.path.relpath(absPath, root)
                    test, ext = os.path.splitext(relPath)
                    if ext in ['.c', '.cpp', '.s']:
                        self._tests.append(test)
        return self._tests

    def getBuildEnv(self, replaceVar=mx_subst.path_substitutions):
        env = super(VMX86TestSuite, self).getBuildEnv(replaceVar=replaceVar)
        env['VPATH'] = os.path.join(self.dir, self.name)
        env['PROJECT'] = self.name
        env['TESTS'] = ' '.join(self.getTests())
        return env

    def getResults(self, replaceVar=mx_subst.results_substitutions):
        if not self.results:
            self.results = []
            for t in self.getTests():
                self.results.append(t + '.elf')
        return super(VMX86TestSuite, self).getResults(replaceVar=replaceVar)

def truffle_extract_VM_args(args, useDoubleDash=False):
    vmArgs, remainder = [], []
    if args is not None:
        for (i, arg) in enumerate(args):
            if any(arg.startswith(prefix) for prefix in ['-X', '-G:', '-D', '-verbose', '-ea', '-da', '-agentlib']) or arg in ['-esa']:
                vmArgs += [arg]
            elif useDoubleDash and arg == '--':
                remainder += args[i:]
                break
            else:
                remainder += [arg]
    return vmArgs, remainder

def getCommonOptions(withAssertion):
    options = [
            '-Dgraal.TruffleCompilationExceptionsArePrinted=true',
            '-Dgraal.ExitVMOnException=true',
            '-Dgraal.TruffleOSRCompilationThreshold=10',
            '-Dgraal.TruffleCompilationThreshold=10',
            '-Dgraal.MaximumLoopExplosionCount=10000']

    library_path = ['/usr/java/packages/lib', '/usr/lib64', '/lib64', '/lib', '/usr/lib']
    library_path += [mx_subst.path_substitutions.substitute('<path:VM_MEMORY_NATIVE>')]

    options += ['-Xss56m', '-Xms4g', '-Xmx4g']
    options += ['-Djava.library.path=' + os.pathsep.join(library_path)]
    options += getAMD64RootOptions()
    if withAssertion:
        options += ['-ea', '-esa']

    return options

def getAMD64RootOptions():
    return ["-Dvmx86test.projectRoot=" + _root,
            mx_subst.path_substitutions.substitute('-Dvmx86test.testSuitePath=<path:VM_TESTCASES>')]

def getClasspathOptions():
    """gets the classpath of the vmx86 distributions"""
    return mx.get_runtime_jvm_args(['VM', 'VMX86_LAUNCHER', 'CORE', 'POSIX'])

def getTrcviewClasspathOptions():
    """gets the classpath of the trcview distributions"""
    return mx.get_runtime_jvm_args(['VM', 'VMX86_TRCVIEW', 'CORE', 'POSIX'])

def runAMD64(args=None, out=None):
    """uses vmx86 to execute a Linux/x86_64 ELF binary"""
    vmArgs, vmx86Args = truffle_extract_VM_args(args)
    return mx.run_java(getCommonOptions(False) + vmArgs + getClasspathOptions() + ['org.graalvm.vm.x86.launcher.AMD64Launcher'] + vmx86Args, out=out)

def runTrcview(args=None, out=None):
    """GUI tool to inspect execution traces"""
    vmArgs, trcviewArgs = truffle_extract_VM_args(args)
    return mx.run_java(getCommonOptions(False) + vmArgs + getTrcviewClasspathOptions() + ['org.graalvm.vm.x86.trcview.ui.MainWindow'] + trcviewArgs, out=out)

def runTrcdump(args=None, out=None):
    """CLI tool to convert binary execution traces to ascii format"""
    vmArgs, trcviewArgs = truffle_extract_VM_args(args)
    return mx.run_java(getCommonOptions(False) + vmArgs + getTrcviewClasspathOptions() + ['org.graalvm.vm.x86.trcview.TextDump'] + trcviewArgs, out=out)

def _unittest_config_participant(config):
    (vmArgs, mainClass, mainClassArgs) = config
    vmArgs = getCommonOptions(True) + vmArgs
    return (vmArgs, mainClass, mainClassArgs)

mx_unittest.add_config_participant(_unittest_config_participant)

mx_sdk.register_graalvm_component(mx_sdk.GraalVmLanguage(
    suite=_suite,
    name='vmx86',
    short_name='vmx86',
    dir_name='amd64',
    license_files=[],
    third_party_license_files=[],
    truffle_jars=['vmx86:VM', 'vmx86:VM_MEMORY_SVM', 'vmx86:CORE', 'vmx86:POSIX'],
    support_distributions=[
        'vmx86:VMX86_GRAALVM_SUPPORT'
    ],
    polyglot_lib_build_args=[
        '-H:CLibraryPath=<path:VMX86_GRAALVM_SUPPORT>/clibraries/<os>-<arch>'
    ],
    launcher_configs=[
        mx_sdk.LanguageLauncherConfig(
            destination='bin/<exe:vmx86>',
            jar_distributions=['vmx86:VMX86_LAUNCHER'],
            main_class='org.graalvm.vm.x86.launcher.AMD64Launcher',
            build_args=[
                '--language:amd64',
                '-H:CLibraryPath=<path:VMX86_GRAALVM_SUPPORT>/clibraries/<os>-<arch>'
            ]
        )
    ],
))

mx.update_commands(_suite, {
    'vmx86' : [runAMD64, ''],
    'trcview' : [runTrcview, ''],
    'trcdump' : [runTrcdump, '']
})
