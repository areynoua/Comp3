package parser;
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
        assert(this.rightSymbols.size() > 0);
    }

    public Rule(String leftVariable, String[] rightSymbols) {
        this.leftVariable = new GrammarSymbol(leftVariable);
        this.rightSymbols = new ArrayList<>();
        for (String rightSymbol : rightSymbols) {
            if (rightSymbol.length() > 0) {
                this.rightSymbols.add(new GrammarSymbol(rightSymbol));                
            }
        }
        assert(this.rightSymbols.size() > 0);
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

    public boolean isLeftRecursive() { // Suppose that there is no rule like "A → epsilon alpha" with alpha ≠ epsilon
        return this.getLeftVariable().equals(this.getRightSymbols().get(0));
    }

    public void removeLeftRecursion(String nonLeftRecursiveSuffix, String leftRecursiveSuffix) {
        if (isLeftRecursive()) {
            this.leftVariable = new GrammarSymbol(this.leftVariable.nameVariant(leftRecursiveSuffix));
            this.rightSymbols.remove(0);
        }
        else {
            this.leftVariable = new GrammarSymbol(this.leftVariable.nameVariant(nonLeftRecursiveSuffix));
        }
    }

    public Set<GrammarSymbol> getGrammarSymbols(boolean getV, boolean getT) {
        Set<GrammarSymbol> symbols = new HashSet<>();
        if (getV) {
            symbols.add(leftVariable);
        }
        for (GrammarSymbol rightSymbol : rightSymbols) {
            if ((rightSymbol.isTerminal() && getT) || (!rightSymbol.isTerminal() && getV)) {
                symbols.add(rightSymbol);
            }
        }
        return symbols;
    }

    public Set<GrammarSymbol> getVariables() {
        return this.getGrammarSymbols(true, false);
    }

    public Set<GrammarSymbol> getTerminals() {
        return this.getGrammarSymbols(false, true);
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
