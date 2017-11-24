/**
* Imp grammar symbols (variables or terminals)
*
* @author Antoine Passemiers
* @author Alexis Reynouard
*/


public class GrammarSymbol {
    
    private final boolean isTerminalSymbol;
    private final String value;

    public static final GrammarSymbol EPSILON = new GrammarSymbol("epsilon");

    public GrammarSymbol(String value) {
        this.isTerminalSymbol = !((value.charAt(0) == '<') && (value.length() > 2));
        this.value = value;
    }

    public boolean isTerminal() {
        return isTerminalSymbol;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GrammarSymbol)) return false;
        else return this.value.equals(((GrammarSymbol) o).value);
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public String toString() {
        return this.value;
    }
}
