package generator;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lexer.Symbol;
import parser.Node;

/**
* Imp-to-LLVM code generator
*
* @author Antoine Passemiers
* @author Alexis Reynouard
*/
public class CodeGenerator {

    private HashMap<Integer, Symbol> identifiers;
    private List<Symbol> tokens;
    private TemplateEngine templateEngine;

    private Integer nUnnamedVariables;

    public CodeGenerator(final String templateFilepath) {
        this.templateEngine = new TemplateEngine(templateFilepath);
        this.tokens = null;
    }

    private Symbol consumeOneToken() {
        Symbol token = this.tokens.get(0);
        this.tokens.remove(0);
        return token;
    }

    public void generate(final List<Symbol> tokens, final Node parseTree,
            final HashMap<Integer, Symbol>  identifiers, final String filepath)
            throws FileNotFoundException, UnsupportedEncodingException {
        this.templateEngine.init();
        this.identifiers = identifiers;
        allocateVariables();
        this.nUnnamedVariables = 2;
        this.tokens = new ArrayList<Symbol>(tokens);
        generateFromProgram(parseTree);

        PrintWriter writer = new PrintWriter(filepath, "UTF-8");
        writer.println(this.templateEngine.finish());
        writer.close();
    }

    private void allocateVariables() {
        this.templateEngine.oneLineComment("Allocate memory for Imp variables");
        for (Integer index : identifiers.keySet()) {
            String identifierName = (String) identifiers.get(index).getValue();
            this.templateEngine.insert(llvmVarName(identifierName) + " = alloca i32");
            this.templateEngine.newLine();
        }
        this.templateEngine.newLine();
    }

    private void generateFromProgram(final Node node) {
        consumeOneToken(); // begin TODO: check ?
        generateFromCode(node.getChildren().get(1));
        consumeOneToken(); // end
    }

    private void generateFromCode(final Node node) {
        if (node.getChildren().get(0).getSymbol().withoutChevrons().equals("InstList")) {
            generateFromInstList(node.getChildren().get(0));
        }
    }

    private void generateFromInstList(final Node node) {
        generateFromInstruction(node.getChildren().get(0));
        this.templateEngine.newLine();
        generateFromInstListTail(node.getChildren().get(1));
    }

    private void generateFromInstruction(final Node node) {
        assert(node.getChildren().size() == 1);
        String instructionName = node.getChildren().get(0).getSymbol().withoutChevrons();
        if (instructionName.equals("Assign")) {
            generateFromAssign(node.getChildren().get(0));
        } else if (instructionName.equals("If")) {
            // TODO
        } else if (instructionName.equals("While")) {
            // TODO
        } else if (instructionName.equals("For")) {
            // TODO
        } else if (instructionName.equals("Print")) {
            generateFromPrint(node.getChildren().get(0));
        } else if (instructionName.equals("Read")) {
            generateFromRead(node.getChildren().get(0));
        }
    }

    private void generateFromAssign(final Node node) {
        assert(node.getChildren().size() == 4);
        String varName = (String) consumeOneToken().getValue(); // [VarName]
        consumeOneToken(); // :=
        this.templateEngine.oneLineComment(varName + " := stuff"); // TODO: print Imp instruction
        String tempVarName = generateFromExprArithP0(node.getChildren().get(2));
        this.templateEngine.insert("store i32 " + tempVarName + ", i32* " + llvmVarName(varName));
    }

    private void generateFromRead(final Node node) {
        assert(node.getChildren().size() == 4);
        consumeOneToken(); // Read
        consumeOneToken(); // (
        String varName = (String) consumeOneToken().getValue();
        this.templateEngine.oneLineComment("Read ( " + varName + " ) ");
        String tempVarName = llvmVarName(String.valueOf(this.nUnnamedVariables++));
        this.templateEngine.insert(tempVarName + " = call i32 @readInt()");
        this.templateEngine.newLine();
        this.templateEngine.insert("store i32 " + tempVarName + ", i32* " + llvmVarName(varName));
        consumeOneToken(); // )
    }

