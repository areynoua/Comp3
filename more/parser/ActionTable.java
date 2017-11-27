package parser;

import java.lang.String;
import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
* Imp LL(1) Parser's action table
*
* @author Antoine Passemiers
* @author Alexis Reynouard
*/
public class ActionTable {

    /** action: match the terminal */
    public static final Integer MATCH = -1;
    /** action: code accepted */
    public static final Integer ACCEPT = -2;
    /** action: does not match the terminal */
    public static final Integer ERROR = -3;

    /** Action table */
    private Map<List<GrammarSymbol>, Integer> M;
    /** Source grammar */
    private final LL1Grammar grammar;

    /**
     * Creates an action table from a grammar. This has been implemented in such
     * a way that the table can be built from any grammar.
     *
     * @param  grammar   Grammar to use
     */
    ActionTable(final LL1Grammar grammar) {
        this.grammar = grammar;
        // Compute first set and follow set for each symbol
        Map<GrammarSymbol, Set<GrammarSymbol>> first = grammar.getFirst();
        Map<GrammarSymbol, Set<GrammarSymbol>> follow = grammar.getFollow();
        M = new HashMap<>();
        // Infer the action table from first and follow sets
        for (GrammarSymbol symbol : getGrammarSymbols()) { // For each line
            for (GrammarSymbol terminal : getTerminals(true)) { // For each column
                if (symbol.isTerminal()) {
                    // In case where symbol on TOS is equal to next input terminal, there is a match
                    if (symbol.equals(terminal)) M.put(Arrays.asList(symbol, terminal), ActionTable.MATCH);
                    // If the TOS is a terminal and is different from the next input terminal,
                    // this implies the existence of a syntax error.
                    else M.put(Arrays.asList(symbol, terminal), ActionTable.ERROR);
                } else {
                    M.put(Arrays.asList(symbol, terminal), ActionTable.ERROR);
                }
            }
        }
        // The sequence of tokens is accepted by empty stack if the only symbol remaining
        // in the stack is a $, and the next input token is $.
        M.put(Arrays.asList(GrammarSymbol.EOS, GrammarSymbol.EOS), ActionTable.ACCEPT);
        int i = 0;
        for (Rule rule : grammar.getRules()) {
            GrammarSymbol variable = rule.getLeftVariable();
            // In case where the right-hand part of the rule consists only of an epsilon,
            // the next terminal to be derived is located in follow(variable)
            boolean hasEpsilon = false;
            for (GrammarSymbol terminal : computeFirst(rule.getRightSymbols())) {
                M.put(Arrays.asList(variable, terminal), i);
                if (terminal.equals(GrammarSymbol.EPSILON)) hasEpsilon = true;
            }
            if (hasEpsilon) { // If left-hand part of the rule contains only epsilon
                for (GrammarSymbol terminal : follow.get(rule.getLeftVariable())) {
                    M.put(Arrays.asList(variable, terminal), i);
                }   
            }
            ++i;
        }
    }

    /** 
     * Computes the follow set of a sequence of symbols.
     *
     * @param  sequence   Sequence of symbols, which must necessarily come
     *         from the right-hand part of a rule
     */
    Set<GrammarSymbol> computeFirst(List<GrammarSymbol> sequence) {
        Map<GrammarSymbol, Set<GrammarSymbol>> first = grammar.getFirst();
        Set<GrammarSymbol> symbols = new HashSet<>();
        int i = 0;
        boolean hasEpsilon = true;
        while (hasEpsilon && (i < sequence.size())) {
            // Add first set of each symbol from the sequence
            Set<GrammarSymbol> terminals = first.get(sequence.get(i));
            symbols.addAll(terminals);
            // Check whether epsilon is contained in the set or not
            hasEpsilon = (terminals.contains(GrammarSymbol.EPSILON));
            i++;
        }
        if (i == sequence.size()) symbols.add(GrammarSymbol.EPSILON);
        return symbols;
    }

