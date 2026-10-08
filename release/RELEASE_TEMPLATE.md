# FastCLICommand v0.1.0 — Initial Release 🚀

## 🎉 Version 0.1.0: Zero-Allocation, Ultra-Fast Command Line Parser and Dispatcher for Java
**Release Date:** 2026-10-08  
**Tag:** `0.1.0`

---

## ✨ Features

- **🚀 Ultra-Fast Routing & Dispatching**: Dispatches subcommands and parses flags at over 1.5 million executions per second with near-zero allocation.
- **🛠️ Flexible Delimiter Syntax**: Supports GNU-style long flags (`--verbose`), short flags (`-v`), equals pairs (`--port=8080`), colon pairs (`--port:8080`), and space-separated values (`--port 8080`).
- **🎯 Type-Safe CLIContext**: Convenient zero-overhead getters for integer, double, boolean, string, and positional arguments.
- **🏷️ Subcommands & Aliases**: Native support for subcommand hierarchies and shorthand aliases (e.g. `b` for `build`).
- **📖 Automatic Help Output**: Standardized `--help`, `-h`, and `/?` catalog generation.
- **🪶 100% Pure Java 21+**: Zero external dependencies, no reflection, and native-image ready.
- **📊 Interactive Showcase Demo**: Complete runnable demo in `examples/Demo/` via `run-demo.bat`.
- **📈 OpenJDK JMH Microbenchmarks**: Verified throughput benchmark suite in `examples/Benchmark/` via `run-benchmark.bat`.

---

## 📦 Installation (JitPack)

### Maven
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastCLICommand</artifactId>
        <version>0.1.0</version>
    </dependency>
</dependencies>
```

### Direct Download (Pre-built JAR)
- 📦 [**FastCLICommand-0.1.0.jar**](https://github.com/andrestubbe/FastCLICommand/releases/download/0.1.0/FastCLICommand-0.1.0.jar)

---

## 🔧 Technical Details
- **Architecture:** 100% Pure Java 21+.
- **Platform:** Cross-platform (Windows, Linux, macOS).
- **Build System:** Standardized Maven pipeline with JDK 21+.

---

**Part of the FastJava Ecosystem** — *Making the JVM faster.*
