# FastCLICommand Roadmap

## Milestone Status

### Zero-Allocation Parser & Robustness (v0.1.1)
**Status:** Released
- [x] Zero-allocation token slicing with direct index offsets in `FastCLIParser`.
- [x] Lazy positional text slicing in `CLIContext`.
- [x] Registration-time `CompiledCommand` pre-lookup eliminating runtime hash allocations.
- [x] Robust negative number parsing without flag collision.
- [x] Standard `--` end-of-options delimiter support.
- [x] Strict `CLIParseException` hierarchy.

### Core Parsing & Dispatching Engine (v0.1.0)
**Status:** Released
- [x] Standard `FastCLICommand` interface contract with name, description, aliases, and option specifications.
- [x] Type-safe `CLIContext` with primitive accessors (`getInt`, `getDouble`, `getBoolean`).
- [x] Flexible delimiter parser: supports equals (`--key=val`), colon (`--key:val`), and space (`--key val`).
- [x] POSIX short flags and GNU-style long flags.
- [x] Subcommand resolution, alias mapping, and fallback default command.
- [x] Automatic global `--help` catalog generation.
- [x] Zero external dependencies and native-image friendly pure Java design.
- [x] OpenJDK JMH microbenchmark suite and interactive showcase demo.

---

## Upcoming Features

### Shell Autocompletion Generation
**Status:** In Progress
- [ ] Automated bash / zsh / PowerShell completion script generation from registered `OptionSpec` lists.

### Interactive Tab Prompting
**Status:** In Progress
- [ ] Direct integration with `FastTerminal` and `FastANSI` for interactive terminal prompts when required arguments are missing.

### Nested Subcommand Hierarchies
**Status:** Backlog
- [ ] Multi-level command trees (`git remote add ...`) with recursive dispatch contexts.
