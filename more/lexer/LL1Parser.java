/**
* Imp LL(1) Parser
*
* @author Antoine Passemiers
* @author Alexis Reynouard
*/

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class LL1Parser {

    private Integer cursor; // Points to the current token from the input token list
    private Stack<GrammarSymbol> stack; // PDA stack
    private L1Grammar grammar;
    private ActionTable actionTable; // LL 1 parser's action table

    public LL1Parser(L1Grammar grammar) {
        this.grammar = grammar;
        this.actionTable = new ActionTable(grammar);
        resetPDA();
    }

    public void parse(List<Symbol> tokens) {
        resetPDA();
        List<Integer> rulesUsed = new ArrayList<>();
        List<GrammarSymbol> symbols = symbolsToGrammarSymbols(tokens);
        GrammarSymbol startVariable = grammar.getRules().get(0).getLeftVariable();
        stack.push(startVariable);
        while (!stack.empty()) {
            GrammarSymbol tos = stack.peek(); // top of stack
            GrammarSymbol symbol = symbols.get(cursor);
            Integer action = actionTable.get(tos, symbol);
            if (action == ActionTable.MATCH) {
                match();
            } else if (action == ActionTable.ACCEPT) {
                accept();
            } else if (action == ActionTable.ERROR) {
                error();
            } else {
                produce(action);
                rulesUsed.add(action);
            }
        }
    }

    private void produce(Integer ruleId) {
        stack.pop();
        Rule rule = grammar.getRules().get(ruleId);
        Integer nSymbols = rule.getRightSymbols().size();
        for (int i = nSymbols - 1; i > -1; i--) {
            stack.push(rule.getRightSymbols().get(i));
        }
    }

    private void match() {
        stack.pop();
        cursor++;
    }

    private void accept() {
        System.out.println("The sequence of tokens has been accepted");
    }

    private void error() {
        System.out.println("There is a syntax error in the input program");
    }

    private void resetPDA() {
        stack = new Stack<>();
        cursor = 0;
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
        conversions.put(LexicalUnit.EOS, "$");

        for (Symbol symbol : symbols) {
            grammarSymbols.add(new GrammarSymbol(conversions.get(symbol.getType())));
        }
        return grammarSymbols;
    }
}