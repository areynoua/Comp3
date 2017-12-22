package generator;
/**
 * Thrown when the imp program call an undefined function.
 */
public class UndefinedFunctionException extends Exception {
    public UndefinedFunctionException() {
        super();
    }
    public UndefinedFunctionException(String functionName) {
        super(messageFromFunctionName(functionName));
    }
    public UndefinedFunctionException(String functionName, Throwable cause) {
        super(messageFromFunctionName(functionName), cause);
    }
    public UndefinedFunctionException(Throwable cause) {
        super(cause);
    }
    private messageFromFunctionName(String functionName) {
        return "Call to undefined function " + functionName;
    }
}
