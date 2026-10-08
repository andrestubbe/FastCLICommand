package fastclicommand;

import java.util.Locale;
import java.util.Objects;

/**
 * Metadata and specification for a command option/flag.
 * Automatically normalizes long and short keys to standard GNU convention (--key, -k).
 */
public record OptionSpec(
        String name,
        String shortName,
        String description,
        boolean requiresValue,
        String defaultValue,
        String longKey,
        String shortKey
) {
    public OptionSpec(String name, String shortName, String description) {
        this(name, shortName, description, false, null);
    }

    public OptionSpec(String name, String shortName, String description, boolean requiresValue) {
        this(name, shortName, description, requiresValue, null);
    }

    public OptionSpec(String name, String shortName, String description, String defaultValue) {
        this(name, shortName, description, true, defaultValue);
    }

    public OptionSpec(String name, String shortName, String description, boolean requiresValue, String defaultValue) {
        this(
                name,
                shortName,
                description,
                requiresValue,
                defaultValue,
                normalizeLong(name),
                normalizeShort(shortName)
        );
    }

    private static String normalizeLong(String n) {
        if (n == null || n.isBlank()) return null;
        String trimmed = n.trim().toLowerCase(Locale.ROOT);
        return trimmed.startsWith("--") ? trimmed : ("--" + (trimmed.startsWith("-") ? trimmed.substring(1) : trimmed));
    }

    private static String normalizeShort(String s) {
        if (s == null || s.isBlank()) return null;
        String trimmed = s.trim().toLowerCase(Locale.ROOT);
        return trimmed.startsWith("-") ? trimmed : ("-" + trimmed);
    }
}
