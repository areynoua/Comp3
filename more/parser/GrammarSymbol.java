package parser;
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
    public static final GrammarSymbol EOS = new GrammarSymbol("$");

    public GrammarSymbol(String value) {
        this.isTerminalSymbol = !((value.charAt(0) == '<') && (value.length() > 2));
        this.value = value;
    }

    public boolean isTerminal() {
        return isTerminalSymbol;
    }

    public String nameVariant(String suffix) {
        if (this.isTerminal()) {
            return this.value + "-" + suffix;
        }
        else {
            assert(this.value.charAt(this.value.length() - 1) == '>');
            return this.value.substring(0, this.value.length() - 1) + "-" + suffix + ">";
        }
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
