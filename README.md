# FastCLICommand 0.1.0 [ALPHA-2026-10] — Zero-Allocation, Ultra-Fast Command Line Parser and Dispatcher for Java

[![Status](https://img.shields.io/badge/status-0.1.0-brightgreen.svg)](https://github.com/andrestubbe/FastCLICommand/releases/tag/0.1.0)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-21+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Windows%20%7C%20Linux%20%7C%20macOS-lightgrey.svg)]()
[![JitPack](https://img.shields.io/badge/JitPack-0.1.0-green.svg)](https://jitpack.io/#andrestubbe/FastCLICommand)

---

**⚡ Ultra-fast command-line routing, parameter parsing, and sub-microsecond CLI dispatch for Java.**

**FastCLICommand** is the lightweight command-line argument parser and execution router of the **FastJava** ecosystem. Engineered for high-frequency tooling, CLI utilities, and real-time terminal environments, it bypasses heavy reflection scans, complex annotation processing, and bloated dependencies to deliver sub-microsecond command routing, flexible syntax parsing (`--flag`, `--key=val`, `--key:val`, `--key val`, `-f`), and zero garbage collection pressure.

---

## Quick Start — Example

```java
import fastclicommand.*;
import java.util.List;

public class Demo {
    public static void main(String[] args) throws Exception {
        FastCLIPublicParser cli = new FastCLIPublicParser();

        // 1. Register command
        cli.register(new FastCLICommand() {
            @Override
            public String name() { return "serve"; }

            @Override
            public String description() { return "Starts HTTP server"; }

            @Override
            public List<OptionSpec> options() {
                return List.of(new OptionSpec("port", "p", "Server port", "8080"));
            }

            @Override
            public int execute(CLIContext ctx) {
                int port = ctx.getInt("--port", 8080);
                System.out.println("Serving on port: " + port);
                return 0;
            }
        });

        // 2. Dispatch CLI arguments
        cli.dispatch(new String[]{"serve", "--port=3000"});
    }
}
```

---

## Table of Contents

- [Why FastCLICommand?](#why-fastclicommand)
- [Key Features](#key-features)
- [Architecture & Pipeline](#architecture--pipeline)
- [Performance & Benchmarks](#performance--benchmarks)
- [Syntax & Argument Parsing](#syntax--argument-parsing)
- [Installation](#installation)
- [Technical Demos & Benchmarks](#technical-demos--benchmarks)
- [Documentation](#documentation)
- [Platform Support](#platform-support)
- [License](#license)
- [Related Projects](#related-projects)

---

## Why FastCLICommand?

Existing Java command-line libraries (such as Picocli, JCommander, and Apache Commons CLI) were designed decades ago and incur significant startup overhead and architectural baggage:

1. **Heavyweight Reflection & Bytecode Scanning**: Annotation-heavy CLI frameworks inspect classes and methods via runtime reflection, adding 20–100 ms to application startup and bloating class-loading overhead.
2. **Excessive Object Allocations**: Traditional parsers construct complex ASTs, option models, and intermediate collections for simple CLI commands, generating heap churn.
3. **Rigid Parameter Syntax**: Many libraries enforce single delimiter conventions and fail on common variations such as colon (`--param:value`) or space-separated values without verbose configuration.
4. **Heavy Transitive Dependencies**: Small CLI tools frequently pull megabytes of external dependencies just to parse command flags.

| Feature | Apache Commons CLI | Picocli | FastCLICommand |
|:---|:---:|:---:|:---:|
| **Startup Overhead** | Medium (~15–30 ms) | High (~30–90 ms reflection) | **Sub-microsecond (< 1 µs)** |
| **Throughput** | ~25,000 ops/sec | ~60,000 ops/sec | **> 1,500,000 ops/sec** |
| **Reflection / Annotations**| Optional | Required | **None (Pure Interface Contract)** |
| **Dependencies** | External JAR | External Annotations + Lib | **Zero Dependencies (Pure Java 21+)** |
| **GC Pressure** | High | Medium | **Near-Zero Allocation** |

---

## Key Features

- ⚡ **Ultra-Fast Dispatching** — Dispatches subcommands and routes flags at over **1.5 million executions per second**.
- 🛠️ **Flexible Argument Syntax** — Seamlessly parses GNU-style flags (`--verbose`), short flags (`-v`), equals pairs (`--port=8080`), colon pairs (`--port:8080`), and space-separated values (`--port 8080`).
- 🎯 **Type-Safe `CLIContext`** — Direct zero-overhead accessors (`ctx.getInt(...)`, `ctx.getDouble(...)`, `ctx.getBoolean(...)`, `ctx.get(...)`, `ctx.joinedArgs()`).
- 🏷️ **Subcommands & Aliases** — Built-in support for hierarchical commands and shorthand aliases (e.g. `b` for `build`, `s` for `serve`).
- 📖 **Automatic Help Generation** — Clean, formatted global help generation (`--help`, `-h`, `/?`) with command descriptions and option specifications.
- 🪶 **Zero Dependency Bloat** — 100% pure Java 21+. Zero external dependencies, no native binaries, no annotation processors.

---

## Architecture & Pipeline

```
┌─────────────────────────────────────────────────────────────┐
│                    Command Line Arguments                   │
│         ["serve", "--port=8080", "-v", "src/main"]          │
└──────────────────────────────┬──────────────────────────────┘
                               │ FastCLIPublicParser.dispatch(args)
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                 Command Resolution & Routing                │
│             (Name lookup + Aliases + Default cmd)           │
└──────────────────────────────┬──────────────────────────────┘
                               │ Parse Remaining Tokens
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                    Token Parsing Engine                     │
│      --key=val | --key:val | --key val | -f | Positional    │
└──────────────────────────────┬──────────────────────────────┘
                               │ Context Construction
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                 Type-Safe CLIContext                        │
│          getInt() | getBoolean() | arg() | joined()         │
└──────────────────────────────┬──────────────────────────────┘
                               │ execute(ctx)
                               ▼
┌─────────────────────────────────────────────────────────────┐
│             FastCLICommand Execution Contract               │
└─────────────────────────────────────────────────────────────┘
```

---

## Performance & Benchmarks

Measured on OpenJDK 21 LTS, Windows 11 x64:

| Operation | Throughput | Latency | Allocation Churn |
|:---|---:|---:|---:|
| **Command Routing & Dispatch** | **> 1,500,000 dispatches/sec** | **< 650 ns** | Minimal (single context) |
| **Argument Tokenization & Parse** | **> 2,000,000 ops/sec** | **< 500 ns** | Flat dictionary lookup |
| **Typed Getter Evaluation (`getInt`)**| **> 15,000,000 ops/sec** | **< 65 ns** | Inlined parsing |

---

## Syntax & Argument Parsing

`FastCLICommand` supports all common terminal input conventions:

```bash
# Long flag (boolean true)
myapp run --verbose

# Short flag (boolean true)
myapp run -v

# Equals delimiter
myapp run --threads=16

# Colon delimiter
myapp run --config:prod.json

# Space-separated value (configured via OptionSpec.requiresValue)
myapp run --port 8080

# Positional arguments & trailing text
myapp run file1.txt file2.txt --output result.bin
```

---

## Installation

### Option 1: Maven (Recommended via JitPack)

Add the JitPack repository and dependency to your `pom.xml`:

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

### Option 2: Gradle (via JitPack)

Add this to your `build.gradle`:

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.andrestubbe:FastCLICommand:0.1.0'
}
```

### Option 3: Direct Download (Pre-built JAR)

Download the latest pre-compiled JAR directly from GitHub Releases:

1. 📦 [**FastCLICommand-0.1.0.jar**](https://github.com/andrestubbe/FastCLICommand/releases/download/0.1.0/FastCLICommand-0.1.0.jar)

---

## Technical Demos & Benchmarks

| Case | Java Example | Launcher | Description |
|:---|:---|:---|:---|
| **Interactive Showcase Demo** | [Demo.java](examples/Demo/src/main/java/fastclicommand/demo/Demo.java) | `run-demo.bat` | End-to-end demonstration of command registration, help generation, flag parsing, and micro-throughput dispatching. |
| **JMH Microbenchmark Suite** | [Benchmark.java](examples/Benchmark/src/main/java/fastclicommand/benchmark/Benchmark.java) | `run-benchmark.bat` | Formal OpenJDK JMH throughput measurements across CLI routing, argument tokenization, and typed getters. |

---

## Documentation

* **[REFERENCE.md](docs/REFERENCE.md)**: Exhaustive catalog of API contracts, interfaces, and options.
* **[PHILOSOPHY.md](docs/PHILOSOPHY.md)**: Design principles: reflection-free design, sub-microsecond startup, and deterministic execution.
* **[ROADMAP.md](docs/ROADMAP.md)**: Planned milestone features and extensions.
* **[CHANGELOG.md](docs/CHANGELOG.md)**: Release history and version notes.
* **[COMPILE.md](docs/COMPILE.md)**: Compilation guide and test execution.

---

## Platform Support

FastCLICommand is a **pure Java 21+ library** with zero native dependencies. It runs identically across all platforms:

| Platform | Architecture | Status | Notes |
|:---|:---:|:---:|:---|
| **Windows 10/11** | x64, ARM64 | ✅ Fully Supported | 100% Pure Java 21+ |
| **Linux** | x64, ARM64 | ✅ Fully Supported | 100% Pure Java 21+ |
| **macOS** | Apple Silicon, x64 | ✅ Fully Supported | 100% Pure Java 21+ |

---

## License

MIT License — See [LICENSE](LICENSE) for details.

---

## Related Projects

- [FastANSI](https://github.com/andrestubbe/FastANSI) — Zero-allocation ANSI and VT100/VT220 escape sequence parser and compositor
- [FastASCII](https://github.com/andrestubbe/FastASCII) — Zero-allocation ASCII/UTF-8 byte engine and high-throughput primitive parsing
- [FastConPTY](https://github.com/andrestubbe/FastConPTY) — High-performance native Windows ConPTY pseudo-terminal backend
- [FastTerminal](https://github.com/andrestubbe/FastTerminal) — High-performance True-Color double-buffered terminal rendering engine
- [FastTerminal3D](https://github.com/andrestubbe/FastTerminal3D) — Real-time software 3D rasterization bridge inside the terminal
- [FastTUI](https://github.com/andrestubbe/FastTUI) — High-performance native Windows TUI framework with mouse support and widgets
- [FastCore](https://github.com/andrestubbe/FastCore) — Native library loader, FFM gateway, and platform abstraction layer

---

**Part of the FastJava Ecosystem** — *Making the JVM faster. Small package. Maximum speed. Zero bloat. 🚀📋*
