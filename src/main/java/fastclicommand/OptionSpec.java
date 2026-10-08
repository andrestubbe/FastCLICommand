package fastclicommand;

import java.util.Collections;
import java.util.List;

/**
 * Metadata and specification for a command option/flag.
 */
public record OptionSpec(
        String name,
        String shortName,
        String description,
        boolean requiresValue,
        String defaultValue
) {
    public OptionSpec(String name, String shortName, String description) {
        this(name, shortName, description, false, null);
    }

    public OptionSpec(String name, String shortName, String description, String defaultValue) {
        this(name, shortName, description, true, defaultValue);
    }
}
