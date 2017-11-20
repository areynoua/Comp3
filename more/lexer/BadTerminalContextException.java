// Example of use: if end of comment occurs outside of any comment
public class BadTerminalContextException extends Exception {
	public BadTerminalContextException() { super(); }
	public BadTerminalContextException(String message) { super(message); }
	public BadTerminalContextException(String message, Throwable cause) { super(message, cause); }
	public BadTerminalContextException(Throwable cause) { super(cause); }
}
