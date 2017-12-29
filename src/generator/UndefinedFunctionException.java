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
        super("" + (String) token.getValue() + " is not a function name");
        this.token = token;
    }
    public UndefinedFunctionException(Symbol token, Throwable cause) {
        super("" + (String) token.getValue() + " is not a function name", cause);
        this.token = token;
    }
    public UndefinedFunctionException(Throwable cause) {
        super(cause);
        this.token = null;
    }
}
