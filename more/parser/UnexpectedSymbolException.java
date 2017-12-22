package parser;

/**
 * Thrown when the parser read an incoherent token (Action table = Error).
 */
public class UnexpectedSymbolException extends Exception {
    public UnexpectedSymbolException() {
        super();
    }
    public UnexpectedSymbolException(GrammarSymbol tos, GrammarSymbol symbol) {
        super("Unexpected symbol: " + symbol.toString() + " with TOS: " + tos.toString());
    }
    public UnexpectedSymbolException(GrammarSymbol tos, GrammarSymbol symbol, Throwable cause) {
        super("Unexpected symbol: " + symbol.toString() + " with TOS: " + tos.toString(), cause);
    }
    public UnexpectedSymbolException(Throwable cause) {
        super(cause);
    }
}
