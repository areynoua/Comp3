/**
* Imp LL(1) Parser
*
* @author Antoine Passemiers
* @author Alexis Reynouard
*/

import java.util.List;
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

    private void resetPDA() {
        stack = new Stack<>();
        cursor = 0;
    }

    public void parse(List<Symbol> tokens) {
        resetPDA();
        stack.push(new GrammarSymbol("Program")); // Pushing start variable
        while (true) {
            GrammarSymbol top = stack.peek();
            // TODO: predictive parser algorithm
        }
    }

    private void produce(Integer ruleId) {
        // TODO: pop stack and push token
    }

    private void match() {
        // TODO: pop stack and shift to next token
    }

    private void accept() {
        // TODO: accept string
    }

    private void error() {
        // TODO: throw error
    }
}