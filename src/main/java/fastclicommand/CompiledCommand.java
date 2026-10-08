package fastclicommand;

import java.util.*;

/**
 * Precompiled command metadata and lookup indices constructed once during registration.
 */
public final class CompiledCommand {

    private final FastCLICommand command;
    private final Map<String, OptionSpec> optionsByKey;
    private final List<OptionSpec> declaredOptions;

    public CompiledCommand(FastCLICommand command) {
        this.command = Objects.requireNonNull(command, "command");
        this.declaredOptions = command.options() != null ? command.options() : Collections.emptyList();

        Map<String, OptionSpec> byKey = new HashMap<>(declaredOptions.size() * 2);
        for (OptionSpec opt : declaredOptions) {
            if (opt.longKey() != null) {
                if (byKey.put(opt.longKey(), opt) != null) {
                    throw new IllegalArgumentException("Duplicate option key '" + opt.longKey() + "' in command: " + command.name());
                }
            }
            if (opt.shortKey() != null) {
                if (byKey.put(opt.shortKey(), opt) != null) {
                    throw new IllegalArgumentException("Duplicate option key '" + opt.shortKey() + "' in command: " + command.name());
                }
            }
        }
        this.optionsByKey = Collections.unmodifiableMap(byKey);
    }

    public FastCLICommand command() {
        return command;
    }

    public OptionSpec findOption(String key) {
        return optionsByKey.get(key);
    }

    public Map<String, OptionSpec> optionsByKey() {
        return optionsByKey;
    }

    public List<OptionSpec> declaredOptions() {
        return declaredOptions;
    }
}
