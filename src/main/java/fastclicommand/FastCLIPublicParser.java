package fastclicommand;

import java.util.List;

/**
 * Backward-compatible facade delegating to FastCLIRouter and FastCLIParser.
 */
public final class FastCLIPublicParser {

    private final FastCLIRouter router;
    private final FastCLIParser parser;

    public FastCLIPublicParser() {
        this.parser = new FastCLIParser(false);
        this.router = new FastCLIRouter(parser);
    }

    public FastCLIPublicParser register(FastCLICommand cmd) {
        router.register(cmd);
        return this;
    }

    public FastCLIPublicParser setDefault(FastCLICommand cmd) {
        router.setDefault(cmd);
        return this;
    }

    public int dispatch(String[] args) {
        return router.dispatch(args);
    }

    public CLIContext parseArguments(String[] args, List<OptionSpec> knownOptions) {
        FastCLICommand dummy = new FastCLICommand() {
            @Override
            public String name() { return "default"; }

            @Override
            public String description() { return "Default"; }

            @Override
            public List<OptionSpec> options() { return knownOptions; }

            @Override
            public int execute(CLIContext context) { return 0; }
        };
        CompiledCommand compiled = new CompiledCommand(dummy);
        int len = args != null ? args.length : 0;
        return parser.parse(args, 0, len, compiled);
    }

    public void printGlobalHelp() {
        router.printGlobalHelp();
    }
}
