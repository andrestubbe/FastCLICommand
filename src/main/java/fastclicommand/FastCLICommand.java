package fastclicommand;

import java.util.List;

/**
 * Standard contract for executable CLI commands across the FastJava ecosystem.
 */
public interface FastCLICommand {

    /**
     * Unique command name or primary identifier.
     */
    String name();

    /**
     * Brief description for help/catalog output.
     */
    String description();

    /**
     * Optional aliases (e.g. -s for --speak).
     */
    default List<String> aliases() {
        return List.of();
    }

    /**
     * Defined options and flags supported by this command.
     */
    default List<OptionSpec> options() {
        return List.of();
    }

    /**
     * Executes the command with the parsed context.
     *
     * @param context Parsed command flags, key-values, and positional parameters.
     * @return Exit code (0 for success).
     * @throws Exception If an unhandled execution error occurs.
     */
    int execute(CLIContext context) throws Exception;
}
