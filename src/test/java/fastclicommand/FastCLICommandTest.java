package fastclicommand;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class FastCLICommandTest {

    @Test
    public void testParsingFlagsAndOptions() {
        FastCLIPublicParser parser = new FastCLIPublicParser();
        List<OptionSpec> specs = List.of(
                new OptionSpec("voice", "v", "Voice name", "default"),
                new OptionSpec("rate", "r", "Rate value", "1.0"),
                new OptionSpec("list", "l", "List items")
        );

        String[] args = new String[] {
                "-v", "thorsten",
                "--rate:1.5",
                "-l",
                "Hello", "World"
        };

        CLIContext ctx = parser.parseArguments(args, specs);
        assertTrue(ctx.has("-l"));
        assertTrue(ctx.has("--list"));
        assertEquals("thorsten", ctx.get("-v"));
        assertEquals("thorsten", ctx.get("--voice"));
        assertEquals(1.5, ctx.getDouble("--rate", 1.0));
        assertEquals(1.5, ctx.getDouble("-r", 1.0));
        assertEquals("Hello World", ctx.joinedArgs());
        assertEquals(2, ctx.argCount());
        assertEquals("Hello", ctx.arg(0));
        assertEquals("World", ctx.arg(1));
    }

    @Test
    public void testNegativeNumbersAndDashDash() {
        FastCLIPublicParser parser = new FastCLIPublicParser();
        List<OptionSpec> specs = List.of(
                new OptionSpec("rate", "r", "Rate", "-1.0"),
                new OptionSpec("verbose", "v", "Verbose")
        );

        String[] args = new String[] {
                "--rate", "-5.5",
                "-v",
                "--",
                "-dashText", "Normal"
        };

        CLIContext ctx = parser.parseArguments(args, specs);
        assertEquals(-5.5, ctx.getDouble("--rate", 0.0));
        assertTrue(ctx.has("-v"));
        assertEquals(2, ctx.argCount());
        assertEquals("-dashText", ctx.arg(0));
        assertEquals("Normal", ctx.arg(1));
    }

    @Test
    public void testDispatchCommandAndAliases() {
        FastCLIRouter router = new FastCLIRouter();
        final boolean[] executed = new boolean[1];

        FastCLICommand cmd = new FastCLICommand() {
            @Override
            public String name() { return "serve"; }

            @Override
            public String description() { return "Starts server"; }

            @Override
            public List<String> aliases() { return List.of("s", "run"); }

            @Override
            public List<OptionSpec> options() {
                return List.of(new OptionSpec("port", "p", "Port", "8080"));
            }

            @Override
            public int execute(CLIContext context) {
                executed[0] = true;
                assertEquals(3000, context.getInt("--port", 8080));
                assertEquals(3000, context.getInt("-p", 8080));
                return 0;
            }
        };

        router.register(cmd);
        int code = router.dispatch(new String[] { "s", "-p", "3000" });
        assertEquals(0, code);
        assertTrue(executed[0]);
    }

    @Test
    public void testStrictExceptionHandling() {
        FastCLIParser parser = new FastCLIParser(true);
        CompiledCommand compiled = new CompiledCommand(new FastCLICommand() {
            @Override
            public String name() { return "test"; }

            @Override
            public String description() { return "Test"; }

            @Override
            public int execute(CLIContext context) { return 0; }
        });

        assertThrows(CLIParseException.class, () -> {
            parser.parse(new String[] { "--unknown" }, 0, 1, compiled);
        });
    }
}
