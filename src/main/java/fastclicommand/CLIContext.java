package fastclicommand;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Parsed command line context providing zero-overhead, type-safe getters for options and positional arguments.
 */
public final class CLIContext {

    private final Map<String, String> options;
    private final List<String> positionalArgs;
    private final String rawJoinedText;

    public CLIContext(Map<String, String> options, List<String> positionalArgs, String rawJoinedText) {
        this.options = options != null ? options : Collections.emptyMap();
        this.positionalArgs = positionalArgs != null ? positionalArgs : Collections.emptyList();
        this.rawJoinedText = rawJoinedText != null ? rawJoinedText : "";
    }

    public boolean has(String flag) {
        return options.containsKey(flag.toLowerCase());
    }

    public boolean has(OptionSpec spec) {
        if (spec == null) return false;
        if (spec.name() != null && has(spec.name())) return true;
        return spec.shortName() != null && has(spec.shortName());
    }

    public String get(String flag) {
        return options.get(flag.toLowerCase());
    }

    public String get(String flag, String defaultValue) {
        return options.getOrDefault(flag.toLowerCase(), defaultValue);
    }

    public String get(OptionSpec spec) {
        if (spec == null) return null;
        String val = null;
        if (spec.name() != null) val = get(spec.name());
        if (val == null && spec.shortName() != null) val = get(spec.shortName());
        return (val != null) ? val : spec.defaultValue();
    }

    public double getDouble(OptionSpec spec, double defaultFallback) {
        String val = get(spec);
        if (val == null) return defaultFallback;
        try {
            return Double.parseDouble(val.trim().replace(",", "."));
        } catch (Exception e) {
            return defaultFallback;
        }
    }

    public int getInt(String flag, int defaultValue) {
        String val = get(flag);
        if (val == null) return defaultValue;
        try {
            return Integer.parseInt(val.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public double getDouble(String flag, double defaultValue) {
        String val = get(flag);
        if (val == null) return defaultValue;
        try {
            return Double.parseDouble(val.trim().replace(",", "."));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public boolean getBoolean(String flag, boolean defaultValue) {
        String val = get(flag);
        if (val == null) return defaultValue;
        return "true".equalsIgnoreCase(val) || "1".equals(val) || "yes".equalsIgnoreCase(val);
    }

    public List<String> args() {
        return positionalArgs;
    }

    public String arg(int index) {
        return (index >= 0 && index < positionalArgs.size()) ? positionalArgs.get(index) : null;
    }

    public String arg(int index, String defaultValue) {
        String a = arg(index);
        return (a != null) ? a : defaultValue;
    }

    public String joinedArgs() {
        return rawJoinedText;
    }
}
