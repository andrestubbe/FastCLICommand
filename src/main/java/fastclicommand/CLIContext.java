package fastclicommand;

import java.util.*;

/**
 * Immutable and allocation-efficient context representing parsed CLI state.
 * Uses string array slice indices to defer and avoid intermediate allocations.
 */
public final class CLIContext {

    private final Map<String, String> canonicalOptions;
    private final String[] rawArgs;
    private final int[] positionalIndices;
    private final int positionalCount;
    private String lazyJoinedArgs;

    public CLIContext(Map<String, String> canonicalOptions, String[] rawArgs, int[] positionalIndices, int positionalCount) {
        this.canonicalOptions = canonicalOptions != null ? Collections.unmodifiableMap(canonicalOptions) : Collections.emptyMap();
        this.rawArgs = rawArgs;
        this.positionalIndices = positionalIndices;
        this.positionalCount = positionalCount;
    }

    public boolean has(String flag) {
        if (flag == null) return false;
        String normalized = normalizeLookupKey(flag);
        String val = canonicalOptions.get(normalized);
        if (val == null) return false;
        return !isFalseString(val);
    }

    public boolean has(OptionSpec spec) {
        if (spec == null) return false;
        String val = null;
        if (spec.longKey() != null) val = canonicalOptions.get(spec.longKey());
        if (val == null && spec.shortKey() != null) val = canonicalOptions.get(spec.shortKey());
        if (val == null) return false;
        return !isFalseString(val);
    }

    public boolean contains(String flag) {
        if (flag == null) return false;
        return canonicalOptions.containsKey(normalizeLookupKey(flag));
    }

    public boolean contains(OptionSpec spec) {
        if (spec == null) return false;
        if (spec.longKey() != null && canonicalOptions.containsKey(spec.longKey())) return true;
        return spec.shortKey() != null && canonicalOptions.containsKey(spec.shortKey());
    }

    public String get(String flag) {
        if (flag == null) return null;
        String normalized = normalizeLookupKey(flag);
        return canonicalOptions.get(normalized);
    }

    public String get(String flag, String defaultValue) {
        String val = get(flag);
        return val != null ? val : defaultValue;
    }

    public String get(OptionSpec spec) {
        if (spec == null) return null;
        String val = null;
        if (spec.longKey() != null) val = canonicalOptions.get(spec.longKey());
        if (val == null && spec.shortKey() != null) val = canonicalOptions.get(spec.shortKey());
        return val != null ? val : spec.defaultValue();
    }

    public int getInt(String flag) {
        String val = get(flag);
        if (val == null) throw new CLIParseException(flag, "Missing required option");
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            throw new CLIParseException(flag, "Invalid integer format: '" + val + "'", e);
        }
    }

    public int getInt(String flag, int defaultValue) {
        String val = get(flag);
        if (val == null) return defaultValue;
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public int getInt(OptionSpec spec, int defaultValue) {
        String val = get(spec);
        if (val == null) return defaultValue;
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public double getDouble(String flag) {
        String val = get(flag);
        if (val == null) throw new CLIParseException(flag, "Missing required option");
        try {
            return Double.parseDouble(val.trim());
        } catch (NumberFormatException e) {
            throw new CLIParseException(flag, "Invalid floating-point number: '" + val + "'", e);
        }
    }

    public double getDouble(String flag, double defaultValue) {
        String val = get(flag);
        if (val == null) return defaultValue;
        try {
            return Double.parseDouble(val.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public double getDouble(OptionSpec spec, double defaultValue) {
        String val = get(spec);
        if (val == null) return defaultValue;
        try {
            return Double.parseDouble(val.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public boolean getBoolean(String flag, boolean defaultValue) {
        String val = get(flag);
        if (val == null) return defaultValue;
        if (isFalseString(val)) return false;
        return "true".equalsIgnoreCase(val) || "1".equals(val) || "yes".equalsIgnoreCase(val);
    }

    public boolean getBoolean(OptionSpec spec, boolean defaultValue) {
        String val = get(spec);
        if (val == null) return defaultValue;
        if (isFalseString(val)) return false;
        return "true".equalsIgnoreCase(val) || "1".equals(val) || "yes".equalsIgnoreCase(val);
    }

    private static boolean isFalseString(String val) {
        if (val == null) return false;
        String trimmed = val.trim();
        return "false".equalsIgnoreCase(trimmed) || "0".equals(trimmed) || "no".equalsIgnoreCase(trimmed) || "off".equalsIgnoreCase(trimmed);
    }

    public int argCount() {
        return positionalCount;
    }

    public String arg(int index) {
        if (index < 0 || index >= positionalCount) return null;
        return rawArgs[positionalIndices[index]];
    }

    public String arg(int index, String defaultValue) {
        String a = arg(index);
        return a != null ? a : defaultValue;
    }

    public List<String> args() {
        if (positionalCount == 0) return Collections.emptyList();
        List<String> list = new ArrayList<>(positionalCount);
        for (int i = 0; i < positionalCount; i++) {
            list.add(rawArgs[positionalIndices[i]]);
        }
        return Collections.unmodifiableList(list);
    }

    public String joinedArgs() {
        if (lazyJoinedArgs != null) return lazyJoinedArgs;
        if (positionalCount == 0) {
            lazyJoinedArgs = "";
            return lazyJoinedArgs;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < positionalCount; i++) {
            if (i > 0) sb.append(' ');
            sb.append(rawArgs[positionalIndices[i]]);
        }
        lazyJoinedArgs = sb.toString();
        return lazyJoinedArgs;
    }

    private static String normalizeLookupKey(String k) {
        String trimmed = k.trim().toLowerCase(Locale.ROOT);
        if (trimmed.startsWith("-")) return trimmed;
        return trimmed.length() == 1 ? ("-" + trimmed) : ("--" + trimmed);
    }
}
