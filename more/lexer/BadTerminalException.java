package lexer;
/**
 * Thrown when the lexer read an bad token.
 * This can occur, for example with a non-ASCII character in an identifier
 */
public class BadTerminalException extends Exception {
    public BadTerminalException() { super(); }
    public BadTerminalException(String message) { super(message); }
    public BadTerminalException(String message, Throwable cause) { super(message, cause); }
    public BadTerminalException(Throwable cause) { super(cause); }
}
