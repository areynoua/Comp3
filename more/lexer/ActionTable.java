/**
* Imp LL(1) Parser's action table
*
* @author Antoine Passemiers
* @author Alexis Reynouard
*/

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class ActionTable {

    private List<Rule> rules;
    
    public ActionTable() {
        rules = new ArrayList<>();

        rules.add(new Rule(
            new GrammarSymbol("Program"), 
            Arrays.asList(
                new GrammarSymbol(LexicalUnit.BEGIN), 
                new GrammarSymbol("Code"), 
                new GrammarSymbol(LexicalUnit.END))));

        rules.add(new Rule(
            new GrammarSymbol("Code"),
            Arrays.asList(
                new GrammarSymbol("Epsilon"))));

        rules.add(new Rule(
            new GrammarSymbol("Code"),
            Arrays.asList(
                new GrammarSymbol("InstList"))));

        rules.add(new Rule(
            new GrammarSymbol("InstList"),
            Arrays.asList(
                new GrammarSymbol("Instruction"))));

        rules.add(new Rule(
            new GrammarSymbol("InstList"),
            Arrays.asList(
                new GrammarSymbol("Instruction"),
                new GrammarSymbol(LexicalUnit.SEMICOLON),
                new GrammarSymbol("InstList"))));

        // TODO: etc....
    }

    public Integer getRule(GrammarSymbol variable, GrammarSymbol terminal) {
        // TODO: return cell M[A, u]
        // A is the variable on top of the stack
        // u is the look-ahead token
        return 0; // TODO
    }
}