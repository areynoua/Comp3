/**
* Imp grammar rules
*
* @author Antoine Passemiers
* @author Alexis Reynouard
*/

import java.lang.String;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Rule {
    
    private GrammarSymbol leftVariable;
    private List<GrammarSymbol> rightSymbols; // Symbols can be either variables or terminals

    public Rule(GrammarSymbol leftVariable, List<GrammarSymbol> rightSymbols) {
        this.leftVariable = leftVariable;
        this.rightSymbols = rightSymbols;
    }

    public Rule(String leftVariable, String[] rightSymbols) {
        this.leftVariable = new GrammarSymbol(leftVariable);
        this.rightSymbols = new ArrayList<>();
        for (String rightSymbol : rightSymbols) {
            if (rightSymbol.length() > 0) {
                this.rightSymbols.add(new GrammarSymbol(rightSymbol));                
            }
        }
    }

    public Rule removePrefix(Integer prefixSize) {
        List<GrammarSymbol> withoutPrefix = rightSymbols.subList(prefixSize, rightSymbols.size());
        return new Rule(leftVariable, withoutPrefix);
    }

    public boolean contains(GrammarSymbol symbol) {
        if (leftVariable.equals(symbol)) return true;
        for (GrammarSymbol rightSymbol : rightSymbols) {
            if (rightSymbol.equals(symbol)) return true;
        }
        return false;
    }

    public Set<GrammarSymbol> getVariables() {
        Set<GrammarSymbol> variables = new HashSet<>();
        variables.add(leftVariable);
        for (GrammarSymbol rightSymbol : rightSymbols) {
            if (!rightSymbol.isTerminal()) variables.add(rightSymbol);
        }
        return variables;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Rule)) return false;
        else {
            boolean isEqual = this.leftVariable.equals(((Rule) o).leftVariable);
            isEqual &= (this.rightSymbols.size() == ((Rule) o).rightSymbols.size());
            if (isEqual) {
                for (int i = 0; i < this.rightSymbols.size(); i++) {
                    isEqual &= (this.rightSymbols.get(i).equals(((Rule) o).rightSymbols.get(i)));
                }
            }
            return isEqual;
        }
    }
    
    @Override
    public String toString() {
        String result = leftVariable.toString() + " -> ";
        for (GrammarSymbol rightSymbol : rightSymbols) {
            result += rightSymbol.toString() + " ";
        }
        return result;
    }

    public String toPrettyString(boolean showLeftVariable, Integer padding) {
        String result = showLeftVariable ? leftVariable.toString() : "";
        result += String.join("", Collections.nCopies(padding - result.length(), " ")) + "-> "; 
        for (GrammarSymbol rightSymbol : rightSymbols) result += rightSymbol.toString() + " ";
        return result;
    }

    public GrammarSymbol getLeftVariable() {
        return leftVariable;
    }

    public List<GrammarSymbol> getRightSymbols() {
        return rightSymbols;
    }
}