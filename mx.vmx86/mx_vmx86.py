import os

import mx
import mx_subst

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
