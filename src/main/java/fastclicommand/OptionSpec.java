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
        boolean allowsOptionalValue,
        String defaultValue,
        String longKey,
        String shortKey
) {
    public OptionSpec {
        longKey = normalizeLong(name != null ? name : longKey);
        shortKey = normalizeShort(shortName != null ? shortName : shortKey);
    }

    public OptionSpec(String name, String shortName, String description) {
        this(name, shortName, description, false, false, null, null, null);
    }

    public OptionSpec(String name, String shortName, String description, boolean requiresValue) {
        this(name, shortName, description, requiresValue, false, null, null, null);
    }

    public OptionSpec(String name, String shortName, String description, String defaultValue) {
        this(name, shortName, description, true, false, defaultValue, null, null);
    }

    public OptionSpec(String name, String shortName, String description, boolean requiresValue, String defaultValue) {
        this(name, shortName, description, requiresValue, false, defaultValue, null, null);
    }

    public static OptionSpec optionalValue(String name, String shortName, String description) {
        return new OptionSpec(name, shortName, description, false, true, null, null, null);
    }

    public static OptionSpec optionalValue(String name, String shortName, String description, String defaultPresenceValue) {
        return new OptionSpec(name, shortName, description, false, true, defaultPresenceValue, null, null);
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
