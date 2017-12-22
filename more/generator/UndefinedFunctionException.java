package generator;

import lexer.Symbol;

/**
 * Thrown when the imp program call an undefined function.
 */
public class UndefinedFunctionException extends Exception {

    private final Symbol token;

    public UndefinedFunctionException() {
        super();
        this.token = null;
    }
    public UndefinedFunctionException(Symbol token) {
        super("Call to undefined function " + (String) token.getValue());
        this.token = token;
    }
    public UndefinedFunctionException(Symbol token, Throwable cause) {
        super("Call to undefined function " + (String) token.getValue(), cause);
        this.token = token;
    }
    public UndefinedFunctionException(Throwable cause) {
        super(cause);
        this.token = null;
    }
}
