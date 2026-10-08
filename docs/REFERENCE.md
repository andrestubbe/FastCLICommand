# FastCLICommand API Reference Manual

`FastCLICommand` provides lightweight, reflection-free command-line argument parsing and execution dispatching for Java applications with zero external dependencies.

---

## 1. Interface: `fastclicommand.FastCLICommand`

The core contract implemented by all executable CLI commands.

### Method Index

| Method Signature | Return Type | Description |
|:---|:---|:---|
| `name()` | `String` | Unique primary command identifier (e.g. `"serve"`, `"build"`). |
| `description()` | `String` | Brief human-readable description displayed in `--help` output. |
| `aliases()` | `List<String>` | Optional alternative aliases (e.g. `List.of("s", "run")`). Defaults to empty list. |
| `options()` | `List<OptionSpec>` | Declared option and flag specifications for value binding. Defaults to empty list. |
| `execute(CLIContext context)` | `int` | Executes the command logic. Returns process exit code (`0` for success). |

---

## 2. Class: `fastclicommand.CLIContext`

Encapsulates parsed command-line parameters, options, and trailing positional text.

### Method Index

| Method Signature | Return Type | Description |
|:---|:---|:---|
| `has(String flag)` | `boolean` | Checks if a given option flag is present. Case-insensitive. |
| `get(String flag)` | `String` | Retrieves string value of an option (or `null` if not specified). |
| `get(String flag, String defaultValue)` | `String` | Retrieves string value with fallback default. |
| `getInt(String flag, int defaultValue)` | `int` | Parses integer value with fallback default. |
| `getDouble(String flag, double defaultValue)` | `double` | Parses double value (supports `.` and `,` decimal separators). |
| `getBoolean(String flag, boolean defaultValue)` | `boolean` | Parses boolean flag (`"true"`, `"1"`, `"yes"` evaluate to `true`). |
| `args()` | `List<String>` | Returns all positional non-flag arguments in original order. |
| `arg(int index)` | `String` | Retrieves positional argument at index (or `null` if out of bounds). |
| `arg(int index, String defaultValue)` | `String` | Retrieves positional argument with fallback default. |
| `joinedArgs()` | `String` | Returns raw space-joined positional text string. |

---

## 3. Class: `fastclicommand.FastCLIPublicParser`

Command-line router and tokenizer engine.

### Method Index

| Method Signature | Return Type | Description |
|:---|:---|:---|
| `register(FastCLICommand cmd)` | `FastCLIPublicParser` | Registers a command instance under its primary name and all its aliases. |
| `setDefault(FastCLICommand cmd)` | `FastCLIPublicParser` | Sets fallback default command when no subcommand token matches. |
| `dispatch(String[] args)` | `int` | Parses arguments, resolves command or help, and executes the target command. |
| `parseArguments(String[] args, List<OptionSpec> knownOptions)` | `CLIContext` | Parses tokens into a `CLIContext` using known option specifications. |
| `printGlobalHelp()` | `void` | Prints standard usage catalog of all registered commands to `System.out`. |

---

## 4. Record: `fastclicommand.OptionSpec`

Declares metadata and semantics for command-line options.

```java
public record OptionSpec(
    String name,
    String shortName,
    String description,
    boolean requiresValue,
    String defaultValue
)
```

### Constructors

- `OptionSpec(String name, String shortName, String description)`: Boolean flag (`requiresValue = false`).
- `OptionSpec(String name, String shortName, String description, String defaultValue)`: Value option with default (`requiresValue = true`).
- Canonical record constructor for full parameter control.

---

**Part of the FastJava Ecosystem** — *Making the JVM faster.*
