package fastclicommand;

import java.util.*;

/**
 * High-speed, allocation-conscious argument tokenizer and parser.
 * Operates on array slices without intermediate array copying.
 */
public final class FastCLIParser {

    private final boolean strict;

    public FastCLIParser() {
        this(false);
    }

    public FastCLIParser(boolean strict) {
        this.strict = strict;
    }

    public CLIContext parse(String[] args, int from, int to, CompiledCommand compiled) {
        if (args == null || from >= to) {
            return emptyContext(compiled);
        }

        int totalArgs = to - from;
        int expectedOptions = (compiled != null && !compiled.declaredOptions().isEmpty())
                ? Math.max(compiled.declaredOptions().size(), totalArgs)
                : Math.max(totalArgs, 4);
        Map<String, String> parsedOptions = new HashMap<>(expectedOptions * 4 / 3 + 1);
        int[] positionalIndices = new int[totalArgs];
        int positionalCount = 0;
        boolean parsingOptions = true;

        for (int i = from; i < to; i++) {
            String arg = args[i];
            if (arg == null) continue;

            if (parsingOptions && arg.equals("--")) {
                parsingOptions = false;
                continue;
            }

            if (parsingOptions && isFlagToken(arg)) {
                int colonIdx = arg.indexOf(':');
                int equalIdx = arg.indexOf('=');
                int splitIdx = -1;
                if (colonIdx > 0 && equalIdx > 0) splitIdx = Math.min(colonIdx, equalIdx);
                else if (colonIdx > 0) splitIdx = colonIdx;
                else if (equalIdx > 0) splitIdx = equalIdx;

                if (splitIdx > 0) {
                    String rawKey = arg.substring(0, splitIdx).toLowerCase(Locale.ROOT);
                    String val = arg.substring(splitIdx + 1);
                    recordOption(rawKey, val, compiled, parsedOptions);
                } else {
                    String rawKey = arg.toLowerCase(Locale.ROOT);
                    OptionSpec spec = (compiled != null) ? compiled.findOption(rawKey) : null;

                    if (spec != null && spec.requiresValue()) {
                        if (i + 1 < to && !args[i + 1].equals("--") && !isFlagToken(args[i + 1])) {
                            recordOption(rawKey, args[i + 1], compiled, parsedOptions);
                            i++;
                        } else {
                            throw new CLIParseException(rawKey, "Option requires a value but none was provided");
                        }
                    } else {
                        recordOption(rawKey, "true", compiled, parsedOptions);
                    }
                }
            } else {
                positionalIndices[positionalCount++] = i;
            }
        }

        // Apply defaults from compiled specifications
        if (compiled != null) {
            for (OptionSpec spec : compiled.declaredOptions()) {
                if (spec.defaultValue() != null && spec.longKey() != null) {
                    parsedOptions.putIfAbsent(spec.longKey(), spec.defaultValue());
                }
            }
        }

        return new CLIContext(parsedOptions, args, positionalIndices, positionalCount);
    }

    private void recordOption(String rawKey, String val, CompiledCommand compiled, Map<String, String> out) {
        OptionSpec spec = (compiled != null) ? compiled.findOption(rawKey) : null;
        if (spec != null) {
            if (spec.longKey() != null) out.put(spec.longKey(), val);
            if (spec.shortKey() != null) out.put(spec.shortKey(), val);
        } else {
            if (strict) {
                throw new CLIParseException(rawKey, "Unrecognized option");
            }
            out.put(rawKey, val);
        }
    }

    private static boolean isFlagToken(String arg) {
        if (!arg.startsWith("-") || arg.length() < 2) return false;
        // Negative numbers like -1 or -5.5 should be positional unless matched as known flag
        char c = arg.charAt(1);
        return !(c >= '0' && c <= '9');
    }

    private static CLIContext emptyContext(CompiledCommand compiled) {
        Map<String, String> defs = Collections.emptyMap();
        if (compiled != null && !compiled.declaredOptions().isEmpty()) {
            defs = new HashMap<>();
            for (OptionSpec spec : compiled.declaredOptions()) {
                if (spec.defaultValue() != null && spec.longKey() != null) {
                    defs.put(spec.longKey(), spec.defaultValue());
                }
            }
        }
        return new CLIContext(defs, new String[0], new int[0], 0);
    }
}
