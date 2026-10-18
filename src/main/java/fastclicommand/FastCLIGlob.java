package fastclicommand;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.*;
import java.util.*;

/**
 * FastCLIGlob - Ultra-fast glob pattern expansion for CLI arguments.
 * <p>
 * On Windows shells (cmd.exe, PowerShell), wildcard patterns (*, ?, **) in CLI
 * arguments are not expanded by the shell prior to process launch.
 * FastCLIGlob resolves this by expanding wildcards either via native FastGLOB (JNI)
 * if present on the classpath, or falling back gracefully to high-performance
 * Java NIO DirectoryStreams / PathMatchers.
 */
public final class FastCLIGlob {

    private static final boolean NATIVE_FASTGLOB_AVAILABLE;
    private static Method fastGlobMethod;

    static {
        boolean available = false;
        try {
            Class<?> clazz = Class.forName("fastglob.FastGLOB");
            fastGlobMethod = clazz.getMethod("glob", String.class, String.class);
            available = true;
        } catch (Throwable ignored) {
            fastGlobMethod = null;
        }
        NATIVE_FASTGLOB_AVAILABLE = available;
    }

    private FastCLIGlob() {}

    /**
     * Checks if native FastGLOB acceleration is available in current runtime.
     */
    public static boolean isNativeAvailable() {
        return NATIVE_FASTGLOB_AVAILABLE;
    }

    /**
     * Tests whether a string contains glob wildcard characters (*, ?, [, {).
     */
    public static boolean isGlob(String pattern) {
        if (pattern == null || pattern.isEmpty()) return false;
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            if (c == '*' || c == '?' || c == '[' || c == '{') {
                return true;
            }
        }
        return false;
    }

    /**
     * Expands an array of arguments, replacing any glob patterns with their matching file paths.
     * Arguments that do not contain wildcards, or glob patterns that match no files, are preserved.
     *
     * @param args Array of raw command line tokens.
     * @return Newly expanded array of arguments.
     */
    public static String[] expand(String[] args) {
        if (args == null || args.length == 0) return args;

        boolean hasAnyGlob = false;
        for (String a : args) {
            if (isGlob(a)) {
                hasAnyGlob = true;
                break;
            }
        }
        if (!hasAnyGlob) return args;

        List<String> result = new ArrayList<>(args.length + 8);
        for (String a : args) {
            if (isGlob(a)) {
                List<String> matched = resolve(a);
                if (matched.isEmpty()) {
                    result.add(a); // Keep verbatim if no matches
                } else {
                    result.addAll(matched);
                }
            } else {
                result.add(a);
            }
        }
        return result.toArray(new String[0]);
    }

    /**
     * Expands a single glob pattern into a list of absolute or relative matching paths.
     *
     * @param pattern Glob expression (e.g. "*.txt", "src/** /*.java", "models/*.onnx").
     * @return List of matching file path strings sorted in natural order.
     */
    public static List<String> resolve(String pattern) {
        if (pattern == null || pattern.isBlank()) return Collections.emptyList();

        // 1. Try native FastGLOB if loaded
        if (NATIVE_FASTGLOB_AVAILABLE && fastGlobMethod != null) {
            try {
                String baseDir = ".";
                String relativePattern = pattern;
                int lastSlash = Math.max(pattern.lastIndexOf('/'), pattern.lastIndexOf('\\'));
                if (lastSlash != -1) {
                    baseDir = pattern.substring(0, lastSlash);
                    relativePattern = pattern.substring(lastSlash + 1);
                }
                String[] res = (String[]) fastGlobMethod.invoke(null, baseDir, relativePattern);
                if (res != null && res.length > 0) {
                    List<String> out = new ArrayList<>(res.length);
                    for (String r : res) {
                        out.add(baseDir.equals(".") ? r : (baseDir + File.separator + r));
                    }
                    Collections.sort(out);
                    return out;
                }
            } catch (Throwable ignored) {
                // Fall back to Java NIO
            }
        }

        // 2. High-performance Java NIO Fallback
        return resolveNio(pattern);
    }

    private static List<String> resolveNio(String pattern) {
        String normalized = pattern.replace('\\', '/');
        int firstWildcard = -1;
        for (int i = 0; i < normalized.length(); i++) {
            char c = normalized.charAt(i);
            if (c == '*' || c == '?' || c == '[' || c == '{') {
                firstWildcard = i;
                break;
            }
        }

        if (firstWildcard == -1) {
            Path p = Paths.get(pattern);
            return Files.exists(p) ? List.of(pattern) : Collections.emptyList();
        }

        int lastSepBeforeWildcard = normalized.lastIndexOf('/', firstWildcard);
        Path rootDir;
        String subPattern;

        if (lastSepBeforeWildcard == -1) {
            rootDir = Paths.get(".");
            subPattern = normalized;
        } else {
            String dirPart = normalized.substring(0, lastSepBeforeWildcard);
            rootDir = dirPart.isEmpty() ? Paths.get("/") : Paths.get(dirPart);
            subPattern = normalized.substring(lastSepBeforeWildcard + 1);
        }

        if (!Files.isDirectory(rootDir)) {
            return Collections.emptyList();
        }

        List<String> matches = new ArrayList<>();
        boolean isRecursive = subPattern.contains("**");
        int maxDepth = isRecursive ? 32 : 1;

        FileSystem fs = FileSystems.getDefault();
        PathMatcher matcher = fs.getPathMatcher("glob:" + subPattern);

        try (var stream = Files.walk(rootDir, maxDepth)) {
            stream.filter(p -> !p.equals(rootDir)).forEach(p -> {
                Path rel = rootDir.relativize(p);
                String relUnix = rel.toString().replace('\\', '/');
                if (matcher.matches(Paths.get(relUnix))) {
                    matches.add(p.toString());
                }
            });
        } catch (IOException ignored) {}

        Collections.sort(matches);
        return matches;
    }
}
