/**
* Imp LL(1) grammar
*
* @author Antoine Passemiers
* @author Alexis Reynouard
*/

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class L1Grammar {

    private List<Rule> rules;
    private Map<GrammarSymbol, Set<GrammarSymbol>> firsts;
    private Map<GrammarSymbol, Set<GrammarSymbol>> following;

    private int dbg_lvl;

    public L1Grammar(String path) {
        firsts = new HashMap();
        following = new HashMap();
        fromFile(path);
        removeUnproductive();
        saveRulesToFiles("grammars/unproductive_removed.grammar");
        removeInaccessible();
        saveRulesToFiles("grammars/inaccessible_removed.grammar");
        leftFactor();
        saveRulesToFiles("grammars/left_factored.grammar");

        computeFollow1();
        System.out.println("--------------------------------------------------------------------------------");
        for (GrammarSymbol toprint : this.getVariables()) {
            System.out.print(toprint);
            System.out.print(" = ");
            System.out.println(this.following.get(toprint));
        }
        System.out.println("--------------------------------------------------------------------------------");

        System.out.println(this);

        System.out.println("Finished");
    }

    public Integer getRule(GrammarSymbol variable, GrammarSymbol terminal) {
        // TODO: return cell M[A, u]
        // A is the variable on top of the stack
        // u is the look-ahead token
        return 0; // TODO
    }

    private void fromFile(String path) {
        rules = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String previousLeftHandPart = null;
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("->");
                if (parts.length == 2) { // Don't process empty lines
                    String leftHandPart = parts[0].replaceAll("\\s+","");
                    if (leftHandPart.length() > 0) {
                        previousLeftHandPart = leftHandPart;
                    } else {
                        leftHandPart = previousLeftHandPart;
                    }
                    String rightHandPart = parts[1];
                    String[] rightHandTokens = rightHandPart.split(" ");
                    for (int i = 0; i < rightHandTokens.length; i++) {
                        rightHandTokens[i] = rightHandTokens[i].replaceAll("\\s+","");
                    }
                    this.rules.add(new Rule(leftHandPart, rightHandTokens));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveRulesToFiles(final String path) {
        PrintWriter writer;
        try {
            writer = new PrintWriter(path, "UTF-8");
            for (Rule rule : rules) {
                writer.println(rule.toPrettyString(true, 20));
            }
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        String result = "";
        for (Rule rule : rules) {
            result += rule.toString() + "\n";
        }
        return result;
    }

    private Set<GrammarSymbol> getGrammarSymbols(boolean getV, boolean getT) {
        Set<GrammarSymbol> S = new HashSet<>();
        for (Rule rule : rules) {
            S.addAll(rule.getGrammarSymbols(getV, getT));
        }
        return S;
    }

    private Set<GrammarSymbol> getGrammarSymbols() {
        return getGrammarSymbols(true, true);
    }

    private Set<GrammarSymbol> getVariables() {
        return this.getGrammarSymbols(true, false);
    }

    private Set<GrammarSymbol> getTerminals() {
        return this.getGrammarSymbols(false, true);
    }

    public void removeUseless() {
        removeUnproductive();
        removeInaccessible();
    }

    public void removeUnproductive() {
        Set<GrammarSymbol> allVariables = getVariables();
        Set<GrammarSymbol> vPrevious = new HashSet<>();
        Set<GrammarSymbol> vCurrent = new HashSet<>();
        vPrevious.add(new GrammarSymbol("<DummyVariable>"));
        while (!vCurrent.equals(vPrevious)) {
            vPrevious = vCurrent;
            vCurrent = new HashSet<>();
            for (Rule rule : rules) {
                boolean toAdd = true;
                for (GrammarSymbol rightSymbol : rule.getRightSymbols()) {
                    if (!rightSymbol.isTerminal() && !vPrevious.contains(rightSymbol)) toAdd = false;
                }
                if (toAdd) vCurrent.add(rule.getLeftVariable());
            }
            vCurrent.addAll(vPrevious); // Union between V_(i-1) and V_i
        }
        Set<GrammarSymbol> unproductiveVariables = new HashSet<>();
        for (GrammarSymbol variable : allVariables) {
            if (!vCurrent.contains(variable)) unproductiveVariables.add(variable);
        }
        List<Rule> rulesToRemove = new ArrayList<>();
        for (Rule rule : rules) {
            Set<GrammarSymbol> intersection = new HashSet<GrammarSymbol>(rule.getVariables());
            intersection.retainAll(unproductiveVariables);
            if (intersection.size() > 0) rulesToRemove.add(rule);
        }
        for (Rule rule : rulesToRemove) {
            rules.remove(rule);   
        }
    }

    /**
     * Removes inaccessible variables
     * NOTE: this methods makes the assumption that the left
     * variable of the rule in this.rules is the start variable
     * of the grammar.
     */
    public void removeInaccessible() {
        Set<GrammarSymbol> vPrevious = new HashSet<>();
        Set<GrammarSymbol> vCurrent = new HashSet<>();
        vPrevious.add(new GrammarSymbol("<DummyVariable"));
        vCurrent.add(this.rules.get(0).getLeftVariable());
        while (!vCurrent.equals(vPrevious)) {
            vPrevious = vCurrent;
            vCurrent = new HashSet<>();
            for (Rule rule : rules) {
                if (vPrevious.contains(rule.getLeftVariable())) {
                    for (GrammarSymbol rightSymbol : rule.getRightSymbols()) {
                        if (!rightSymbol.isTerminal()) vCurrent.add(rightSymbol);
                    }
                }
            }
            vCurrent.addAll(vPrevious); // Union between V_(i-1) and V_i
        }
        Set<GrammarSymbol> accessibleVariables = vCurrent;
        List<Rule> rulesToRemove = new ArrayList<>();
        for (Rule rule : rules) {
            for (GrammarSymbol variable : rule.getVariables()) {
                if (!accessibleVariables.contains(variable)) rulesToRemove.add(rule);
            }
        }
        for (Rule rule : rulesToRemove) {
            rules.remove(rule);   
        }
    }

    private Set<Rule> rulesWithCommonPrefix(final Rule baseRule) {
        Set<Rule> leftSimilarRules = new HashSet<>();
        for (Rule rule : this.rules) {
            if (!baseRule.equals(rule)) {
                if (rule.getLeftVariable().equals(baseRule.getLeftVariable())) {
                    // System.out.println(baseRule.getRightSymbols().size());
                    Integer nSymbols = Math.min(baseRule.getRightSymbols().size(), rule.getRightSymbols().size());
                    boolean atLeastOneDifference = false;
                    for (int i = 0; i < nSymbols; i++) {
                        if (!rule.getRightSymbols().get(i).equals(baseRule.getRightSymbols().get(i))) {
                            atLeastOneDifference = true;
                        }
                    }
                    if (atLeastOneDifference && (rule.getRightSymbols().get(0).equals(baseRule.getRightSymbols().get(0)))) {
                        leftSimilarRules.add(rule);
                    }
                }
            }
        }
        return leftSimilarRules;
    }

    private List<GrammarSymbol> commonPrefix(final Rule baseRule, final Set<Rule> rules) {
        int prefixSize = Integer.MAX_VALUE;
        for (Rule rule : rules) {
            int nSymbols = Math.min(baseRule.getRightSymbols().size(), rule.getRightSymbols().size());
            int nCommonSymbols = 0;
            for (int i = 0; i < nSymbols; i++) {
                if (baseRule.getRightSymbols().get(i).equals(rule.getRightSymbols().get(i))) nCommonSymbols++;
                else break;
            }
            prefixSize = Math.min(prefixSize, nCommonSymbols);
        }
        List<GrammarSymbol> prefix = new ArrayList<>();
        for (int i = 0; i < prefixSize; i++) {
            prefix.add(baseRule.getRightSymbols().get(i));
        }
        return prefix;
    }

    private Rule hasRulesWithCommonPrefix() {
        for (Rule rule : this.rules) {
            if (rulesWithCommonPrefix(rule).size() > 0) return rule;
        }
        return null;
    }

    public void leftFactor() {
        Rule baseRule;
        while ((baseRule = hasRulesWithCommonPrefix()) != null) {
            Set<Rule> leftSimilarRules = rulesWithCommonPrefix(baseRule);
            String newVariableName = baseRule.getLeftVariable().toString();
            newVariableName = newVariableName.substring(0, newVariableName.length() - 1) + "-Tail>";
            GrammarSymbol newVariable = new GrammarSymbol(newVariableName);
            List<GrammarSymbol> prefix = commonPrefix(baseRule, leftSimilarRules);

            System.out.println(prefix);

            leftSimilarRules.add(baseRule);
            Integer prefixSize = prefix.size();
            prefix.add(newVariable);

            this.rules.add(new Rule(baseRule.getLeftVariable(), prefix));
            for (Rule rule : leftSimilarRules) {
                Rule newRule = new Rule(newVariable, rule.removePrefix(prefixSize).getRightSymbols());
                System.out.println("New: " + newRule);
                this.rules.add(newRule);
                this.rules.remove(rule);
            }
        }
    }

    public Set<GrammarSymbol> first1(GrammarSymbol symbol) {

        if (this.firsts.containsKey(symbol)) {
            return this.firsts.get(symbol);
        }

        Set<GrammarSymbol> symbols = new HashSet<>();

        if (symbol.isTerminal()) {
            symbols.add(symbol);
        }

        else { // symbol is a variable
            for (Rule rule : this.rules) {
                if (rule.getLeftVariable().equals(symbol)) {
                    GrammarSymbol s = rule.getRightSymbols().get(0);
                    symbols.addAll(first1(s));
                }
            }
        }

        this.firsts.put(symbol, symbols);
        return symbols;
    }

    public void computeFollow1() {
        // Only 1 call needed I think
        assert(this.following.size() == 0);
        // Initialization
        for (GrammarSymbol variable : this.getVariables()) {
            this.following.put(variable, new HashSet<GrammarSymbol>());
        }
        this.following.get(this.rules.get(0).getLeftVariable()).add(new GrammarSymbol("epsilon"));

        // Fix point iteration
        boolean finished;
        do {
            finished = true;
            for (Rule rule : this.rules) {
                List<GrammarSymbol> rSymbols = rule.getRightSymbols();
                for (int rSIdx = 0; rSIdx < rSymbols.size() - 1; ++rSIdx) {
                    GrammarSymbol va = rSymbols.get(rSIdx);
                    if (!va.isTerminal()) {
                        finished = finished && !this.following.get(va).addAll(first1(rSymbols.get(rSIdx+1)));
                    }
                }
                int rSIdx = rSymbols.size()-1;
                GrammarSymbol va = rSymbols.get(rSIdx);
                if (!va.isTerminal()) {
                    finished = finished && !this.following.get(va).addAll(this.following.get(rule.getLeftVariable()));
                }
            }
        } while(!finished);
    }
}
