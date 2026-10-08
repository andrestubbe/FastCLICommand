# The Philosophy of FastCLICommand

> [!IMPORTANT]
> **"Zero Allocation. Zero Reflection. Deterministic Sub-Microsecond Dispatch."**

FastCLICommand is built on the principle that command-line interfaces for modern Java tools, CLI utilities, and daemon runners should **start instantaneously and execute deterministically**, without paying the runtime tax of reflection scans, annotation processing, or external dependencies.

---

## Core Tenets

### 1. Reject Runtime Reflection
Traditional CLI frameworks (such as Picocli or args4j) rely heavily on field reflection, annotations, and dynamic bytecode inspection. In microservices, CLI tools, and GraalVM Native Image binaries, reflection causes noticeable startup delays (20–100 ms) and requires fragile metadata reflection configurations. FastCLICommand uses pure Java interface contracts (`FastCLICommand.execute(ctx)`), guaranteeing instant cold-start execution.

### 2. Flexible Real-World Syntax
Developers and end users format CLI parameters differently:
- GNU-style: `--port=8080`
- Windows/PowerShell style: `--port:8080`
- Space-separated: `--port 8080`
- POSIX short flags: `-p 8080` or `-v`

FastCLICommand natively normalizes all four syntaxes in a single fast token pass without requiring tedious configuration.

### 3. Allocation-Aware Context
Commands frequently execute in loops, test runners, or high-throughput batch scripts. `CLIContext` avoids heavy intermediate collection wrappers and provides direct primitive-parsing accessors (`getInt`, `getBoolean`, `getDouble`) with fallback defaults, keeping heap allocation to an absolute minimum.

### 4. Zero Dependencies
A CLI argument parser should never be larger than the application it powers. FastCLICommand has **0 external dependencies** and runs as a pure Java library, making it trivial to embed into any FastJava tool or standalone utility.

---

**⚡ FastCLICommand — Powering the command-line interfaces of FastJava.**
