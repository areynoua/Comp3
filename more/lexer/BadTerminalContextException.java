package lexer;
/**
 * Thrown when the lexer read an incoherent token.
 * This can occur, for example with an "end of comment" terminal outside of any comment.
 */
public class BadTerminalContextException extends Exception {
    public BadTerminalContextException() { super(); }
    public BadTerminalContextException(String message) { super(message); }
    public BadTerminalContextException(String message, Throwable cause) { super(message, cause); }
    public BadTerminalContextException(Throwable cause) { super(cause); }
}