    /** 
     * Get element M[symbol, terminal] from the action table, where
     * symbol is the top of the stack and terminal is the next input
     * token to be read.
     * 
     * @param symbol  Top of the stack
     * @param terminal  Next token to read from the inputs
     */
    Integer get(GrammarSymbol symbol, GrammarSymbol terminal) {
        return M.get(Arrays.asList(symbol, terminal));
    }

    /** 
     * Helper function for getting the set of all terminals in the grammar
     *
     * @param includeEpsilon  Whether to add epsilon to the set of terminals or not
     */
    private Set<GrammarSymbol> getTerminals(boolean includeEpsilon) {
        Set<GrammarSymbol> terminals = grammar.getTerminals();
        if (includeEpsilon) {
            terminals.add(GrammarSymbol.EPSILON);
        } else {
            terminals.remove(GrammarSymbol.EPSILON);
        }
        terminals.add(GrammarSymbol.EOS); // Add $ symbol
        return terminals;
    }

    /** Helper function for getting all the grammar symbols */
    private Set<GrammarSymbol> getGrammarSymbols() {
        Set<GrammarSymbol> symbols = grammar.getGrammarSymbols();
        symbols.add(GrammarSymbol.EPSILON); // Add epsilon
        symbols.add(GrammarSymbol.EOS); // Add $ symbol
        return symbols;
    }

    /** String representation of the table */
    @Override
    public String toString() {
        // Column header
        String result = String.join("", Collections.nCopies(19, " "));
        for (GrammarSymbol terminal : getTerminals(false)) {
            result += String.format("%8s", terminal) + " ";
        }
        result += "\n";
        for (GrammarSymbol symbol : getGrammarSymbols()) {
            if (!symbol.isTerminal()) {
                // Line header
                result += String.format("%18s", symbol) + " ";
                // Display each cell of current line
                for (GrammarSymbol terminal : getTerminals(false)) {
                    Integer action = get(symbol, terminal);
                    if (action != ActionTable.ERROR) { // Put action number
                        result += String.format("%8s", action) + " ";
                    } else { // Put spaces
                        result += String.format("%8s", " ") + " ";
                    }
                }
                result += "\n";
            }
        }
        return result;
    }

    /** Returns latex tabular */
    public String toLatexString() {
        List<GrammarSymbol> variables = new ArrayList();
        List<GrammarSymbol> terminals = new ArrayList();
        variables.addAll(grammar.getVariables());
        terminals.addAll(grammar.getTerminals());
        terminals.remove(GrammarSymbol.EPSILON);
        variables.sort(null);
        terminals.sort(null);
        terminals.add(GrammarSymbol.EOS);
        terminals.add(GrammarSymbol.EPSILON);

        StringBuilder sb = new StringBuilder();
        sb.append("\\begin{tabular}{r|");
        sb.append(String.join("", Collections.nCopies(terminals.size(), "@{\\hskip0.12em}c")));
        sb.append("}\n");
        for (GrammarSymbol terminal : terminals) {
            sb.append(" & \\verb=");
            sb.append(terminal.toString());
            sb.append("=");
        }
        sb.append("\\\\\n");

        // First half of the table, mapping input tokens to variables
        for (GrammarSymbol row : variables) {
            sb.append("\\verb=");
            sb.append(row);
            sb.append("=");
            for (GrammarSymbol terminal : terminals) {
                Integer action = get(row, terminal);
                sb.append(" & ");
                sb.append(action != ActionTable.ERROR ? action : " ");
            }
            sb.append(" \\\\\n");
        }
        sb.append("\\hline\n");
        // First half of the table, mapping input tokens to terminals
        for (GrammarSymbol row : terminals) {
            if (row.equals(GrammarSymbol.EPSILON))
                continue;
            sb.append("\\verb=");
            sb.append(row);
            sb.append("=");
            for (GrammarSymbol terminal : terminals) {
                Integer action = get(row, terminal);
                sb.append(" & ");
                sb.append(action != ActionTable.ERROR ? action : " ");
            }
            sb.append("\\\\\n");
        }

        sb.append("\\end{tabular}");
        return sb.toString();
    }
}
