trufflex86
==========

trufflex86 is an x86\_64 interpreter built on top of the GraalVM.


Build Dependencies
==================

- gcc for libmemory (high performance memory library) and libemu86 (ptrace based debugging tool)
- Linux/x86\_64 host with gcc to build the test cases

It *is* possible to build trufflex86 on any non-Linux/x86\_64 host as long as mx and a JDK are available. However, such configurations are *not tested*. If trufflex86 is built on any host other than Linux/x86\_64, the test programs have to be cross-compiled or copied from a Linux/x86\_64 host. It might be necessary to change the `suite.py` in order to do that.


How to get started
==================

Create a new directory, e.g. `git` which will contain all necessary git repositories:

```
mkdir git && cd git
```

Install mx which is used to build trufflex86:

```
git clone https://github.com/graalvm/mx
export PATH=$PWD/mx:$PATH
```

Clone the Graal repository as well as this repository:

```
git clone https://github.com/oracle/graal
git clone https://github.com/oracle/trufflex86
```

Set `JAVA_HOME` to a JDK >= 1.8, e.g.:

```
echo JAVA_HOME=/usr/lib/jvm/java-11-openjdk > trufflex86/mx.trufflex86/env
```

Build trufflex86:

```
cd trufflex86 && mx build
```

Running a program
-----------------

To run a program, start it with `mx vmx86`:

```
mx vmx86 /bin/uname -a
```

Running on Graal
----------------

In order to use Graal as a JIT compiler, first build Graal:

```
mx --dynamicimport /compiler build
```

Then:

```
mx --dynamicimport /compiler --jdk jvmci vmx86 /bin/uname -a
```

IDE Setup
---------

Generate Eclipse project files:

```
mx eclipseinit
```

Generate IntelliJ project files:

```
mx intellijinit
```


Execution Traces
================

First, record an execution trace:

```
mx -J"-Dvmx86.debug.exec=true -Dvmx86.debug.exec.trace=true -Dvmx86.debug.exec.tracefile=trace-file.trc -Dmem.virtual=true -Dposix.strace=true" vmx86 /bin/echo hello
```

Then, inspect it in the trace viewer:

```
mx trcview
```

Debugging the Interpreter
-------------------------

To check if all CPU instructions are implemented correctly (at least for a given program execution), one can also verify an execution trace on the host CPU (as long as the host has an x86\_64 CPU):

```
mx trchk trace-file.trc
```
