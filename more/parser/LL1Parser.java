package parser;
/**
* Imp LL(1) Parser
*
* @author Antoine Passemiers
* @author Alexis Reynouard
*/

import java.util.Collections;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.StringJoiner;

import lexer.LexicalUnit;
import lexer.Symbol;

public class LL1Parser {

    private Integer cursor; // Points to the current token from the input token list
    private Stack<Node> stack; // PDA stack
    private LL1Grammar grammar;
    private ActionTable actionTable; // LL 1 parser's action table
    private List<Integer> rulesUsed; // Output
    private Node root; // Root of parse tree
    private Integer numNodes;
    private boolean isSyntaxCorrect;
    private List<Symbol> tokens; // debug purpose

    public LL1Parser(LL1Grammar grammar) {
        this.grammar = grammar;
        this.actionTable = new ActionTable(grammar);
        resetPDA();
    }

    public boolean parse(List<Symbol> tokens) {
        if (!this.grammar.check()) {
            System.exit(1);
        }
        resetPDA();
        this.tokens = tokens;
        List<GrammarSymbol> symbols = symbolsToGrammarSymbols(tokens);
        GrammarSymbol startVariable = grammar.getRules().get(0).getLeftVariable();
        numNodes = 0;
        root = new Node(startVariable, ++numNodes);
        Node currentNode = root;
        stack.push(new Node(GrammarSymbol.EOS, ++numNodes));
        stack.push(root);
        while (!stack.empty()) {
            currentNode = stack.peek(); // top of stack
            GrammarSymbol tos = currentNode.getSymbol();
            GrammarSymbol symbol = symbols.get(cursor);
            Integer action = actionTable.get(tos, symbol);

            if (action == ActionTable.MATCH) {
                tos = match();
            } else if (action == ActionTable.ACCEPT) {
                accept();
                return true;
            } else if (action == ActionTable.ERROR) {
                error();
                return false;
            } else {
                List<Node> nodes = produce(action);
                for (Node node : nodes) {
                    currentNode.addChild(node);
                }
                rulesUsed.add(action);
            }
        }
        return false;
    }

    private List<Node> produce(Integer ruleId) {
        stack.pop();
        List<Node> nodes = new ArrayList<>();
        Rule rule = grammar.getRules().get(ruleId);
        Integer offset = numNodes;
        Integer nSymbols = rule.getRightSymbols().size();
        for (int i = nSymbols - 1; i > -1; i--) {
            if (!rule.getRightSymbols().get(i).equals(GrammarSymbol.EPSILON)) {
                Node currentNode = new Node(rule.getRightSymbols().get(i), offset + i);
                stack.push(currentNode);
                nodes.add(currentNode);
                ++numNodes;
            }
        }
        return nodes;
    }

    private GrammarSymbol match() {
        GrammarSymbol tos = stack.pop().getSymbol();
        ++cursor;
        return tos;
    }

    private void accept() {
        isSyntaxCorrect = true;
        stack.pop();
    }

    private void error() {
        isSyntaxCorrect = false;
    }

    private void resetPDA() {
        stack = new Stack<>();
        cursor = 0;
        rulesUsed = new ArrayList<>(); // output
        root = null;
        numNodes = 0;
    }

    public Node getParseTree() {
        return root;
    }

    @Override
    public String toString() {
        StringJoiner stackSj = new StringJoiner("', '", "[ '", "' ]"); 
        Stack<Node> stack = (Stack<Node>) this.stack.clone();
        while (!stack.empty()) {
            stackSj.add(stack.pop().getSymbol().toString());
        }

        StringJoiner sj = new StringJoiner("\n    ","LL1Parser:\n    ","\n");
        sj.add("cursor: " + this.cursor)
          .add("stack:  " + stackSj.toString())
          .add("token:  " + this.tokens.get(this.cursor));

        return sj.toString();
    }

    public String rulesUsedToString() {
        return rulesUsed.toString();
    }

