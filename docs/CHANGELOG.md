# Changelog — FastCLICommand 📜

All notable changes to **FastCLICommand** will be documented in this file.
The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [0.1.2] — 2026-10-08

### Changed
- **Parser Robustness & Token Handling**:
  - Fixed value option consumption to prevent swallowing subsequent flags when an argument value is missing (`--env --verbose`).
  - Added full boolean value semantics in `CLIContext.has()` and `CLIContext.getBoolean()` respecting explicit `:false`, `=false`, and `0` values.
  - Added lenient fallback behavior for `getInt(..., defaultValue)` and `getDouble(..., defaultValue)`.
  - Added `CLIContext.contains()` for pure presence checks regardless of truthiness.
  - Enforced key normalization in `OptionSpec` compact constructor to prevent normalization bypasses.
  - Eliminated per-call dummy class allocation in `FastCLIPublicParser.parseArguments()` via `CompiledCommand` option compilation.

## [0.1.1] — 2026-10-08

### Changed
- **Zero-Allocation Hot Path Optimization**:
  - Replaced intermediate `copyOfRange` with direct `(from, to)` slice indexing in `FastCLIParser`.
  - Converted `CLIContext` to a slice-based architecture deferring positional argument concatenation (`joinedArgs()` is now strictly lazy).
  - Precompiled command option lookups at registration time via `CompiledCommand`, eliminating runtime `HashSet` and allocation churn.
- **Robustness & Semantic Correctness**:
  - Normalized long and short keys in `OptionSpec` (`longKey()`, `shortKey()`) ensuring consistent lookups across all conventions.
  - Added full support for negative numeric arguments (e.g. `--rate -1`, `-5.5`) without misinterpreting them as flags.
  - Added standard `--` end-of-options delimiter support for raw positional text.
  - Added `CLIParseException` for strict typing instead of silent error swallowing.
  - Split router and tokenizer concerns cleanly into `FastCLIRouter` and `FastCLIParser`.

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
