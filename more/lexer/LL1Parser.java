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
    private Stack<Symbol> stack; // PDA stack
    private ActionTable actionTable; // LL 1 parser's action table

    public LL1Parser() {
        this.actionTable = new ActionTable();
        this.resetPDA();
    }

    private void resetPDA() {
        this.stack = new Stack<>();
        this.cursor = 0;
    }

    public void parse(List<Symbol> tokens) {
        this.resetPDA();
        System.out.println("Parsing list of tokens...");
        // TODO: predictive parsing algorithm
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