    public String toTextTree() {
        StringBuilder sb = new StringBuilder();
        Stack<Integer> levels = new Stack<>();
        int ruleUsedIdx = 0;

        levels.push(ruleUsedIdx);
        levels.push(rulesUsed.get(ruleUsedIdx));
        String varName = grammar.getRules().get(rulesUsed.get(ruleUsedIdx)).getLeftVariable().toString();
        sb.append(varName.substring(1, varName.length() - 1));
        sb.append("\n");

        while (!levels.empty()) {
            int level = levels.size() / 2;
            sb.append(String.join("", Collections.nCopies(level, "| ")));
            int ruleId = levels.pop();
            Rule rule = grammar.getRules().get(ruleId);
            int position = levels.pop();
            GrammarSymbol symbol = rule.getRightSymbols().get(position);

            if (symbol.isTerminal()) {
                sb.append("+ ");
                sb.append(symbol.toString());
                sb.append("\n");
            }
            else {
                sb.append("+ ");
                sb.append(symbol.toString());
                sb.append("\n");
            }
            if (position < rule.getRightSymbols().size() - 1) {
                levels.push(position+1);
                levels.push(ruleId);
            }
            if (!symbol.isTerminal()) {
                levels.push(0);
                levels.push(rulesUsed.get(++ruleUsedIdx));
            }
        }
        sb.append("$");
        return sb.toString();
    }

    public String toLatexTree() {
        // !! Tikz limit to 255 levels
        int limit = 255;
        StringBuilder sb = new StringBuilder();
        Stack<Integer> levels = new Stack<>();
        int ruleUsedIdx = 0;

        levels.push(ruleUsedIdx);
        levels.push(rulesUsed.get(ruleUsedIdx));
        sb.append("\\begin{tikzpicture}");
        sb.append("[level 1/.style={sibling distance=45mm},level 2/.style={sibling distance=45mm},\n");
        sb.append(" level 3/.style={sibling distance=45mm},level 4/.style={sibling distance=45mm},\n");
        sb.append(" level 5/.style={sibling distance=25mm},level 6/.style={sibling distance=25mm},\n");
        sb.append(" level 7/.style={sibling distance=20mm},level 8/.style={sibling distance=23mm}]");
        sb.append("\n\\node {");
        String varName = grammar.getRules().get(rulesUsed.get(ruleUsedIdx)).getLeftVariable().toString();
        sb.append(varName.substring(1, varName.length() - 1));
        sb.append("}\n");

        while (!levels.empty()) {
            int ruleId = levels.pop();
            Rule rule = grammar.getRules().get(ruleId);
            int position = levels.pop();
            if (position == rule.getRightSymbols().size() && !levels.empty()) {
                if (levels.size() < limit*2) sb.append("}\n");
            }
            if (position != rule.getRightSymbols().size()) {
                GrammarSymbol symbol = rule.getRightSymbols().get(position);
                if (levels.size() < limit*2) sb.append("child { node {");

                if (symbol.isTerminal()) {
                    if (levels.size() < limit*2) {
                        sb.append("$");
                        sb.append(symbol.toString());
                        sb.append("$");
                        sb.append("}}\n");
                    }
                }
                else {
                    if (levels.size() < limit*2) {
                        if (levels.size() < (limit-1)*2) {
                            sb.append("$<$");
                            sb.append(symbol.toString().substring(1,symbol.toString().length()-1));
                            sb.append("$>$");
                            sb.append("}\n");
                        }
                        else {
                            sb.append("...}}\n");
                        }
                    }
                }
                if (position < rule.getRightSymbols().size()) {
                    levels.push(position+1);
                    levels.push(ruleId);
                }
                if (!symbol.isTerminal()) {
                    levels.push(0);
                    levels.push(rulesUsed.get(++ruleUsedIdx));
                }
            }
        }
        sb.append(";\n\\end{tikzpicture}");
        return sb.toString();
    }

