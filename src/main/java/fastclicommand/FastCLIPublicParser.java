package fastclicommand;

import java.util.*;

/**
 * Ultra-fast command-line parser and router.
 * Handles flags (--flag, -f), key-values (--key=val, --key:val, --key val), and positional text.
 */
public final class FastCLIPublicParser {

    private final Map<String, FastCLICommand> commandMap = new LinkedHashMap<>();
    private FastCLICommand defaultCommand;

    public FastCLIPublicParser() {}

    public FastCLIPublicParser register(FastCLICommand cmd) {
        if (cmd == null) return this;
        commandMap.put(cmd.name().toLowerCase(), cmd);
        for (String alias : cmd.aliases()) {
            commandMap.put(alias.toLowerCase(), cmd);
        }
        return this;
    }

    public FastCLIPublicParser setDefault(FastCLICommand cmd) {
        this.defaultCommand = cmd;
        return this;
    }

    public int dispatch(String[] args) throws Exception {
        if (args == null || args.length == 0) {
            if (defaultCommand != null) {
                return defaultCommand.execute(new CLIContext(Collections.emptyMap(), Collections.emptyList(), ""));
            }
            printGlobalHelp();
            return 0;
        }

        String first = args[0].toLowerCase();
        if (first.equals("--help") || first.equals("-h") || first.equals("/?")) {
            printGlobalHelp();
            return 0;
        }

        FastCLICommand cmd = commandMap.get(first);
        String[] commandArgs;
        if (cmd != null) {
            commandArgs = Arrays.copyOfRange(args, 1, args.length);
        } else {
            cmd = defaultCommand;
            commandArgs = args;
        }

        if (cmd == null) {
            System.err.println("[CLI] Unknown command: " + args[0]);
            printGlobalHelp();
            return 1;
        }

        CLIContext ctx = parseArguments(commandArgs, cmd.options());
        return cmd.execute(ctx);
    }

    public CLIContext parseArguments(String[] args, List<OptionSpec> knownOptions) {
        Map<String, String> parsedOptions = new HashMap<>();
        List<String> positional = new ArrayList<>();
        StringBuilder joinedPositional = new StringBuilder();

        // Build lookup for known flags requiring value
        Set<String> valueFlags = new HashSet<>();
        if (knownOptions != null) {
            for (OptionSpec opt : knownOptions) {
                if (opt.requiresValue()) {
                    if (opt.name() != null) valueFlags.add(opt.name().toLowerCase());
                    if (opt.shortName() != null) valueFlags.add(opt.shortName().toLowerCase());
                }
            }
        }

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg == null || arg.isBlank()) continue;

            if (arg.startsWith("-")) {
                int colonIdx = arg.indexOf(':');
                int equalIdx = arg.indexOf('=');
                int splitIdx = -1;
                if (colonIdx > 0 && equalIdx > 0) splitIdx = Math.min(colonIdx, equalIdx);
                else if (colonIdx > 0) splitIdx = colonIdx;
                else if (equalIdx > 0) splitIdx = equalIdx;

                if (splitIdx > 0) {
                    String key = arg.substring(0, splitIdx).toLowerCase();
                    String val = arg.substring(splitIdx + 1);
                    parsedOptions.put(key, val);
                } else {
                    String key = arg.toLowerCase();
                    if (valueFlags.contains(key) && i + 1 < args.length && !args[i + 1].startsWith("-")) {
                        parsedOptions.put(key, args[i + 1]);
                        i++;
                    } else {
                        parsedOptions.put(key, "true");
                    }
                }
            } else {
                positional.add(arg);
                if (!joinedPositional.isEmpty()) joinedPositional.append(" ");
                joinedPositional.append(arg);
            }
        }

        return new CLIContext(parsedOptions, positional, joinedPositional.toString());
    }

    public void printGlobalHelp() {
        System.out.println("Usage: [command] [options] [arguments]");
        System.out.println();
        System.out.println("Available Commands:");
        for (FastCLICommand cmd : new LinkedHashSet<>(commandMap.values())) {
            System.out.printf("  %-16s %s%n", cmd.name(), cmd.description());
        }
    }
}
