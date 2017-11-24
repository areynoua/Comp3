/**
* Imp LL(1) Parser's action table
*
* @author Antoine Passemiers
* @author Alexis Reynouard
*/

import java.lang.String;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ActionTable {

    private static final Integer MATCH = -1;
    private static final Integer ACCEPT = -2;
    private static final Integer ERROR = -3;

    private Map<List<GrammarSymbol>, Integer> M;
    private L1Grammar grammar;

    ActionTable(L1Grammar grammar) {
        this.grammar = grammar;
        Map<GrammarSymbol, Set<GrammarSymbol>> first = grammar.getFirst();
        Map<GrammarSymbol, Set<GrammarSymbol>> follow = grammar.getFollow();
        M = new HashMap<>();
        for (GrammarSymbol symbol : getGrammarSymbols()) {
            for (GrammarSymbol terminal : getTerminals()) {
                if (symbol.isTerminal()) {
                    if (symbol.equals(terminal)) M.put(Arrays.asList(symbol, terminal), ActionTable.MATCH);
                    else M.put(Arrays.asList(symbol, terminal), ActionTable.ERROR);
                } else {
                    M.put(Arrays.asList(symbol, terminal), ActionTable.ERROR);
                }
            }
        }
        M.put(Arrays.asList(GrammarSymbol.EOS, GrammarSymbol.EOS), ActionTable.ACCEPT);

        int i = 0;
        for (Rule rule : grammar.getRules()) {
            GrammarSymbol variable = rule.getLeftVariable();
            for (GrammarSymbol terminal : first.get(rule.getRightSymbols().get(0))) {
                M.put(Arrays.asList(variable, terminal), i);
            }
            i++;
        }

        System.out.println(this.toString());
    }

    Integer get(GrammarSymbol symbol, GrammarSymbol terminal) {
        return M.get(Arrays.asList(symbol, terminal));
    }

    @Override
    public String toString() {
        String result = String.join("", Collections.nCopies(19, " "));
        for (GrammarSymbol terminal : getTerminals()) {
            result += String.format("%8s", terminal) + " ";
        }
        result += "\n";
        for (GrammarSymbol symbol : getGrammarSymbols()) {
            result += String.format("%18s", symbol) + " ";
            for (GrammarSymbol terminal : getTerminals()) {
                Integer action = get(symbol, terminal);
                result += String.format("%8s", action) + " ";
            }
            result += "\n";
        }
        return result;
    }

    private Set<GrammarSymbol> getTerminals() {
        Set<GrammarSymbol> terminals = grammar.getTerminals();
        terminals.add(GrammarSymbol.EPSILON);
        terminals.add(GrammarSymbol.EOS);
        return terminals;
    }

    private Set<GrammarSymbol> getGrammarSymbols() {
        Set<GrammarSymbol> symbols = grammar.getGrammarSymbols();
        symbols.add(GrammarSymbol.EPSILON);
        symbols.add(GrammarSymbol.EOS);
        return symbols;
    }
}