    public void saveLatexTreeToFile(final String path) {
        PrintWriter writer;
        try {
            writer = new PrintWriter(path, "UTF-8");
            writer.println("\\documentclass[a4paper]{article}\n\\usepackage{tikz}\n\\begin{document}");
            writer.println(this.toLatexTree());
            writer.println("\\end{document}");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void saveJavascriptToFile(final String path) {
        StringBuilder sb = new StringBuilder();
        sb.append("var nodeDataArray = [\n");
        if (isSyntaxCorrect) {
            Stack<Node> stack = new Stack<>();
            Node currentNode = root;
            stack.push(root);
            while (!stack.empty()) {
                currentNode = stack.pop();
                sb.append("  { key: " + currentNode.getId() + ", text: \"" + currentNode.getSymbol().withoutChevrons() + "\", ");
                if (!currentNode.getSymbol().equals(GrammarSymbol.EPSILON)) {
                    sb.append("fill: \"#f68c06\", stroke: \"#4d90fe\" ");
                } else {
                    sb.append("fill: \"#f8f8f8\", stroke: \"#4d90fe\" ");
                }
                if (currentNode.getParent() != null) sb.append(", parent: " + currentNode.getParent().getId());
                sb.append("},\n");
                for (Node child : currentNode.getChildren()) {
                    stack.push(child);
                }
                if (currentNode.isLeaf() && (!currentNode.getSymbol().isTerminal())) {
                    Node epsilonNode = new Node(GrammarSymbol.EPSILON, ++numNodes);
                    currentNode.addChild(epsilonNode);
                    stack.push(epsilonNode);
                }
            }
        }
        sb.append("]");
        PrintWriter writer;
        try {
            writer = new PrintWriter(path, "UTF-8");
            writer.print(sb.toString());
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void saveTextTreeToFile(final String path) {
        PrintWriter writer;
        try {
            writer = new PrintWriter(path, "UTF-8");
            writer.println(this.toTextTree());
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public String actionTableToString() {
        return this.actionTable.toString();
    }

    public void saveLatexActionTableToFile(final String path) {
        PrintWriter writer;
        try {
            writer = new PrintWriter(path, "UTF-8");
            writer.println(this.actionTable.toLatexString());
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    List<GrammarSymbol> symbolsToGrammarSymbols(List<Symbol> symbols) {
        List<GrammarSymbol> grammarSymbols = new ArrayList<>();
        Map<LexicalUnit, String> conversions = new EnumMap<LexicalUnit, String>(LexicalUnit.class);

        conversions.put(LexicalUnit.VARNAME, "[VarName]");
        conversions.put(LexicalUnit.NUMBER, "[Number]");
        conversions.put(LexicalUnit.BEGIN, "begin");
        conversions.put(LexicalUnit.END, "end");
        conversions.put(LexicalUnit.SEMICOLON, ";");
        conversions.put(LexicalUnit.ASSIGN, ":=");
        conversions.put(LexicalUnit.LPAREN, "(");
        conversions.put(LexicalUnit.RPAREN, ")");
        conversions.put(LexicalUnit.MINUS, "-");
        conversions.put(LexicalUnit.PLUS, "+");
        conversions.put(LexicalUnit.TIMES, "*");
        conversions.put(LexicalUnit.DIVIDE, "/");
        conversions.put(LexicalUnit.IF, "if");
        conversions.put(LexicalUnit.THEN, "then");
        conversions.put(LexicalUnit.ENDIF, "endif");
        conversions.put(LexicalUnit.ELSE, "else");
        conversions.put(LexicalUnit.NOT, "not");
        conversions.put(LexicalUnit.AND, "and");
        conversions.put(LexicalUnit.OR, "or");
        conversions.put(LexicalUnit.EQ, "eq");
        conversions.put(LexicalUnit.GEQ, ">=");
        conversions.put(LexicalUnit.GT, ">");
        conversions.put(LexicalUnit.LEQ, "<=");
        conversions.put(LexicalUnit.LT, "<");
        conversions.put(LexicalUnit.NEQ, "<>");
        conversions.put(LexicalUnit.WHILE, "while");
        conversions.put(LexicalUnit.DO, "do");
        conversions.put(LexicalUnit.DONE, "done");
        conversions.put(LexicalUnit.FOR, "for");
        conversions.put(LexicalUnit.FROM, "from");
        conversions.put(LexicalUnit.BY, "by");
        conversions.put(LexicalUnit.TO, "to");
        conversions.put(LexicalUnit.PRINT, "print");
        conversions.put(LexicalUnit.READ, "read");
        conversions.put(LexicalUnit.RAND, "rand");
        conversions.put(LexicalUnit.FUNCTION, "function");
        conversions.put(LexicalUnit.FUNCNAME, "[FuncName]");
        conversions.put(LexicalUnit.RETURN, "return");
        conversions.put(LexicalUnit.IMPORT, "import");
        conversions.put(LexicalUnit.MODULENAME, "[ModuleName]");
        conversions.put(LexicalUnit.COMMA, ",");
        conversions.put(LexicalUnit.EOS, "$");

        for (Symbol symbol : symbols) {
            GrammarSymbol grammarSymbol = new GrammarSymbol(conversions.get(symbol.getType()));
            grammarSymbols.add(grammarSymbol);
        }
        grammarSymbols.add(GrammarSymbol.EOS);
        return grammarSymbols;
    }
}
