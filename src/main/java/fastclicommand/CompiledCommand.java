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
        this(
                Objects.requireNonNull(command, "command"),
                command.options() != null ? command.options() : Collections.emptyList()
        );
    }

    public CompiledCommand(String name, String description, List<OptionSpec> declaredOptions) {
        this(
                new SimpleFastCLICommand(name, description, declaredOptions),
                declaredOptions != null ? declaredOptions : Collections.emptyList()
        );
    }

    private CompiledCommand(FastCLICommand command, List<OptionSpec> declaredOptions) {
        this.command = command;
        this.declaredOptions = declaredOptions;

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

    private record SimpleFastCLICommand(String name, String description, List<OptionSpec> options) implements FastCLICommand {
        @Override
        public int execute(CLIContext context) {
            return 0;
        }
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
