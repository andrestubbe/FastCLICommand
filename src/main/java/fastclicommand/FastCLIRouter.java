package fastclicommand;

import java.io.PrintStream;
import java.util.*;

/**
 * High-performance command router and registry.
 * Vorkompiliert Commands beim Registrieren, vermeidet copyOfRange und bietet vollständige Hilfe-Generierung.
 */
public final class FastCLIRouter {

    private final Map<String, CompiledCommand> commandMap = new LinkedHashMap<>();
    private final FastCLIParser parser;
    private CompiledCommand defaultCommand;
    private PrintStream out = System.out;
    private PrintStream err = System.err;

    public FastCLIRouter() {
        this(new FastCLIParser(false));
    }

    public FastCLIRouter(FastCLIParser parser) {
        this.parser = Objects.requireNonNull(parser, "parser");
    }

    public FastCLIRouter setOutputStreams(PrintStream out, PrintStream err) {
        if (out != null) this.out = out;
        if (err != null) this.err = err;
        return this;
    }

    public FastCLIRouter register(FastCLICommand cmd) {
        Objects.requireNonNull(cmd, "cmd");
        String name = cmd.name();
        Objects.requireNonNull(name, "cmd.name()");
        if (name.isBlank()) throw new IllegalArgumentException("Command name cannot be blank");

        CompiledCommand compiled = new CompiledCommand(cmd);
        String primaryKey = name.toLowerCase(Locale.ROOT);
        if (commandMap.containsKey(primaryKey)) {
            throw new IllegalArgumentException("Duplicate command name: " + primaryKey);
        }
        commandMap.put(primaryKey, compiled);

        if (cmd.aliases() != null) {
            for (String alias : cmd.aliases()) {
                if (alias == null || alias.isBlank()) continue;
                String aliasKey = alias.toLowerCase(Locale.ROOT);
                if (commandMap.containsKey(aliasKey)) {
                    throw new IllegalArgumentException("Duplicate alias '" + aliasKey + "' for command: " + name);
                }
                commandMap.put(aliasKey, compiled);
            }
        }
        return this;
    }

    public FastCLIRouter setDefault(FastCLICommand cmd) {
        this.defaultCommand = (cmd != null) ? new CompiledCommand(cmd) : null;
        return this;
    }

    public int dispatch(String[] args) {
        if (args == null || args.length == 0) {
            if (defaultCommand != null) {
                try {
                    return defaultCommand.command().execute(parser.parse(args, 0, 0, defaultCommand));
                } catch (Exception e) {
                    err.println("[CLI] Command error: " + e.getMessage());
                    return 1;
                }
            }
            printGlobalHelp();
            return 0;
        }

        String first = args[0].toLowerCase(Locale.ROOT);
        if (first.equals("--help") || first.equals("-h") || first.equals("/?")) {
            printGlobalHelp();
            return 0;
        }

        CompiledCommand target = commandMap.get(first);
        int fromOffset = 1;

        if (target == null) {
            target = defaultCommand;
            fromOffset = 0;
        }

        if (target == null) {
            err.println("[CLI] Unknown command: " + args[0]);
            printGlobalHelp();
            return 2;
        }

        // Subcommand-spezifische Hilfe erkennen
        if (args.length > fromOffset && isHelpFlag(args[fromOffset])) {
            printCommandHelp(target);
            return 0;
        }

        try {
            CLIContext ctx = parser.parse(args, fromOffset, args.length, target);
            return target.command().execute(ctx);
        } catch (CLIParseException e) {
            err.println("[CLI] Parse error: " + e.getMessage());
            return 2;
        } catch (Exception e) {
            err.println("[CLI] Execution failure: " + e.getMessage());
            return 1;
        }
    }

    private static boolean isHelpFlag(String s) {
        return s.equalsIgnoreCase("--help") || s.equalsIgnoreCase("-h") || s.equalsIgnoreCase("/?");
    }

    public void printGlobalHelp() {
        out.println("Usage: [command] [options] [arguments]");
        out.println();
        out.println("Available Commands:");
        Set<CompiledCommand> distinct = new LinkedHashSet<>(commandMap.values());
        for (CompiledCommand c : distinct) {
            FastCLICommand cmd = c.command();
            out.printf("  %-16s %s%n", cmd.name(), cmd.description());
        }
    }

    public void printCommandHelp(CompiledCommand compiled) {
        FastCLICommand cmd = compiled.command();
        out.println("Command: " + cmd.name() + " - " + cmd.description());
        if (cmd.aliases() != null && !cmd.aliases().isEmpty()) {
            out.println("Aliases: " + String.join(", ", cmd.aliases()));
        }
        out.println();
        out.println("Options:");
        for (OptionSpec opt : compiled.declaredOptions()) {
            String shortPart = (opt.shortKey() != null) ? (opt.shortKey() + ", ") : "    ";
            String longPart = String.format("%-18s", opt.longKey());
            String defPart = (opt.defaultValue() != null) ? (" (default: " + opt.defaultValue() + ")") : "";
            out.printf("  %s%s %s%s%n", shortPart, longPart, opt.description(), defPart);
        }
    }
}
