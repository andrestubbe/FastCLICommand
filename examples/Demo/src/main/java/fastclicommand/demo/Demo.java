package fastclicommand.demo;

import fastclicommand.CLIContext;
import fastclicommand.FastCLICommand;
import fastclicommand.FastCLIPublicParser;
import fastclicommand.OptionSpec;

import java.util.List;

/**
 * Interactive showcase demo for FastCLICommand:
 * Demonstrating zero-allocation command routing, option extraction,
 * alias resolution, and sub-microsecond CLI dispatch.
 */
public class Demo {

    public static void main(String[] args) throws Exception {
        System.out.println("=================================================");
        System.out.println("  FastCLICommand — Interactive Showcase Demo     ");
        System.out.println("=================================================\n");

        // 1. Create Router & Parser
        FastCLIPublicParser router = new FastCLIPublicParser();

        // 2. Register Commands
        router.register(new BuildCommand());
        router.register(new ServeCommand());
        router.register(new StatusCommand());

        // 3. Showcase 1: Global Help
        System.out.println("[1] Global Help Output:");
        System.out.println("-------------------------------------------------");
        router.dispatch(new String[]{"--help"});

        // 4. Showcase 2: Dispatching 'serve' with key-values & flags
        System.out.println("\n[2] Dispatching: serve --port=8080 -v --env production src/main");
        System.out.println("-------------------------------------------------");
        router.dispatch(new String[]{"serve", "--port=8080", "-v", "--env", "production", "src/main"});

        // 5. Showcase 3: Dispatching via alias 'b' for 'build' with colon delimiter
        System.out.println("\n[3] Dispatching via alias 'b' (build): b --release:true app.jar");
        System.out.println("-------------------------------------------------");
        router.dispatch(new String[]{"b", "--release:true", "app.jar"});

        // 6. Micro-Throughput Test
        System.out.println("\n[4] Micro-Throughput Benchmark (1,000,000 command dispatches)...");
        String[] testArgs = new String[]{"status", "--quick", "-v"};
        long start = System.nanoTime();
        int iterations = 1_000_000;
        for (int i = 0; i < iterations; i++) {
            router.dispatch(testArgs);
        }
        long elapsedNanos = System.nanoTime() - start;
        double millis = elapsedNanos / 1_000_000.0;
        double opsPerSec = (iterations / (double) elapsedNanos) * 1_000_000_000.0;

        System.out.printf("  1,000,000 dispatches completed in: %.2f ms (%.0f dispatches/sec)\n", millis, opsPerSec);
        System.out.println("\n✔ FastCLICommand showcase finished successfully!");
    }

    // ── Command Implementations ──────────────────────────────────────────────

    static class BuildCommand implements FastCLICommand {
        @Override
        public String name() { return "build"; }

        @Override
        public String description() { return "Compiles and packages the application binary."; }

        @Override
        public List<String> aliases() { return List.of("b"); }

        @Override
        public List<OptionSpec> options() {
            return List.of(
                    new OptionSpec("release", "r", "Build optimized production release binary"),
                    new OptionSpec("target", "t", "Target architecture output", "x64")
            );
        }

        @Override
        public int execute(CLIContext ctx) {
            boolean isRelease = ctx.getBoolean("--release", false);
            String target = ctx.get("--target", "x64");
            String artifact = ctx.arg(0, "default.jar");

            System.out.println("  [Build] Executing compile job:");
            System.out.printf("          Release: %b | Target: %s | Artifact: %s%n", isRelease, target, artifact);
            return 0;
        }
    }

    static class ServeCommand implements FastCLICommand {
        @Override
        public String name() { return "serve"; }

        @Override
        public String description() { return "Starts high-performance HTTP dev server."; }

        @Override
        public List<String> aliases() { return List.of("s", "run"); }

        @Override
        public List<OptionSpec> options() {
            return List.of(
                    new OptionSpec("port", "p", "TCP port to bind", "8080"),
                    new OptionSpec("env", "e", "Environment mode", "development"),
                    new OptionSpec("verbose", "v", "Enable detailed request logging")
            );
        }

        @Override
        public int execute(CLIContext ctx) {
            int port = ctx.getInt("--port", 8080);
            String env = ctx.get("--env", "development");
            boolean verbose = ctx.has("-v") || ctx.getBoolean("--verbose", false);
            String rootDir = ctx.joinedArgs();

            System.out.println("  [Serve] Starting server instance:");
            System.out.printf("          Port: %d | Environment: %s | Verbose: %b%n", port, env, verbose);
            System.out.printf("          Root directory: %s%n", rootDir.isBlank() ? "." : rootDir);
            return 0;
        }
    }

    static class StatusCommand implements FastCLICommand {
        @Override
        public String name() { return "status"; }

        @Override
        public String description() { return "Queries system and runtime status."; }

        @Override
        public int execute(CLIContext ctx) {
            // Hot-path empty body for benchmarking throughput
            return 0;
        }
    }
}
