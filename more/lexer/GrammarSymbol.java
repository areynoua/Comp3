/**
* Imp grammar symbols (variables or terminals)
*
* @author Antoine Passemiers
* @author Alexis Reynouard
*/

import java.util.List;

public class GrammarSymbol {
    
    private final boolean isTerminal;
    private final String variableName;
    private final LexicalUnit lexicalUnit;

    public GrammarSymbol(String variableName) {
        // The symbol is a variable
        this.isTerminal = false;
        this.variableName = variableName;
        this.lexicalUnit = null;
    }

    public GrammarSymbol(LexicalUnit lexicalUnit) {
        // The symbol is a terminal
        this.isTerminal = true;
        this.lexicalUnit = lexicalUnit;
        this.variableName = null;
    }

    public boolean equals(GrammarSymbol other) {
        if (isTerminal == other.isTerminal) {
            return this.lexicalUnit == other.lexicalUnit;
        } else {
            return this.variableName.equals(other.variableName);
        }
    }

    @Override
    public int hashCode() {
        int hash;
        if (isTerminal) hash = lexicalUnit.hashCode();
        else hash = variableName.hashCode();
        return hash;
    }
}