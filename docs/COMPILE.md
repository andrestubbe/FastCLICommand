# Compiling FastCLICommand 🛠️

`FastCLICommand` is a **100% pure Java 21+ library**. It requires no C++ compiler, no native build tools, and no platform-dependent libraries.

---

## Prerequisites

- **JDK 21 or higher** (OpenJDK, Temurin, Corretto, GraalVM)
- **Apache Maven 3.8+**

Verify your environment:
```bash
java -version
mvn -version
```

---

## Building the Library

From the root of the repository:

```bash
# Clean, test, and compile the jar package
mvn clean package
```

The compiled artifact will be created in `target/FastCLICommand-0.1.0.jar`.

---

## Running the Showcase Demo

Execute the interactive showcase via the included batch script or Maven:

```cmd
run-demo.bat
```

Or manually:
```bash
mvn clean package -DskipTests
cd examples/Demo
mvn compile exec:java
```

---

## Running JMH Benchmarks

```cmd
run-benchmark.bat
```

Or manually:
```bash
cd examples/Benchmark
mvn clean package
java -jar target/benchmarks.jar -f 1 -wi 2 -i 3 -tu ms -bm thrpt
```

---

**Part of the FastJava Ecosystem** — *Making the JVM faster.*
