package parser;
/**
 * Thrown when the lexer read an incoherent token.
 * This can occur, for example with an "end of comment" terminal outside of any comment.
 */
public class UnexpectedEndOfFileException extends Exception {
    public UnexpectedEndOfFileException() { super("Unexpected End Of File"); }
    public UnexpectedEndOfFileException(String message) { super(message); }
    public UnexpectedEndOfFileException(String message, Throwable cause) { super(message, cause); }
    public UnexpectedEndOfFileException(Throwable cause) { super("Unexpected End Of File", cause); }
}