    private void generateFromPrint(final Node node) {
        assert(node.getChildren().size() == 4);
        consumeOneToken(); // Print
        consumeOneToken(); // (
        String varName = (String) consumeOneToken().getValue();
        String tempVarName = llvmVarName(String.valueOf(this.nUnnamedVariables++));
        this.templateEngine.oneLineComment("Print ( " + varName + " ) ");
        this.templateEngine.insert(tempVarName + " = load i32, i32* " + llvmVarName(varName));
        this.templateEngine.newLine();
        String instruction = "call void @println(i32 " + tempVarName + ")";
        this.templateEngine.insert(instruction);
        consumeOneToken(); // )
    }

    private void generateFromInstListTail(final Node node) {
        if (node.getChildren().size() > 1) {
            consumeOneToken(); // ;
            this.templateEngine.newLine();
            generateFromInstList(node.getChildren().get(1));
        }
    }

    private String generateFromExprArithP0(final Node node) {
        String tempVarName = generateFromExprArithP0I(node.getChildren().get(0));
        generateFromExprArithP0J(node.getChildren().get(1));
        return tempVarName; 
        // TODO: assignation: (a := 4 + 8) will yield %a
    }

    private String generateFromExprArithP0I(final Node node) {
        return generateFromExprArithP1(node.getChildren().get(0));
    }

    private String generateFromExprArithP0J(final Node node) {
        if (node.getChildren().size() > 1) {
            generateFromOpP0(node.getChildren().get(0));
            return generateFromExprArithP1(node.getChildren().get(1));
        }
        return null; // TODO
    }

    private String generateFromExprArithP1(final Node node) {
        String tempVarName = generateFromExprArithP1I(node.getChildren().get(0));
        generateFromExprArithP1J(node.getChildren().get(1));
        return tempVarName;
        // return null; // TODO: Must return the name of the variable to be used for assignation: (a := 4 + 8) will yield %a
    }

    private String generateFromOpP0(final Node node) {
        return null; // TODO
    }

    private String generateFromExprArithP1I(final Node node) {
        String tempVarName = generateFromAtom(node.getChildren().get(0));
        return tempVarName;
    }

    private String generateFromExprArithP1J(final Node node) {
        if (node.getChildren().size() > 1) {
            generateFromOpP1(node.getChildren().get(0));
            String tempVarName = generateFromAtom(node.getChildren().get(1));
            return tempVarName;
        }
        return null; // TODO
    }

    private String generateFromOpP1(final Node node) {
        return null; // TODO
    }

    private String generateFromAtom(final Node node) {
        String symbolName = node.getChildren().get(0).getSymbol().withoutChevrons();
        String tempVarName;
        if (symbolName.equals("[VarName]")) {
            tempVarName = null; // TODO
        } else if (symbolName.equals("[Number]")) {
            tempVarName = llvmVarName(String.valueOf(this.nUnnamedVariables++));
            Integer number = Integer.parseInt((String) consumeOneToken().getValue());
            String instruction = tempVarName + " = add i32 0, " + number;
            this.templateEngine.insert(instruction);
            this.templateEngine.newLine();
        } else if (symbolName.equals("(")) {
            consumeOneToken(); // (
            tempVarName = generateFromExprArithP0(node.getChildren().get(1));
            consumeOneToken(); // )
        } else if (symbolName.equals("-")) {
            consumeOneToken(); // -
            String atomVarName = generateFromAtom(node.getChildren().get(1));
            tempVarName = llvmVarName(String.valueOf(this.nUnnamedVariables++));
            String instruction = tempVarName + " = sub i32 0, " + atomVarName;
            this.templateEngine.insert(instruction);
            this.templateEngine.newLine();
        } else {
            tempVarName = null; // TODO: raise exception
        }
        return tempVarName;
    }

    private String llvmVarName(final String varName) {
        return "%" + varName;
    }
}
