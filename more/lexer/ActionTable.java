/**
* Imp LL(1) Parser's action table
*
* @author Antoine Passemiers
* @author Alexis Reynouard
*/

import java.lang.Math;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;


public class ActionTable {

    private List<Rule> rules;
    
    public ActionTable() {
        create();
        removeUnproductive();
        saveRulesToFiles("unproductive_removed.grammar");
        removeInaccessible();
        saveRulesToFiles("inaccessible_removed.grammar");
        leftFactor();
        saveRulesToFiles("left_factored.grammar");

        for (Rule rule : rules) {
            System.out.println(rule);
        }

        System.out.println("Finished");
    }

    public Integer getRule(GrammarSymbol variable, GrammarSymbol terminal) {
        // TODO: return cell M[A, u]
        // A is the variable on top of the stack
        // u is the look-ahead token
        return 0; // TODO
    }

    private void create() {
        rules = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("imp.grammar"))) {
            String previousLeftHandPart = null;
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("->");
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

    private Set<GrammarSymbol> getVariables() {
        Set<GrammarSymbol> V = new HashSet<>();
        for (Rule rule : rules) {
            for (GrammarSymbol variable : rule.getVariables()) {
                V.add(variable);
            }
        }
        return V;
    }

    private void removeUseless() {
        removeUnproductive();
        removeInaccessible();
    }

    private void removeUnproductive() {
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

    private void removeInaccessible() {
        Set<GrammarSymbol> vPrevious = new HashSet<>();
        Set<GrammarSymbol> vCurrent = new HashSet<>();
        vPrevious.add(new GrammarSymbol("<DummyVariable"));
        vCurrent.add(new GrammarSymbol("<Program>"));
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
        for (Rule rule : rules) {
            if (!baseRule.equals(rule)) {
                if (rule.getLeftVariable().equals(baseRule.getLeftVariable())) {
                    if (rule.getRightSymbols().get(0).equals(baseRule.getRightSymbols().get(0))) {
                        leftSimilarRules.add(rule);
                    }
                }
            }
        }
        return leftSimilarRules;
    }

    private List<GrammarSymbol> commonPrefix(final Rule baseRule, final Set<Rule> rules) {
        int prefixSize = Integer.MAX_VALUE;
        for (Rule rule : this.rules) {
            int nCommonSymbols = 0;
            int nSymbols = Math.min(baseRule.getRightSymbols().size(), rule.getRightSymbols().size());
            for (int i = 0; i < nSymbols; i++) {
                if (baseRule.getRightSymbols().get(i).equals(rule.getRightSymbols().get(i))) nCommonSymbols++;
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

    private void leftFactor() {
        Rule baseRule;
        while ((baseRule = hasRulesWithCommonPrefix()) != null) {
            Set<Rule> leftSimilarRules = rulesWithCommonPrefix(baseRule);
            String newVariableName = baseRule.getLeftVariable().toString();
            newVariableName = newVariableName.substring(0, newVariableName.length() - 1) + "-Tail>";
            GrammarSymbol newVariable = new GrammarSymbol(newVariableName);
            List<GrammarSymbol> prefix = commonPrefix(baseRule, leftSimilarRules);

            // leftSimilarRules.add(baseRule);

            prefix.add(newVariable);
            this.rules.add(new Rule(baseRule.getLeftVariable(), prefix));
            for (Rule rule : leftSimilarRules) {
                this.rules.add(new Rule(newVariable, rule.removePrefix(prefix.size()).getRightSymbols()));
                this.rules.remove(rule);
            }

            System.out.println("shdsdj");
        }
    }

}