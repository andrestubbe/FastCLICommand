package fastclicommand.benchmark;

import fastclicommand.CLIContext;
import fastclicommand.FastCLICommand;
import fastclicommand.FastCLIPublicParser;
import fastclicommand.OptionSpec;
import org.openjdk.jmh.annotations.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Standard OpenJDK JMH Microbenchmark Suite for FastCLICommand.
 *
 * Measures throughput of command parsing, option retrieval, and dispatch routing.
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@Fork(1)
public class Benchmark {

    private FastCLIPublicParser router;
    private String[] dispatchArgs;
    private String[] parseOnlyArgs;
    private List<OptionSpec> knownOptions;

    @Setup
    public void setup() {
        router = new FastCLIPublicParser();
        router.register(new FastCLICommand() {
            @Override
            public String name() { return "bench"; }

            @Override
            public String description() { return "Benchmark target"; }

            @Override
            public List<OptionSpec> options() {
                return List.of(
                        new OptionSpec("threads", "t", "Worker threads", "4"),
                        new OptionSpec("verbose", "v", "Verbose flag")
                );
            }

            @Override
            public int execute(CLIContext context) {
                return context.getInt("--threads", 1);
            }
        });

        dispatchArgs = new String[]{"bench", "--threads=8", "-v", "input.bin"};
        parseOnlyArgs = new String[]{"--threads=8", "-v", "input.bin"};
        knownOptions = List.of(
                new OptionSpec("threads", "t", "Worker threads", "4"),
                new OptionSpec("verbose", "v", "Verbose flag")
        );
    }

    @org.openjdk.jmh.annotations.Benchmark
    public int benchmarkDispatch() throws Exception {
        return router.dispatch(dispatchArgs);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public CLIContext benchmarkParseArgumentsOnly() {
        return router.parseArguments(parseOnlyArgs, knownOptions);
    }
}
