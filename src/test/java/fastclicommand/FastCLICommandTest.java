package fastclicommand;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class FastCLICommandTest {

    @Test
    public void testParsingFlagsAndOptions() {
        FastCLIPublicParser parser = new FastCLIPublicParser();
        List<OptionSpec> specs = List.of(
                new OptionSpec("--voice", "-v", "Voice name", "default"),
                new OptionSpec("--rate", "-r", "Rate value", "1.0"),
                new OptionSpec("--list", "-l", "List items")
        );

        String[] args = new String[] {
                "-v", "thorsten",
                "--rate:1.5",
                "-l",
                "Hello", "World"
        };

        CLIContext ctx = parser.parseArguments(args, specs);
        assertTrue(ctx.has("-l"));
        assertEquals("thorsten", ctx.get("-v"));
        assertEquals("1.5", ctx.get("--rate"));
        assertEquals(1.5, ctx.getDouble("--rate", 1.0));
        assertEquals("Hello World", ctx.joinedArgs());
        assertEquals(2, ctx.args().size());
    }

    @Test
    public void testDispatchCommand() throws Exception {
        FastCLIPublicParser parser = new FastCLIPublicParser();
        final boolean[] executed = new boolean[1];

        FastCLICommand cmd = new FastCLICommand() {
            @Override
            public String name() { return "speak"; }

            @Override
            public String description() { return "Speaks text"; }

            @Override
            public int execute(CLIContext context) {
                executed[0] = true;
                return 0;
            }
        };

        parser.register(cmd);
        int code = parser.dispatch(new String[] { "speak", "test" });
        assertEquals(0, code);
        assertTrue(executed[0]);
    }
}
