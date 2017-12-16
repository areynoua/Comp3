package parser;

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

/**
* Imp LL(1) grammar
*
* @author Antoine Passemiers
* @author Alexis Reynouard
*/
public class LL1Grammar {

    private List<Rule> rules;

    private Map<GrammarSymbol, Set<GrammarSymbol>> first;
    private Map<GrammarSymbol, Set<GrammarSymbol>> follow;

    /** Construct grammar from rules from an file */
    public LL1Grammar(String path) throws FileNotFoundException, IOException {
        fromFile(path);
        computeFirst();
        computeFollow();
    }

    /** First1 set */
    public Map<GrammarSymbol, Set<GrammarSymbol>> getFirst() {
        return first;
    }

    /** Follow1 set */
    public Map<GrammarSymbol, Set<GrammarSymbol>> getFollow() {
        return follow;
    }

    /** Actual construction from file */
    private void fromFile(String path) throws FileNotFoundException, IOException {
        rules = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(path));
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
    }

    /** Save the grammar to a file */
    public void saveRulesToFile(final String path) {
        PrintWriter writer;
        try {
            writer = new PrintWriter(path, "UTF-8");
            GrammarSymbol lastVariable = GrammarSymbol.EPSILON;
            for (Rule rule : rules) {
                boolean keepLeft = (!lastVariable.equals(rule.getLeftVariable()));
                writer.println(rule.toPrettyString(keepLeft, 20));
                lastVariable = rule.getLeftVariable();
            }
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /** Save the grammar to a file in a latex format */
    public void saveLatexRulesToFiles(final String path) {
        PrintWriter writer;
        try {
            writer = new PrintWriter(path, "UTF-8");
            GrammarSymbol lastVariable = GrammarSymbol.EPSILON;
            for (Rule rule : rules) {
                writer.println(rule.toLatex(!lastVariable.equals(rule.getLeftVariable())));
                lastVariable = rule.getLeftVariable();
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

    /** Return a set of all grammar symbols that exists in any rule
     *
     * @param getT: include terminals
     * @param getV: include variables
     */
    public Set<GrammarSymbol> getGrammarSymbols(boolean getV, boolean getT) {
        Set<GrammarSymbol> S = new HashSet<>();
        for (Rule rule : rules) {
            S.addAll(rule.getGrammarSymbols(getV, getT));
        }
        return S;
    }

    /** Return rules */
    public List<Rule> getRules() {
        return rules;
    }

    /**  Return a set of all grammar symbols that exists in any rule */
    public Set<GrammarSymbol> getGrammarSymbols() {
        return getGrammarSymbols(true, true);
    }

    /**  Return a set of all grammar variables that exists in any rule */
    public Set<GrammarSymbol> getVariables() {
        return this.getGrammarSymbols(true, false);
    }

    /**  Return a set of all grammar terminals that exists in any rule */
    public Set<GrammarSymbol> getTerminals() {
        return this.getGrammarSymbols(false, true);
    }

    /** return the left recursive variables */
    public List<GrammarSymbol> getLeftRecursiveVariables() {
        List<GrammarSymbol> leftRecursiveVariables = new ArrayList<>();
        for (Rule rule : this.rules) {
            if (rule.isLeftRecursive()) {
                leftRecursiveVariables.add(rule.getLeftVariable());
            }
        }
        return leftRecursiveVariables;
    }

    /** remove the useless rules */
    public void removeUseless() {
        removeUnproductive();
        removeInaccessible();
    }

    /** remove the productive rules */
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
     * NOTE: this method makes the assumption that the left
     * variable of the first rule in this.rules is the start variable
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

    /** return the rules with common prefix */
    private List<Rule> rulesWithCommonPrefix(final Rule baseRule) {
        List<Rule> leftSimilarRules = new ArrayList<>();
        for (Rule rule : this.rules) {
            if (!baseRule.equals(rule) && rule.getLeftVariable().equals(baseRule.getLeftVariable())) {
                if (rule.getRightSymbols().get(0).equals(baseRule.getRightSymbols().get(0))) {
                    leftSimilarRules.add(rule);
                }
            }
        }
        return leftSimilarRules;
    }

    /** return the longest common prefix with baseRule */
    private List<GrammarSymbol> commonPrefix(final Rule baseRule, final List<Rule> rules) {
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

    /** return a rule with a common prefix */
    private Rule hasRulesWithCommonPrefix() {
        for (Rule rule : this.rules) {
            if (rulesWithCommonPrefix(rule).size() > 0) return rule;
        }
        return null;
    }

    /** Perform a left factorization of the grammar */
    public void leftFactor() {
        Rule baseRule;
        while ((baseRule = hasRulesWithCommonPrefix()) != null) {
            List<Rule> leftSimilarRules = rulesWithCommonPrefix(baseRule);
            String newVariableName = baseRule.getLeftVariable().toString();
            newVariableName = newVariableName.substring(0, newVariableName.length() - 1) + "-Tail>";
            GrammarSymbol newVariable = new GrammarSymbol(newVariableName);
            List<GrammarSymbol> prefix = commonPrefix(baseRule, leftSimilarRules);

            leftSimilarRules.add(baseRule);
            Integer prefixSize = prefix.size();
            prefix.add(newVariable);

            this.rules.add(new Rule(baseRule.getLeftVariable(), prefix));
            for (Rule rule : leftSimilarRules) {
                List<GrammarSymbol> rightHandPart = rule.removePrefix(prefixSize).getRightSymbols();
                if (rightHandPart.size() == 0) rightHandPart.add(GrammarSymbol.EPSILON);
                Rule newRule = new Rule(newVariable, rightHandPart);
                // System.out.println("New: " + newRule);
                this.rules.add(newRule);
                this.rules.remove(rule);
            }
        }
    }


    /**  Remove the left recursion of the language */
    public void removeLeftRecursion() {
        // A → Aa
        // A → B
        List<GrammarSymbol> leftRecursiveVariables = this.getLeftRecursiveVariables();
        List<Rule> newRules = new ArrayList<>();
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

    /** Compute first1 set of all symbols */
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

    /** Compute follow1 set of all variables */
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

    /** Performs some basic checks to see if the grammar is LL1. No garentees! */
    public boolean check() {
        if (hasRulesWithCommonPrefix() != null) {
            System.err.println("The grammar has some rules with a common prefix\nYou should use the --ll argument");
            return false;
        }
        return true;
    }

    /** return a latex tabular to display first and follow sets */
    public String firstFollowToLatex() {
        StringBuilder sb = new StringBuilder();
        List<GrammarSymbol> symbols = new ArrayList<>();
        symbols.addAll(getVariables());
        symbols.sort(null);

        sb.append("\\begin{longtable}{r p{7cm} p{7cm}}\n");
        sb.append("\\textnormal{Symbol} $A$ & $First^1(A)$ & $Follow^1(A)$");
        for (GrammarSymbol symbol : symbols) {
            sb.append("\\\\ \\hline\n");
            sb.append(symbol.toString());
            sb.append(" & ");
            Set<GrammarSymbol> f = first.get(symbol);
            for (GrammarSymbol fs : f) {
                sb.append(fs.toString());
                sb.append(" ");
            }
            sb.append(" & ");
            f = follow.get(symbol);
            for (GrammarSymbol fs : f) {
                sb.append(fs.toString());
                sb.append(" ");
            }
        }

        sb.append("\n\\end{longtable}\n");
        return sb.toString();
    }

    public void saveLatexFirstFollowToFile(final String path) {
        PrintWriter writer;
        try {
            writer = new PrintWriter(path, "UTF-8");
            writer.println(this.firstFollowToLatex());
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
