# Changelog — FastCLICommand 📜

All notable changes to **FastCLICommand** will be documented in this file.
The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [0.1.0] — 2026-10-08

### Added
- **Core Architecture**:
  - `FastCLICommand` interface contract for executable subcommands with metadata and option specs.
  - `FastCLIPublicParser` high-speed router supporting command lookup, aliases, and default commands.
  - `CLIContext` with type-safe accessors (`getInt`, `getDouble`, `getBoolean`, `args`, `joinedArgs`).
  - `OptionSpec` record for declaring long/short flags, values, and default fallbacks.
  - Multi-syntax parser supporting equals (`--key=val`), colon (`--key:val`), space-separated values, and flags.
  - Formatted usage and help generation (`--help`, `-h`, `/?`).
- **Tooling & Examples**:
  - `examples/Demo`: Interactive showcase demonstrating multi-command routing and throughput.
  - `examples/Benchmark`: JMH microbenchmark suite measuring routing and token parsing.
  - `run-demo.bat` and `run-benchmark.bat` launcher scripts.
  - Complete documentation suite (`README.md`, `REFERENCE.md`, `PHILOSOPHY.md`, `ROADMAP.md`, `COMPILE.md`).
