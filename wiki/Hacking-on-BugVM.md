## Building from source

### Prerequisites

In order to get the source code and build BugVM you will need the following tools:

* [Git](http://git-scm.com)
* [CMake](http://www.cmake.org) 2.8.8 or newer
* Java JDK 7 or newer - [Oracle's JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html) or [OpenJDK](http://openjdk.java.net) should work
* [Maven 3.x](http://maven.apache.org)
* C/C++ compiler - GCC on Linux, clang on Mac OS X

**Mac OS X**

Download and install Java SE JDK 7 from Oracle. Make sure `$JAVA_HOME` is set properly by adding
```bash
$ export JAVA_HOME=$(/usr/libexec/java_home)
```
to ~/.profile.

Install [Xcode 6.4 from the Mac App Store](https://itunes.apple.com/us/app/xcode/id497799835?mt=12). It includes clang and git. Once installed start Xcode and agree to the Xcode terms. Note Xcode 7 with iOS SDK 9.1 has some issue building vm core libraries.

Download and install CMake.

Get Maven from the URL above and install it.

**Ubuntu 12.04 (AMD64)**

Install GCC 4.7 and set it as default:
```bash
$ sudo add-apt-repository ppa:ubuntu-toolchain-r/test
$ sudo apt-get update
$ sudo apt-get install gcc-4.7 g++-4.7 gcc-4.7-multilib g++-4.7-multilib
$ sudo update-alternatives --install /usr/bin/gcc gcc /usr/bin/gcc-4.7 60 --slave /usr/bin/g++ g++ /usr/bin/g++-4.7
$ sudo update-alternatives --install /usr/bin/gcc gcc /usr/bin/gcc-4.6 40 --slave /usr/bin/g++ g++ /usr/bin/g++-4.6
```

Install other required tools:
```bash
$ sudo apt-get install build-essential git openjdk-7-jdk maven libcap-dev
```

The cmake version in Ubuntu 12.04 is too old so we use the version from Ubuntu 12.10:
```bash
$ wget http://us.archive.ubuntu.com/ubuntu/pool/main/c/cmake/cmake_2.8.9-0ubuntu1_amd64.deb
$ wget http://us.archive.ubuntu.com/ubuntu/pool/main/c/cmake/cmake-data_2.8.9-0ubuntu1_all.deb
$ sudo dpkg -i cmake*.deb
```

### Building the vm core libraries

The code of the vm core libraries is in the `vm/` folder in the repository. To start a build run 
```bash
$ vm/build.sh
```
By default this script will build both debug and release builds for all architectures that can be built on your host machine. There are few command line options to control the build type and targets to build for. For help run:
```bash
$ vm/build.sh --help
```

The build script installs the static library files produced by the build to `vm/target/binaries/`. If you've made a change to the vm code that you want to use in a BugVM distribution the libraries you've built have to be copied to `vm/binaries/`. Run
```bash
$ vm/copy-binaries.sh
```
to `rsync` the files in `vm/target/binaries/` with `vm/binaries/`. Only the release files will be synced.

### Building the other components

The rest of BugVM is built using Maven. In the root of the checked out repository:
```bash
$ mvn clean install
```

If you see errors such as:
```
com.bugvm.libimobiledevice.AfcClientTest  Time elapsed: 0 sec  <<< ERROR!
com.bugvm.libimobiledevice.LibIMobileDeviceException: LOCKDOWN_E_PASSWORD_PROTECTED
	at com.bugvm.libimobiledevice.LockdowndClient.checkResult(LockdowndClient.java:148)
	at com.bugvm.libimobiledevice.LockdowndClient.<init>(LockdowndClient.java:58)
	at com.bugvm.libimobiledevice.AfcClientTest.beforeClass(AfcClientTest.java:57)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:606)
```
make sure you do not have an iOS device connected that has **not** been provisioned for development.

## Building a distribution

First clone the `bugvm-soot` repository and install the artifact into your local Maven repository.
Then clone the `bugvm-dist` repository (must be in the same folder as the `bugvm` folder from the core repo) and run `mvn clean install`. You will then have a distribution `tar.gz` in `package/target/`.

## Source code layout

TBW

## The BUGVM_DEV_ROOT environment variable

When developing BugVM it is convenient to be able to run the compiler and link against the vm core libraries and runtime classes without creating a distribution package first. The `bin/bugvm` script and the compiler code look for the `$BUGVM_DEV_ROOT` environment variable when determining how to launch the compiler and what to link against. 

If `$BUGVM_DEV_ROOT` is set `bin/bugvm` will use the compiler matching `$BUGVM_DEV_ROOT/compiler/target/bugvm-compiler-*.jar` and the compiler will link against the vm core libraries in `vm/target/binaries/` and use the runtime classes in `$BUGVM_DEV_ROOT/rt/target/bugvm-rt-<version>.jar`.

## Debugging the vm core libraries

If you're hacking on the vm code it is often necessary to debug that code. First you have to build debug versions of the vm core libraries as described above. You can then build and debug an executable linked against the debug version of the libraries using:
```bash
$ export BUGVM_DEV_ROOT=/path/to/checked/out/repository 
$ $BUGVM_DEV_ROOT/bugvm -cp ... -verbose -use-debug-libs -d /tmp/foo org.my.main.Class
$ gdb /tmp/foo/org.my.main.Class
```

## Controlling a BugVM executable

An executable produced with BugVM can be controlled from the command line using a few special command line options. All options start with `-rvm:`. These options have to be specified before any non `-rvm:` option on the command line and will not be visible to the Java program.

* `-rvm:log=silent|fatal|error|warn|info|debug|trace`  
  Enables log messages from the vm core code. If not specified the default logging level is `error`.
* `-rvm:mx<size>[k|K|m|M|g|G]`  
  Sets the max heap size in bytes, kB, MB or GB. E.g. `-rvm:mx=128M` sets the max heap size to 128 MB.
* `-rvm:ms<size>[k|K|m|M|g|G]`  
  Sets the initial heap size. The value is interpreted the same as the `-rvm:mx` values.
* `-rvm:MainClass`  
  Sets the main class to be launched if no main class was specified when the executable was compiled.

## Running the tests

NOTE: All tests use the `$BUGVM_DEV_ROOT` environment variable. A successful build of the vm and the Maven modules must be carried out first.

### dalvik tests

The tests in `tests/dalvik` have been ported from Android's `dalvik/tests` path. To run them do:
```bash
$ cd tests/dalvik
$ ./run-all-tests --host
```

### drlvm tests

The tests in `tests/drlvm` have been taken from the DRLVM VM used by the Apache Harmony project. It contains a large number of tests that tests a VM on a very low level. Because of the size the actual tests live in a separate GitHub repo (https://github.com/bugvm/drlvm-vts-bundle) which is included as a git submodule. Here's how to run those tests:
```bash
$ git submodule init
$ git submodule update
$ cd tests/drlvm/drlvm-vts-bundle/vts/vm/build/
$ ant setup
$ ant build-vts
$ ant run-tests
```

Only the last line is usually necessary to rerun the tests. A single executable containing all test classes will be built on the first test run and cached in `/tmp/bugvm-vts.$ARCH` (most likely `/tmp/bugvm-vts.auto`). It will not be rebuilt if it already exists. To cleanup before a new test run do:
```bash
$ rm -rf /tmp/bugvm-vts.*
```

### libcore tests
The tests in `tests/libcore` check if the runtime libraries are behaving correctly. Some of the tests fail due to some missing ICU support. To run the tests:

```bash
$ cd tests/libcore
$ ant
```

This will compile all tests and create a binary in `/tmp/bugvm-libcore.$OS.$ARCH.$timestamp`. The JUnit report does not report individual tests, the console output has to be inspected for that.

You can attach lldb for debugging as follows:
```bash
$ cd /tmp/bugvm-libcore.$OS.$ARCH.$timestamp
$ lldb ./test -- -rvm:mx256M -rvm:MainClass=BugVMAllTests
```

The Java code has no debugging symbols, but the native parts of the runtime library have.

## Working with the code in Eclipse

TBW

## Syncing with Android

TBW

## Performing a release

TBW