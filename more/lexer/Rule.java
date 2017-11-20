/**
* Imp grammar rules
*
* @author Antoine Passemiers
* @author Alexis Reynouard
*/

import java.util.List;

public class Rule {
    
    private GrammarSymbol leftVariable;
    private List<GrammarSymbol> rightSymbols; // Symbols can be either variables or terminals

    public Rule(GrammarSymbol leftVariable, List<GrammarSymbol> rightSymbols) {
        this.leftVariable = leftVariable;
        this.rightSymbols = rightSymbols;
    }
    
}