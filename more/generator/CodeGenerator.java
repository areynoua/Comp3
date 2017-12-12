package generator;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lexer.LexicalUnit;
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
    private Integer nConditions;

    public CodeGenerator(final String templateFilepath) {
        this.templateEngine = new TemplateEngine(templateFilepath);
        this.tokens = null;
    }

    private Symbol consumeOneToken() {
        Symbol token = this.tokens.get(0);
        this.tokens.remove(0);
        return token;
    }

    private Symbol consumeOneToken(LexicalUnit type) {
        Symbol token = this.tokens.get(0);
        if (type != token.getType()) {
            // TODO: raise exception
        }
        this.tokens.remove(0);
        return token;
    }

    public void generate(final List<Symbol> tokens, final Node parseTree,
            final HashMap<Integer, Symbol>  identifiers, final String filepath)
            throws FileNotFoundException, UnsupportedEncodingException {
        this.templateEngine.init();
        this.identifiers = identifiers;
        allocateVariables();
        this.nUnnamedVariables = 1;
        this.nConditions = 0;
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
        consumeOneToken(LexicalUnit.BEGIN);
        generateFromCode(node.getChildren().get(1));
        consumeOneToken(LexicalUnit.END);
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
        String instructionName = node.getChildren().get(0).getSymbol().withoutChevrons();
        if (instructionName.equals("Assign")) {
            generateFromAssign(node.getChildren().get(0));
        } else if (instructionName.equals("If")) {
            generateFromIf(node.getChildren().get(0));
        } else if (instructionName.equals("While")) {
            generateFromWhile(node.getChildren().get(0));
        } else if (instructionName.equals("For")) {
            generateFromFor(node.getChildren().get(0));
        } else if (instructionName.equals("Print")) {
            generateFromPrint(node.getChildren().get(0));
        } else if (instructionName.equals("Read")) {
            generateFromRead(node.getChildren().get(0));
        }
    }

    private void generateFromAssign(final Node node) {
        String varName = (String) consumeOneToken(LexicalUnit.VARNAME).getValue();
        consumeOneToken(LexicalUnit.ASSIGN);
        this.templateEngine.oneLineComment(varName + " := stuff"); // TODO: print Imp instruction
        String tempVarName = generateFromExprArithP0(node.getChildren().get(2));
        this.templateEngine.insert("store i32 " + tempVarName + ", i32* " + llvmVarName(varName));
    }

    private void generateFromRead(final Node node) {
        consumeOneToken(LexicalUnit.READ);
        consumeOneToken(LexicalUnit.LPAREN);
        String varName = (String) consumeOneToken(LexicalUnit.VARNAME).getValue();
        this.templateEngine.oneLineComment("Read ( " + varName + " ) ");
        String tempVarName = llvmVarName(String.valueOf(this.nUnnamedVariables++));
        this.templateEngine.insert(tempVarName + " = call i32 @readInt()");
        this.templateEngine.newLine();
        this.templateEngine.insert("store i32 " + tempVarName + ", i32* " + llvmVarName(varName));
        consumeOneToken(LexicalUnit.RPAREN);
    }

    private void generateFromPrint(final Node node) {
        consumeOneToken(LexicalUnit.PRINT);
        consumeOneToken(LexicalUnit.LPAREN);
        String varName = (String) consumeOneToken(LexicalUnit.VARNAME).getValue();
        String tempVarName = llvmVarName(String.valueOf(this.nUnnamedVariables++));
        this.templateEngine.oneLineComment("Print ( " + varName + " ) ");
        this.templateEngine.insert(tempVarName + " = load i32, i32* " + llvmVarName(varName));
        this.templateEngine.newLine();
        String instruction = "call void @println(i32 " + tempVarName + ")";
        this.templateEngine.insert(instruction);
        consumeOneToken(LexicalUnit.RPAREN);
    }

    private void generateFromIf(final Node node) {
        String condName = "if.n" + String.valueOf(++this.nConditions);
        String trueLabelName = condName + ".true";
        String falseLabelName;
        if (node.getChildren().get(4).getChildren().size() > 1) {
            falseLabelName = condName + ".false";
        } else {
            falseLabelName = condName;
        }
        consumeOneToken(LexicalUnit.IF);

        String condVarName = generateFromCondP0(node.getChildren().get(1));
        String instruction = "br i1 " + condVarName + ", label %" + trueLabelName + ", label %" + falseLabelName;
        this.templateEngine.insert(instruction);
        this.templateEngine.newLine();

        consumeOneToken(LexicalUnit.THEN);
        this.templateEngine.addLabel(trueLabelName);
        generateFromCode(node.getChildren().get(3));
        this.templateEngine.insert("br label %" + condName);
        this.templateEngine.newLine();
        generateFromIfTail(node.getChildren().get(4));
        this.templateEngine.addLabel(condName);
    }

    private void generateFromWhile(final Node node) {
        String condName = "while.n" + String.valueOf(++this.nConditions) + ".cond";
        String trueLabelName = condName + ".body";
        String falseLabelName = condName + ".end";
        this.templateEngine.insert("br label %" + condName);
        this.templateEngine.newLine();
        consumeOneToken(LexicalUnit.WHILE);
        this.templateEngine.addLabel(condName);
        String condVarName = generateFromCondP0(node.getChildren().get(1));
        String instruction = "br i1 " + condVarName + ", label %" + trueLabelName + ", label %" + falseLabelName;
        this.templateEngine.insert(instruction);
        this.templateEngine.newLine();
        consumeOneToken(LexicalUnit.DO);
        this.templateEngine.addLabel(trueLabelName);
        generateFromCode(node.getChildren().get(3));
        this.templateEngine.insert("br label %" + condName);
        this.templateEngine.newLine();
        consumeOneToken(LexicalUnit.DONE);
        this.templateEngine.addLabel(falseLabelName);
    }

    private void generateFromFor(final Node node) {
        consumeOneToken(LexicalUnit.FOR);
        // TODO: [VarName]
        consumeOneToken(LexicalUnit.FROM);
        // TODO: exprarith-p0
        // TODO: for-tail
    }

    private void generateFromForTail(final Node node) {
        if (node.getChildren().size() > 5) {
            consumeOneToken(LexicalUnit.BY);
            // TODO: exprarith-p0
            consumeOneToken(LexicalUnit.TO);
            // TODO: exprarith-p0
            consumeOneToken(LexicalUnit.DO);
            generateFromCode(node.getChildren().get(5));
            consumeOneToken(LexicalUnit.DONE);
        } else {
            consumeOneToken(LexicalUnit.TO);
            // TODO: exprarith-p0
            consumeOneToken(LexicalUnit.DO);
            generateFromCode(node.getChildren().get(5));
            consumeOneToken(LexicalUnit.DONE);
        }
    }

    private String generateFromCondP0(final Node node) {
        String tempVarName = generateFromCondP0I(node.getChildren().get(0));
        return generateFromCondP0J(node.getChildren().get(1), tempVarName);
    }

    private String generateFromCondP0I(final Node node) {
        return generateFromCondP1(node.getChildren().get(0));
    }

    private String generateFromCondP0J(final Node node, final String leftVarName) {
        if (node.getChildren().size() > 1) {
            String rightVarName = generateFromExprArithP1(node.getChildren().get(1));
            String tempVarName = llvmVarName(String.valueOf(this.nUnnamedVariables++));
            String instruction = tempVarName + " = or i32 " + leftVarName + ", " + rightVarName;
            this.templateEngine.insert(instruction);
            this.templateEngine.newLine();
            return tempVarName;
        } else {
            return leftVarName;
        }
    }

    private String generateFromCondP1(final Node node) {
        String tempVarName = generateFromCondP1I(node.getChildren().get(0));
        return generateFromCondP1J(node.getChildren().get(1), tempVarName);
    }

    private String generateFromCondP1I(final Node node) {
        return generateFromCondP2(node.getChildren().get(0));
    }

    private String generateFromCondP1J(final Node node, final String leftVarName) {
        if (node.getChildren().size() > 1) {
            String rightVarName = generateFromCondP2(node.getChildren().get(1));
            String tempVarName = llvmVarName(String.valueOf(this.nUnnamedVariables++));
            String instruction = tempVarName + " = and i32 " + leftVarName + ", " + rightVarName;
            this.templateEngine.insert(instruction);
            this.templateEngine.newLine();
            return tempVarName;
        } else {
            return leftVarName;
        }
    }

    private String generateFromCondP2(final Node node) {
        if (node.getChildren().size() > 1) {
            return generateFromSimpleCond(node.getChildren().get(1));
        } else {
            return generateFromSimpleCond(node.getChildren().get(0));
        }
    }

    private String generateFromSimpleCond(final Node node) {
        String leftVarName = generateFromExprArithP0(node.getChildren().get(0));
        String opName = generateFromComp(node.getChildren().get(1));
        String rightVarName = generateFromExprArithP0(node.getChildren().get(2));
        String tempVarName = llvmVarName(String.valueOf(this.nUnnamedVariables++));
        String instruction = null;
        if (opName.equals("=")) {
            instruction = tempVarName + " = icmp eq i32 " + leftVarName + ", " + rightVarName;
        } else if (opName.equals(">=")) {
            instruction = tempVarName + " = icmp sge i32 " + leftVarName + ", " + rightVarName;
        } else if (opName.equals(">")) {
            instruction = tempVarName + " = icmp sgt i32 " + leftVarName + ", " + rightVarName;
        } else if (opName.equals("<=")) {
            instruction = tempVarName + " = icmp sle i32 " + leftVarName + ", " + rightVarName;
        } else if (opName.equals("<")) {
            instruction = tempVarName + " = icmp slt i32 " + leftVarName + ", " + rightVarName;
        } else if (opName.equals("<>")) {
            instruction = tempVarName + " = icmp ne i32 " + leftVarName + ", " + rightVarName;
        } else {
            // TODO: raise exception
        }
        this.templateEngine.insert(instruction);
        this.templateEngine.newLine();
        return tempVarName;
    }

    private String generateFromComp(final Node node) {
        return (String) consumeOneToken().getValue();
    }

    private void generateFromIfTail(final Node node) {
        if (node.getChildren().size() > 1) {
            String condName = "if.n" + String.valueOf(this.nConditions);
            String falseLabelName = condName + ".false";
            consumeOneToken(LexicalUnit.ELSE);
            this.templateEngine.addLabel(falseLabelName);
            generateFromCode(node.getChildren().get(1));
            consumeOneToken(LexicalUnit.ENDIF);
            this.templateEngine.insert("br label %" + condName);
            this.templateEngine.newLine();
        } else {
            consumeOneToken(LexicalUnit.ENDIF);
        }
    }

    private void generateFromInstListTail(final Node node) {
        if (node.getChildren().size() > 1) {
            consumeOneToken(LexicalUnit.SEMICOLON);
            this.templateEngine.newLine();
            generateFromInstList(node.getChildren().get(1));
        }
    }

    private String generateFromExprArithP0(final Node node) {
        String tempVarName = generateFromExprArithP0I(node.getChildren().get(0));
        return generateFromExprArithP0J(node.getChildren().get(1), tempVarName);
    }

    private String generateFromExprArithP0I(final Node node) {
        return generateFromExprArithP1(node.getChildren().get(0));
    }

    private String generateFromExprArithP0J(final Node node, final String leftVarName) {
        if (node.getChildren().size() > 1) {
            String opName = generateFromOpP0(node.getChildren().get(0));
            String rightVarName = generateFromExprArithP1(node.getChildren().get(1));
            String tempVarName = llvmVarName(String.valueOf(this.nUnnamedVariables++));
            String instruction = null;
            if (opName.equals("+")) {
                instruction = tempVarName + " = add i32 " + leftVarName + ", " + rightVarName;
            } else if (opName.equals("-")) {
                instruction = tempVarName + " = sub i32 " + leftVarName + ", " + rightVarName;
            } else {
                // TODO: raise exception
            }
            this.templateEngine.insert(instruction);
            this.templateEngine.newLine();
            return tempVarName;
        } else {
            return leftVarName;
        }
    }

    private String generateFromExprArithP1(final Node node) {
        String tempVarName = generateFromExprArithP1I(node.getChildren().get(0));
        tempVarName = generateFromExprArithP1J(node.getChildren().get(1), tempVarName);
        return tempVarName;
    }

    private String generateFromOpP0(final Node node) {
        return (String) consumeOneToken().getValue();
    }

    private String generateFromExprArithP1I(final Node node) {
        return generateFromAtom(node.getChildren().get(0));
    }

    private String generateFromExprArithP1J(final Node node, final String leftVarName) {
        if (node.getChildren().size() > 1) {
            String opName = generateFromOpP1(node.getChildren().get(0));
            String rightVarName = generateFromAtom(node.getChildren().get(1));
            String tempVarName = llvmVarName(String.valueOf(this.nUnnamedVariables++));
            String instruction = null;
            if (opName.equals("*")) {
                instruction = tempVarName + " = mul i32 " + leftVarName + ", " + rightVarName;
            } else if (opName.equals("/")) {
                instruction = tempVarName + " = sdiv i32 " + leftVarName + ", " + rightVarName;
            } else {
                // TODO: raise exception
            }
            this.templateEngine.insert(instruction);
            this.templateEngine.newLine();
            return tempVarName;
        } else {
            return leftVarName;
        }
    }

    private String generateFromOpP1(final Node node) {
        return (String) consumeOneToken().getValue();
    }

    private String generateFromAtom(final Node node) {
        String symbolName = node.getChildren().get(0).getSymbol().withoutChevrons();
        String tempVarName;
        if (symbolName.equals("[VarName]")) {
            tempVarName = llvmVarName(String.valueOf(this.nUnnamedVariables++));
            String varName = (String) consumeOneToken(LexicalUnit.VARNAME).getValue();
            String instruction = tempVarName + " = load i32, i32* " + llvmVarName(varName);
            this.templateEngine.insert(instruction);
            this.templateEngine.newLine();
        } else if (symbolName.equals("[Number]")) {
            tempVarName = llvmVarName(String.valueOf(this.nUnnamedVariables++));
            Integer number = Integer.parseInt((String) consumeOneToken(LexicalUnit.NUMBER).getValue());
            String instruction = tempVarName + " = add i32 0, " + number;
            this.templateEngine.insert(instruction);
            this.templateEngine.newLine();
        } else if (symbolName.equals("(")) {
            consumeOneToken(LexicalUnit.LPAREN);
            tempVarName = generateFromExprArithP0(node.getChildren().get(1));
            consumeOneToken(LexicalUnit.RPAREN);
        } else if (symbolName.equals("-")) {
            consumeOneToken(LexicalUnit.MINUS);
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
