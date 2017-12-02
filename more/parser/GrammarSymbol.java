package parser;

import lexer.Symbol;

/**
* Imp grammar symbols (variables or terminals)
*
* @author Antoine Passemiers
* @author Alexis Reynouard
*/
public class GrammarSymbol implements Comparable<GrammarSymbol> {
    
    private final boolean isTerminalSymbol;
    private final String value;

    /** Epsilon grammar symbol */
    public static final GrammarSymbol EPSILON = new GrammarSymbol("epsilon");
    /** End of program grammar symbol */
    public static final GrammarSymbol EOS = new GrammarSymbol("$");

    /**
     * Constructor
     *
     * If the value begin with '<', it is not a terminal
     */
    public GrammarSymbol(String value) {
        this.isTerminalSymbol = !((value.charAt(0) == '<') && (value.length() > 2));
        this.value = value;
    }

    /** Is this symbol a terminal ? */
    public boolean isTerminal() {
        return isTerminalSymbol;
    }

    /**
     * return the grammar symbol string representation with the suffix appended
     *
     * e.g.:
     *   <value>.suffix("suff") → <value-suff>
     *   value.suffix("suff") → value-suff
     */
    public String nameVariant(String suffix) {
        if (this.isTerminal()) {
            return this.value + "-" + suffix;
        }
        else {
            assert(this.value.charAt(this.value.length() - 1) == '>');
            return this.value.substring(0, this.value.length() - 1) + "-" + suffix + ">";
        }
    }

    /**
     * return the grammar symbol string without "<" and ">"
     *
     * e.g.:
     *   <value> → value
     *   value → value
     */
    public String withoutChevrons() {
        if (this.isTerminal()) {
            return this.value;
        } else {
            return this.value.substring(1, this.value.length() - 1);
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

    @Override
    public int compareTo(GrammarSymbol other) {
        if (this.value.length() < other.value.length()) {
            return -1;
        }
        if (this.value.length() > other.value.length()) {
            return 1;
        }
        return this.value.compareTo(other.value);
    }
}
