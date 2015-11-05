BugVM can target Linux (x86 32-bit and 64-bit) and the tools can be run under Linux. But please note that **it is not yet possible to produce iOS apps under Linux**. You are restricted to building Linux console executables at this time. The instructions below are for Ubuntu 15.10 and may have to be changed if you use a more recent Ubuntu version or some other Linux distribution.

Install required packages:
```
sudo apt-get install build-essential gcc-multilib g++-multilib openjdk-7-jdk zlibc
```

Download and extract the latest BugVM release (currently 1.8.0):
```
wget "http://download.bugvm.com/bugvm-1.8.0.tar.gz"
tar xvfz bugvm-1.8.0.tar.gz
```

Create a simple Hello World sample and compile it using `javac`:

```
cat << EOF > HelloWorld.java
public class HelloWorld {
    public static void main(String[] args) {
         System.out.println("Hello world!");
     }
}
EOF
mkdir classes
javac -d classes/ HelloWorld.java
```

Finally, compile the Java bytecode into native code and run the program using:
```
bugvm-1.8.0/bin/bugvm -verbose -cp classes/ -run HelloWorld
```
