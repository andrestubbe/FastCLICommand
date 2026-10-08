package fastclicommand;

/**
 * Thrown when command-line parsing or argument conversion fails.
 */
public class CLIParseException extends RuntimeException {

    private final String optionName;

    public CLIParseException(String message) {
        super(message);
        this.optionName = null;
    }

    public CLIParseException(String optionName, String message) {
        super(optionName != null ? "[" + optionName + "] " + message : message);
        this.optionName = optionName;
    }

    public CLIParseException(String optionName, String message, Throwable cause) {
        super(optionName != null ? "[" + optionName + "] " + message : message, cause);
        this.optionName = optionName;
    }

    public String getOptionName() {
        return optionName;
    }
}
