package parser;

import java.lang.String;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Imp grammar rules
 *
 * @author Antoine Passemiers
 * @author Alexis Reynouard
 */
public class Rule {

    /** Left-hand variable of the rule */
    private GrammarSymbol leftVariable;
    /** Right-hand part of the rule, which consists of a sequence of symbols,
     * that can be either variables or terminals */
    private List<GrammarSymbol> rightSymbols;

    /**
     * Creates a rule from its two parts. We made the assumption that the left-hand
     * part of a rule is composed of ony and only one variable.
     *
     * @param leftVariable  Left-hand variable
     * @param rightSymbols  Right-hand sequence of symbols
     */
    public Rule(GrammarSymbol leftVariable, List<GrammarSymbol> rightSymbols) {
        this.leftVariable = leftVariable;
        this.rightSymbols = rightSymbols;
        assert(this.rightSymbols.size() > 0); // Ensure that it is not empty
    }

    /**
     * Creates a rule from symbols names
     *
     * @param leftVariable  Name of left-hand variable
     * @param rightSymbols  Names of symbols in right-hand part
     */
    public Rule(String leftVariable, String[] rightSymbols) {
        this.leftVariable = new GrammarSymbol(leftVariable);
        this.rightSymbols = new ArrayList<>();
        for (String rightSymbol : rightSymbols) {
            if (rightSymbol.length() > 0) {
                this.rightSymbols.add(new GrammarSymbol(rightSymbol));                
            }
        }
        assert(this.rightSymbols.size() > 0); // Ensure that it is not empty
    }

    /**
     * Returns new rule containing the same variable as left-hand part,
     * and a new right-hand part with prefix removed.
     *
     * @param prefixSize  The size of the prefix to remove from right-hand part
     */
    public Rule removePrefix(Integer prefixSize) {
        List<GrammarSymbol> withoutPrefix = rightSymbols.subList(prefixSize, rightSymbols.size());
        return new Rule(leftVariable, withoutPrefix);
    }

    /**
     * Tells whether the rule contains the given symbol as the left-hand variable or
     * in the right-hand part.
     *
     * @param symbol  Symbol to look for
     */
    public boolean contains(GrammarSymbol symbol) {
        if (leftVariable.equals(symbol)) return true;
        for (GrammarSymbol rightSymbol : rightSymbols) {
            if (rightSymbol.equals(symbol)) return true;
        }
        return false; // If it has not been found
    }

    /** Tells whether the rule if left-recursive all by itself */
    public boolean isLeftRecursive() { 
        // Suppose that there is no rule like "A → epsilon alpha" with alpha ≠ epsilon
        return this.getLeftVariable().equals(this.getRightSymbols().get(0));
    }

    /**
     * Removes left-recursion after checking that the rule if left-recursive all by itself.
     *
     * @param nonLeftRecursiveSuffix  New left-hand variable name to use in case 
     *        where the rule is not left-recursive
     * @param leftRecursiveSuffix  New left-hand variable name to use in case
     *        where the rule is left-recursive
     */
    public void removeLeftRecursion(String nonLeftRecursiveSuffix, String leftRecursiveSuffix) {
        if (isLeftRecursive()) { // Rule is left-recursive all by itself
            this.leftVariable = new GrammarSymbol(this.leftVariable.nameVariant(leftRecursiveSuffix));
            this.rightSymbols.remove(0);
        }
        else {
            this.leftVariable = new GrammarSymbol(this.leftVariable.nameVariant(nonLeftRecursiveSuffix));
        }
    }

    /**
     * Helper function that returns the rule symbols as a single set.
     * Depending on arguments, it can return either the set of variables,
     * the set of terminals or the set of symbols
     *
     * @param getV  Whether to include variables or not
     * @param getT  Whether to include terminals or not
     */
    public Set<GrammarSymbol> getGrammarSymbols(boolean getV, boolean getT) {
        Set<GrammarSymbol> symbols = new HashSet<>();
        if (getV) { // Include left-hand variable if variables have been asked
            symbols.add(leftVariable);
        }
        for (GrammarSymbol rightSymbol : rightSymbols) {
            if ((rightSymbol.isTerminal() && getT) || (!rightSymbol.isTerminal() && getV)) {
                symbols.add(rightSymbol);
            }
        }
        return symbols;
    }

    /** Returns the set of variables in the rule */
    public Set<GrammarSymbol> getVariables() {
        return this.getGrammarSymbols(true, false);
    }

    /** Returns the set of terminals in the rule */
    public Set<GrammarSymbol> getTerminals() {
        return this.getGrammarSymbols(false, true);
    }

    /** Compares rules using their two parts (in worst case) or only their left variable */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Rule)) return false; // If the other object is not a rule
        else {
            boolean isEqual = this.leftVariable.equals(((Rule) o).leftVariable);
            isEqual &= (this.rightSymbols.size() == ((Rule) o).rightSymbols.size());
            if (isEqual) { // If the left-variable is identical (not sufficient for comparison)
                for (int i = 0; i < this.rightSymbols.size(); i++) {
                    isEqual &= (this.rightSymbols.get(i).equals(((Rule) o).rightSymbols.get(i)));
                }
            }
            return isEqual;
        }
    }

    /** String representation of the rule */
    @Override
    public String toString() {
        String result = leftVariable.toString() + " -> ";
        for (GrammarSymbol rightSymbol : rightSymbols) {
            result += rightSymbol.toString() + " ";
        }
        return result;
    }

    /** 
     * Pretty string representation of the rule.
     *
     * @param showLeftVariable  Whether to add left-hand variable or not.
     *        This argument is useful for displaying the grammar, with
     *        adjacent rules that have the same left-hand variable
     * @param padding  Number of padding spaces
     */
    public String toPrettyString(boolean showLeftVariable, Integer padding) {
        String result = showLeftVariable ? leftVariable.toString() : "";
        result += String.join("", Collections.nCopies(padding - result.length(), " ")) + "-> "; 
        for (GrammarSymbol rightSymbol : rightSymbols) result += rightSymbol.toString() + " ";
        return result;
    }

    /**
     * String representation of the rule, adapted for LaTex.
     * It is very similar to the toPrettyString method.
     *
     * @param showLeftVariable  Whether to add left-hand variable or not.
     * @see toPrettyString
     */
    public String toLatex(boolean showLeftVariable) {
        String result = "  & ";
        if (showLeftVariable) {
            result += "\\varstyle{" + leftVariable.withoutChevrons() + "}";
        }
        result += " & ";
        for (GrammarSymbol rightSymbol : rightSymbols) {
            if (rightSymbol.isTerminal()) {
                result += rightSymbol.toString() + " ";
            } else {
                result += "\\varstyle{" + rightSymbol.withoutChevrons() + "} ";
            }
        }
        result += "\\\\";
        return result;
    }

    /** Get left-hand variable of the rule */
    public GrammarSymbol getLeftVariable() {
        return leftVariable;
    }

    /** Get right-hand sequence of symbols */
    public List<GrammarSymbol> getRightSymbols() {
        return rightSymbols;
    }
}
