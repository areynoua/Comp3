package parser;
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


public class LL1Grammar {

    private List<Rule> rules;

    private Map<GrammarSymbol, Set<GrammarSymbol>> first;
    private Map<GrammarSymbol, Set<GrammarSymbol>> follow;

    private int dbg_lvl;

    public LL1Grammar(String path) {
        fromFile(path);
        //removeUnproductive();
        //saveRulesToFiles("grammars/unproductive_removed.grammar");
        //removeInaccessible();
        //saveRulesToFiles("grammars/inaccessible_removed.grammar");
        //leftFactor();
        //saveRulesToFiles("grammars/left_factored.grammar");
        //removeLeftRecursion();
        //saveRulesToFiles("grammars/left_recursion_removed.grammar");
        computeFirst();
        computeFollow();
        System.out.println("--------------------------------------------------------------------------------");

        for (GrammarSymbol toprint : this.getVariables()) {
            System.out.print("first(");
            System.out.print(toprint);
            System.out.print(") = ");
            System.out.println(this.first.get(toprint));
        }
        System.out.println("--------------------------------------------------------------------------------");

        for (GrammarSymbol toprint : this.getVariables()) {
            System.out.print("follow(");
            System.out.print(toprint);
            System.out.print(") = ");
            System.out.println(this.follow.get(toprint));
        }
        System.out.println("--------------------------------------------------------------------------------");

        System.out.println(this);

        System.out.println("Finished");
    }

    public Map<GrammarSymbol, Set<GrammarSymbol>> getFirst() {
        return first;
    }

    public Map<GrammarSymbol, Set<GrammarSymbol>> getFollow() {
        return follow;
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

    public Set<GrammarSymbol> getGrammarSymbols(boolean getV, boolean getT) {
        Set<GrammarSymbol> S = new HashSet<>();
        for (Rule rule : rules) {
            S.addAll(rule.getGrammarSymbols(getV, getT));
        }
        return S;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public Set<GrammarSymbol> getGrammarSymbols() {
        return getGrammarSymbols(true, true);
    }

    public Set<GrammarSymbol> getVariables() {
        return this.getGrammarSymbols(true, false);
    }

    public Set<GrammarSymbol> getTerminals() {
        return this.getGrammarSymbols(false, true);
    }

    public Set<GrammarSymbol> getLeftRecursiveVariables() {
        Set<GrammarSymbol> leftRecursiveVariables = new HashSet<>();
        for (Rule rule : this.rules) {
            if (rule.isLeftRecursive()) {
                leftRecursiveVariables.add(rule.getLeftVariable());
            }
        }
        return leftRecursiveVariables;
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
            if (!baseRule.equals(rule) && rule.getLeftVariable().equals(baseRule.getLeftVariable())) {
                // System.out.println(baseRule.getRightSymbols().size());
                Integer nSymbols = Math.min(baseRule.getRightSymbols().size(), rule.getRightSymbols().size());
                boolean atLeastOneDifference = false;
                for (int i = 0; i < nSymbols && !atLeastOneDifference; i++) {
                    if (!rule.getRightSymbols().get(i).equals(baseRule.getRightSymbols().get(i))) {
                        atLeastOneDifference = true;
                    }
                }
                if (atLeastOneDifference && (rule.getRightSymbols().get(0).equals(baseRule.getRightSymbols().get(0)))) {
                    leftSimilarRules.add(rule);
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

    public void removeLeftRecursion() {
        // A → Aa
        // A → B
        Set<GrammarSymbol> leftRecursiveVariables = this.getLeftRecursiveVariables();
        Set<Rule> newRules = new HashSet<>();
        for (GrammarSymbol variable : leftRecursiveVariables) {
            // A → UV
            List<GrammarSymbol> rightij = new ArrayList<>(2);
            rightij.add(new GrammarSymbol(variable.nameVariant("i")));
            rightij.add(new GrammarSymbol(variable.nameVariant("j")));
            newRules.add(new Rule(variable, rightij));

            // V → epsilon
            List<GrammarSymbol> righteps = new ArrayList<>(1);
            righteps.add(GrammarSymbol.EPSILON);
            newRules.add(new Rule(new GrammarSymbol(variable.nameVariant("j")), righteps));
        }
        for (Rule rule : this.rules) {
            if (leftRecursiveVariables.contains(rule.getLeftVariable())) {
                // V → a  or  U → B
                rule.removeLeftRecursion("i","j");
            }
        }

        this.rules.addAll(newRules);
    }

    private void computeFirst() {
        assert(this.first == null);
        this.first = new HashMap<GrammarSymbol, Set<GrammarSymbol>>();
        for (GrammarSymbol terminal : this.getTerminals()) {
            Set<GrammarSymbol> f = new HashSet<>(1);
            f.add(terminal);
            this.first.put(terminal, f);
        }
        for (GrammarSymbol variable : this.getVariables()) {
            this.first.put(variable, new HashSet<GrammarSymbol>());
        }

        boolean finished;
        do {
            finished = true;
            for (Rule rule : this.rules) {
                Set<GrammarSymbol> toAdd = new HashSet<>();
                toAdd.add(GrammarSymbol.EPSILON);
                for (int gsIdx = 0;
                        gsIdx < rule.getRightSymbols().size() && toAdd.contains(GrammarSymbol.EPSILON);
                        ++gsIdx) {
                    toAdd.remove(GrammarSymbol.EPSILON);
                    toAdd.addAll(this.first.get(rule.getRightSymbols().get(gsIdx)));
                }
                finished = finished && !this.first.get(rule.getLeftVariable()).addAll(toAdd);
            }
        } while(!finished);
    }

    private void computeFollow() {
        assert(this.follow == null);
        this.follow = new HashMap<GrammarSymbol, Set<GrammarSymbol>>();
        for (GrammarSymbol variable : this.getVariables()) {
            this.follow.put(variable, new HashSet<GrammarSymbol>());
        }
        this.follow.get(this.rules.get(0).getLeftVariable()).add(GrammarSymbol.EPSILON);

        boolean finished;
        do {
            finished = true;
            for (Rule rule : this.rules) {
                for (int rsi = 0; rsi < rule.getRightSymbols().size() - 1; ++rsi) {
                    GrammarSymbol var = rule.getRightSymbols().get(rsi);
                    if (!var.isTerminal()) {
                        Set<GrammarSymbol> toAdd = new HashSet<>();
                        toAdd.addAll(this.first.get(rule.getRightSymbols().get(rsi + 1)));
                        if (toAdd.contains(GrammarSymbol.EPSILON)) {
                            toAdd.remove(GrammarSymbol.EPSILON);
                            toAdd.addAll(this.follow.get(rule.getLeftVariable()));
                        }
                        finished = finished && !this.follow.get(var).addAll(toAdd);
                    }
                }
                GrammarSymbol var = rule.getRightSymbols().get(rule.getRightSymbols().size() - 1);
                if (!var.isTerminal()) {
                    Set<GrammarSymbol> toAdd = new HashSet<>();
                    toAdd.addAll(this.follow.get(rule.getLeftVariable()));
                    finished = finished && !this.follow.get(var).addAll(toAdd);
                }
            }
        } while(!finished);
    }
}
