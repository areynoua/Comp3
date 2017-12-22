package generator;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

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

    private HashMap<Integer, Symbol> identifiers; // Symbol table received from the lexer
    private Set<String> definedFunctionNames; // Set of user-defined function names
    private List<Symbol> tokens; // List of tokens received from the lexer
    private TemplateEngine templateEngine; // Used to write to the outptut llvm file

    private Integer nUnnamedVariables; // Number of llvm unnamed variables currently used
    private Integer nConditions; // Number of llvm conditions currently generated
    private Integer nCalls; // Number of functions calls generated
    private boolean inFunc; // Indicates if the generator is inside a user-defined function or not

    private STDLibManager stdlibManager;

    /**
     * Initializes the recursive descent code generator. 
     *
     * @param templateFilepath  Path to the template file to use for generating
     *        llvm files
     */
    public CodeGenerator(final String templateFilepath) {
        this.templateEngine = new TemplateEngine(templateFilepath);
        this.stdlibManager = new STDLibManager();
        this.tokens = null;
    }

    /**
     * Removes the current token from the input buffer and returns it.
     *
     * @return Token located on the head of the input buffer
     */
    private Symbol consumeOneToken() {
        Symbol token = this.tokens.get(0);
        this.tokens.remove(0);
        return token;
    }

    /**
     * Removes the current token from the input buffer, ensures that it
     * has the specified type, and returns it.
     *
     * @return Token located on the head of the input buffer
     */
    private Symbol consumeOneToken(LexicalUnit type) {
        Symbol token = this.tokens.get(0);
        if (type != token.getType()) {
            System.out.println(token);
            // TODO: raise exception
        }
        this.tokens.remove(0);
        return token;
    }

    /**
     * Generates the body of the llvm code and passes it to the template engine.
     * It first allocates memory space for named variables and then generates
     * code by starting from the root of the parse tree.
     *
     * @param tokens List of tokens received from the lexer
     * @param parseTree Root of the parse tree corresponding to the given list
     *        of tokens (received from the parser)
     * @param identifiers Symbol table received from the lexer
     * @param filepath Path to the output llvm file
     */
    public void generate(final List<Symbol> tokens, final Node parseTree,
            final HashMap<Integer, Symbol>  identifiers, final String filepath)
            throws FileNotFoundException, UnsupportedEncodingException, UndefinedFunctionException {
        this.templateEngine.init();
        this.importModule("_random");
        this.importModule("_stdio");
        this.identifiers = identifiers;
        this.definedFunctionNames = new HashSet<String>();
        allocateVariables();
        this.nUnnamedVariables = 1; // Already one variable for the seed of the RNG
        this.nConditions = 0;
        this.nCalls = 0;
        this.inFunc = false;
        this.tokens = new ArrayList<Symbol>(tokens);

        generateFromProgram(parseTree); // Generates llvm from the root of the parse tree
        // Write to the output file after writing the code and
        // asking the template engine to pretty print the result
        PrintWriter writer = new PrintWriter(filepath, "UTF-8");
        writer.println(this.templateEngine.finish());
        writer.close();
    }

    /**
     * Imports module given its name, and copy its content at the beginning
     * of the target llvm file.
     *
     * @param moduleName Name of the module to be included
     */
    public void importModule(String moduleName) {
        String data = this.stdlibManager.includeModuleToLLVM(moduleName);
        if (data != null) {
            String tag = this.templateEngine.getCurrentTag();
            this.templateEngine.setTag(TemplateEngine.FUNCTIONS);
            this.templateEngine.insert(data);
            this.templateEngine.setTag(tag);
        }
    }

    /**
     * Allocates memory space for named variables (aka all variables existing in
     * in the Imp symbol table).
     */
    private void allocateVariables() {
        this.templateEngine.oneLineComment("Allocate memory for Imp variables");
        for (Integer index : identifiers.keySet()) { // For each symbol from the table
            String identifierName = (String) identifiers.get(index).getValue();
            this.templateEngine.insert(llvmVarName(identifierName) + " = alloca i32");
        }
        this.templateEngine.insert("%tmp = alloca i32");
        this.templateEngine.newLine();
    }

    /**
     * Generates llvm code from a <Program> node of the parse tree.
     * 
     * @param node Current node (must be <Program>)
     */
    private void generateFromProgram(final Node node) throws UndefinedFunctionException {
        consumeOneToken(LexicalUnit.BEGIN);
        generateFromCode(node.getChildren().get(1));
        consumeOneToken(LexicalUnit.END);
    }

    /**
     * Generates llvm code from a <Code> node of the parse tree.
     * 
     * @param node Current node (must be <Code>)
     */
    private void generateFromCode(final Node node) throws UndefinedFunctionException {
        // If of the form "<InstList>"
        if (node.getChildren().get(0).getSymbol().withoutChevrons().equals("InstList")) {
            generateFromInstList(node.getChildren().get(0));
        }
    }

    /**
     * Generates llvm code from a <InstList> node of the parse tree.
     * 
     * @param node Current node (must be <InstList>)
     */
    private void generateFromInstList(final Node node) throws UndefinedFunctionException {
        generateFromInstruction(node.getChildren().get(0));
        this.templateEngine.newLine();
        generateFromInstListTail(node.getChildren().get(1));
    }

    /**
     * Generates llvm code from a <Instruction> node of the parse tree.
     * 
     * @param node Current node (must be <Instruction>)
     */
    private void generateFromInstruction(final Node node) throws UndefinedFunctionException {
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
        } else if (instructionName.equals("Rand")) {
            generateFromRand(node.getChildren().get(0));
        } else if (instructionName.equals("Define")) {
            generateFromDefine(node.getChildren().get(0));
        } else if (instructionName.equals("Return")) {
            generateFromReturn(node.getChildren().get(0));
        } else if (instructionName.equals("Call")) {
            generateFromCall(node.getChildren().get(0));
        } else if (instructionName.equals("Import")) {
            generateFromImport(node.getChildren().get(0));
        }
    }

    /**
     * Generates llvm code from a <Assign> node of the parse tree.
     * 
     * @param node Current node (must be <Assign>)
     */
    private void generateFromAssign(final Node node) throws UndefinedFunctionException {
        String varName = (String) consumeOneToken(LexicalUnit.VARNAME).getValue();
        consumeOneToken(LexicalUnit.ASSIGN);
        generateFromAssignTail(node.getChildren().get(2), varName);
    }

    /**
     * Generates llvm code from a <AssignTail> node of the parse tree.
     * 
     * @param node Current node (must be <AssignTail>)
     * @param varName 
     */
    private void generateFromAssignTail(final Node node, final String varName) throws UndefinedFunctionException {
        String symbolName = node.getChildren().get(0).getSymbol().withoutChevrons();
        this.templateEngine.oneLineComment("Assignation of variable " + varName);
        if (symbolName.equals("ExprArith-p0")) {
            String tempVarName = generateFromExprArithP0(node.getChildren().get(0));
            this.templateEngine.insert("store i32 " + tempVarName + ", i32* " + llvmVarName(varName));
        } else {
            System.out.println(this.nUnnamedVariables);
            String tempVarName = generateFromCall(node.getChildren().get(0));
            this.templateEngine.insert("store i32 " + tempVarName + ", i32* " + llvmVarName(varName));      
        }
    }

    /**
     * Generates llvm code from a <Define> node of the parse tree.
     * 
     * @param node Current node (must be <Define>)
     */
    private void generateFromDefine(final Node node) throws UndefinedFunctionException {
        consumeOneToken(LexicalUnit.FUNCTION);
        String funcName = (String) consumeOneToken(LexicalUnit.FUNCNAME).getValue();
        consumeOneToken(LexicalUnit.LPAREN);

        // Create llvm parameter list
        List<String> args = generateFromParamList(node.getChildren().get(3));
        StringJoiner sj = new StringJoiner(", ", "", "");
        for (String argument : args) {
            sj.add("i32* " + argument);
        }

        // Create llvm function signature
        consumeOneToken(LexicalUnit.RPAREN);
        consumeOneToken(LexicalUnit.DO);
        this.templateEngine.setTag(this.templateEngine.FUNCTIONS);
        Integer unv = this.nUnnamedVariables;
        this.inFunc = true;
        this.nUnnamedVariables = 0;
        this.templateEngine.insert("define i32 " + funcName + "(" + sj.toString() + ") {");
        this.templateEngine.addLabel("entry");

        // Allocate memory for other variables
        this.templateEngine.oneLineComment("Allocate memory for Imp variables");
        for (Integer index : identifiers.keySet()) { // For each symbol from the table
            String identifierName = llvmVarName((String) identifiers.get(index).getValue());
            if (!args.contains(identifierName)) {
                this.templateEngine.insert(identifierName + " = alloca i32");
            }
        }
        this.templateEngine.newLine();

        // Write the body of the function
        generateFromCode(node.getChildren().get(6));
        this.templateEngine.insert("ret i32 0");
        this.templateEngine.newLine();
        this.templateEngine.insert("}");
        this.nUnnamedVariables = unv;
        this.inFunc = false;
        this.templateEngine.setTag(this.templateEngine.BODY);
        consumeOneToken(LexicalUnit.END);

        // Add the function name to the set of fonctions already defined by the user
        this.definedFunctionNames.add(funcName);
    }

    /**
     * Generates llvm code from a <Call> node of the parse tree.
     * 
     * @param node Current node (must be <Call>)
     * @return Name of the variable containing the fonction result
     */
    private String generateFromCall(final Node node) throws UndefinedFunctionException {
        // [FuncName] ( <ExprArith-p0> )
        Symbol funcNameToken = this.tokens.get(0);
        String funcName = (String) consumeOneToken(LexicalUnit.FUNCNAME).getValue();
        if (!(this.definedFunctionNames.contains(funcName) || this.stdlibManager.isFunctionLoaded(funcName))) {
            throw new UndefinedFunctionException(funcNameToken);
        }
        consumeOneToken(LexicalUnit.LPAREN);
        List<String> args = generateFromArgList(node.getChildren().get(2));
        consumeOneToken(LexicalUnit.RPAREN);
        StringJoiner sj = new StringJoiner(", ", "", "");
        for (int i = 0; i < args.size(); i++) {
            String tempVarName = llvmVarName("c" + nCalls + "c" + i);
            this.templateEngine.insert(tempVarName + " = alloca i32");
            this.templateEngine.insert("store i32 " + args.get(i) + ", i32* " + tempVarName);
            sj.add("i32* " + tempVarName);
        }
        this.nCalls++;
        String tempVarName = llvmVarName(String.valueOf(this.nUnnamedVariables++));
        this.templateEngine.insert(tempVarName + " = call i32 " + funcName + "(" + sj.toString() +  ")");
        return tempVarName;
    }

    /**
     * Generates llvm code from a <ArgList> node of the parse tree.
     * 
     * @param node Current node (must be <ArgList>)
     * @return List of llvm arg names
     */
    private List<String> generateFromArgList(final Node node) {
        List<String> args = new ArrayList<>();
        if (node.getChildren().size() > 1) {
            args.add(generateFromExprArithP0(node.getChildren().get(0)));
            args.addAll(generateFromArgListTail(node.getChildren().get(1)));
        }
        return args;
    }

    /**
     * Generates llvm code from a <ArgListTail> node of the parse tree.
     * 
     * @param node Current node (must be <ArgListTail>)
     * @return List of llvm arg names
     */
    private List<String> generateFromArgListTail(final Node node) {
        List<String> args = new ArrayList<>();
        if (node.getChildren().size() > 1) {
            consumeOneToken(LexicalUnit.COMMA);
            args.addAll(generateFromArgList(node.getChildren().get(1)));
        }
        return args;
    }

    /**
     * Generates llvm code from a <ParamList> node of the parse tree.
     * 
     * @param node Current node (must be <ParamList>)
     * @return List of llvm function parameter names
     */
    private List<String> generateFromParamList(final Node node) {
        List<String> args = new ArrayList<>();
        if (node.getChildren().size() > 1) {
            String varName = (String) consumeOneToken(LexicalUnit.VARNAME).getValue();
            args.add(llvmVarName(varName));
            args.addAll(generateFromParamListTail(node.getChildren().get(1)));
        }
        return args;
    }

    /**
     * Generates llvm code from a <ParamListTail> node of the parse tree.
     * 
     * @param node Current node (must be <ParamListTail>)
     * @return List of llvm function parameter names
     */
    private List<String> generateFromParamListTail(final Node node) {
        List<String> args = new ArrayList<>();
        if (node.getChildren().size() > 1) {
            consumeOneToken(LexicalUnit.COMMA);
            args.addAll(generateFromParamList(node.getChildren().get(1)));
        }
        return args;
    }

    /**
     * Generates llvm code from a <Return> node of the parse tree.
     * 
     * @param node Current node (must be <Return>)
     */
    private void generateFromReturn(final Node node) {
        consumeOneToken(LexicalUnit.RETURN);
        String tempVarName = generateFromExprArithP0(node.getChildren().get(1));
        this.templateEngine.insert("ret i32 " + tempVarName);
        if (this.inFunc) this.nUnnamedVariables++; // Hacky stuff
    }

    /**
     * Generates llvm code from a <Import> node of the parse tree.
     * 
     * @param node Current node (must be <Import>)
     */
    private void generateFromImport(final Node node) {
        consumeOneToken(LexicalUnit.IMPORT);
        String moduleName = (String) consumeOneToken(LexicalUnit.MODULENAME).getValue();
        this.importModule(moduleName);
    }

    /**
     * Generates llvm code from a <Read> node of the parse tree.
     * 
     * @param node Current node (must be <Read>)
     */
    private void generateFromRead(final Node node) {
        consumeOneToken(LexicalUnit.READ);
        consumeOneToken(LexicalUnit.LPAREN);
        String varName = (String) consumeOneToken(LexicalUnit.VARNAME).getValue();
        this.templateEngine.oneLineComment("Read ( " + varName + " ) ");
        String tempVarName = llvmVarName(String.valueOf(this.nUnnamedVariables++));
        this.templateEngine.insert(tempVarName + " = call i32 @readInt()");
        this.templateEngine.insert("store i32 " + tempVarName + ", i32* " + llvmVarName(varName));
        consumeOneToken(LexicalUnit.RPAREN);
    }

    /**
     * Generates llvm code from a <Print> node of the parse tree.
     * 
     * @param node Current node (must be <Print>)
     */
    private void generateFromPrint(final Node node) {
        consumeOneToken(LexicalUnit.PRINT);
        consumeOneToken(LexicalUnit.LPAREN);
        String varName = (String) consumeOneToken(LexicalUnit.VARNAME).getValue();
        String tempVarName = llvmVarName(String.valueOf(this.nUnnamedVariables++));
        this.templateEngine.oneLineComment("Print ( " + varName + " ) ");
        this.templateEngine.insert(tempVarName + " = load i32, i32* " + llvmVarName(varName));
        String instruction = "call void @println(i32 " + tempVarName + ")";
        this.templateEngine.insert(instruction);
        consumeOneToken(LexicalUnit.RPAREN);
    }

    /**
     * Generates llvm code from a <Rand> node of the parse tree.
     * 
     * @param node Current node (must be <Rand>)
     */
    private void generateFromRand(final Node node) {
        consumeOneToken(LexicalUnit.RAND);
        consumeOneToken(LexicalUnit.LPAREN);
        String varName = (String) consumeOneToken(LexicalUnit.VARNAME).getValue();
        this.templateEngine.oneLineComment("Rand ( " + varName + " ) ");
        String tempVarName = llvmVarName(String.valueOf(this.nUnnamedVariables++));
        consumeOneToken(LexicalUnit.RPAREN);
        this.templateEngine.insert(tempVarName + " = call i32 @rand()");
        this.templateEngine.insert("store i32 " + tempVarName + ", i32* " + llvmVarName(varName));
    }

    /**
     * Generates llvm code from a <If> node of the parse tree.
     * 
     * @param node Current node (must be <If>)
     */
    private void generateFromIf(final Node node) throws UndefinedFunctionException {
        String condName = "if.n" + String.valueOf(++this.nConditions);
        String trueLabelName = condName + ".true";
        String falseLabelName;
        if (node.getChildren().get(4).getChildren().size() > 1) {
            // If the <IfTail> child node is of the form "endif"
            falseLabelName = condName + ".false";
        } else {
            // If the <IfTail> child node is of the form "else <Code> endif"
            falseLabelName = condName;
        }
        consumeOneToken(LexicalUnit.IF);

        String condVarName = generateFromCondP0(node.getChildren().get(1));
        String instruction = "br i1 " + condVarName + ", label %" + trueLabelName + ", label %" + falseLabelName;
        this.templateEngine.insert(instruction);

        consumeOneToken(LexicalUnit.THEN);
        this.templateEngine.addLabel(trueLabelName);
        generateFromCode(node.getChildren().get(3));
        this.templateEngine.insert("br label %" + condName);
        generateFromIfTail(node.getChildren().get(4));
        this.templateEngine.addLabel(condName);
    }

    /**
     * Generates llvm code from a <While> node of the parse tree.
     * 
     * @param node Current node (must be <While>)
     */
    private void generateFromWhile(final Node node) throws UndefinedFunctionException {
        String condName = "while.n" + String.valueOf(++this.nConditions) + ".cond";
        String trueLabelName = condName + ".body";
        String falseLabelName = condName + ".end";
        this.templateEngine.insert("br label %" + condName);
        consumeOneToken(LexicalUnit.WHILE);

        // Loop condition
        this.templateEngine.addLabel(condName);
        String condVarName = generateFromCondP0(node.getChildren().get(1));
        String instruction = "br i1 " + condVarName + ", label %" + trueLabelName + ", label %" + falseLabelName;
        this.templateEngine.insert(instruction);
        consumeOneToken(LexicalUnit.DO);

        // Loop body
        this.templateEngine.addLabel(trueLabelName);
        generateFromCode(node.getChildren().get(3));
        this.templateEngine.insert("br label %" + condName);
        consumeOneToken(LexicalUnit.DONE);
        this.templateEngine.addLabel(falseLabelName);
    }

    /**
     * Generates llvm code from a <For> node of the parse tree.
     * The for loop is designated as being of either of the two following forms:
     * for [VarName] from "start" to "end" do <Code> done
     * for [VarName] from "start" by "step" to "end" do <Code> done
     * 
     * @param node Current node (must be <For>)
     */
    private void generateFromFor(final Node node) throws UndefinedFunctionException {
        String condName = "for.n" + String.valueOf(++this.nConditions) + ".cond";
        String trueLabelName = condName + ".body";
        String falseLabelName = condName + ".end";
        consumeOneToken(LexicalUnit.FOR);
        String counterVarName = llvmVarName((String) consumeOneToken(LexicalUnit.VARNAME).getValue());
        consumeOneToken(LexicalUnit.FROM);
        String initVarName = generateFromExprArithP0(node.getChildren().get(3));
        this.templateEngine.insert("store i32 " + initVarName + ", i32* " + counterVarName);

        // Compute "end" and "step" values
        String[] varnames = generateFromForTail(node.getChildren().get(4), counterVarName);
        this.templateEngine.insert("br label %" + condName);
        this.templateEngine.addLabel(trueLabelName);

        // Generate body of the for loop
        generateCodeOnlyFromForTail(node.getChildren().get(4));
        
        // Increment/decrement counter
        this.templateEngine.oneLineComment("Increment counter " + counterVarName);
        String incrementVarName = llvmVarName(String.valueOf(this.nUnnamedVariables++));
        this.templateEngine.insert(incrementVarName + " = load i32, i32* " + counterVarName);
        String result = llvmVarName(String.valueOf(this.nUnnamedVariables++));
        this.templateEngine.insert(result + " = add i32 " + incrementVarName + ", " + varnames[1]);
        this.templateEngine.insert("store i32 " + result + ", i32* " + counterVarName);

        // Loop condition
        this.templateEngine.insert("br label %" + condName);
        this.templateEngine.addLabel(condName);
        String tempVarName = llvmVarName(String.valueOf(this.nUnnamedVariables++));
        this.templateEngine.insert(tempVarName + " = load i32, i32* " + counterVarName);
        String condVarName = llvmVarName(String.valueOf(this.nUnnamedVariables++));
        String instruction = condVarName + " = icmp ne i32 " + tempVarName + ", " + varnames[0];
        this.templateEngine.insert(instruction);
        instruction = "br i1 " + condVarName + ", label %" + trueLabelName + ", label %" + falseLabelName;
        this.templateEngine.insert(instruction);
        this.templateEngine.addLabel(falseLabelName);
    }

    /**
     * Generates llvm code from a <ForTail> node of the parse tree.
     * Stops just before to produce code from the next <Code> child node.
     * This is to produce only the code related to the computation of
     * arithmetic expressions. More specifically, this only computes
     * the "end" and "step" integer values required for the for loop to work.
     * 
     * @param node Current node (must be <ForTail>)
     * @param counterVarName Name of the variable that plays the role of the loop counter
     * @return String array of size two, where the first element is the name of the variable
     *         containing the "end" value and the second element is the name of the variable
     *         containing the "step" value.
     */
    private String[] generateFromForTail(final Node node, final String counterVarName) {
        String[] varnames;
        if (node.getChildren().size() > 5) {
            // If of the form "by <ExprArith-p0> to <ExprArith-p0> do <Code> done"
            consumeOneToken(LexicalUnit.BY);
            String stepVarName = generateFromExprArithP0(node.getChildren().get(1));
            consumeOneToken(LexicalUnit.TO);
            String limitVarName = generateFromExprArithP0(node.getChildren().get(3));
            consumeOneToken(LexicalUnit.DO);
            varnames = new String[] { limitVarName, stepVarName };
        } else {
            // If the form "to <ExprArith-p0> do <Code> done"
            consumeOneToken(LexicalUnit.TO);
            String limitVarName = generateFromExprArithP0(node.getChildren().get(1));
            consumeOneToken(LexicalUnit.DO);
            varnames = new String[] { limitVarName, "1" };
        }
        return varnames;
    }

    /**
     * Generates llvm code from a <ForTail> node of the parse tree,
     * by starting from the next <Code> child node. This is to avoid
     * generating the code for "end" and "step" values twice.
     * See generateFromForTail for more details.
     * 
     * @param node Current node (must be <ForTail>)
     * @see generateFromForTail
     */
    private void generateCodeOnlyFromForTail(final Node node) throws UndefinedFunctionException {
        if (node.getChildren().size() > 5) {
            // If of the form "by <ExprArith-p0> to <ExprArith-p0> do <Code> done"
            generateFromCode(node.getChildren().get(5));
            consumeOneToken(LexicalUnit.DONE);
        } else {
            // If the form "to <ExprArith-p0> do <Code> done"
            generateFromCode(node.getChildren().get(3));
            consumeOneToken(LexicalUnit.DONE);
        }
    }

    /**
     * Generates llvm code from a <CondP0> node of the parse tree.
     * 
     * @param node Current node (must be <CondP0>)
     * @return Name of the variable containing the result of the operations
     *         generated from the current node
     */
    private String generateFromCondP0(final Node node) {
        String tempVarName = generateFromCondP0I(node.getChildren().get(0));
        return generateFromCondP0J(node.getChildren().get(1), tempVarName);
    }

    /**
     * Generates llvm code from a <CondP0I> node of the parse tree.
     * 
     * @param node Current node (must be <CondP0I>)
     * @return Name of the variable containing the result of the operations
     *         generated from the current node
     */
    private String generateFromCondP0I(final Node node) {
        return generateFromCondP1(node.getChildren().get(0));
    }

    /**
     * Generates llvm code from a <CondP0J> node of the parse tree.
     * 
     * @param node Current node (must be <CondP0J>)
     * @param leftVarName Name of the left variable in the present condition
     * @return Name of the variable containing the result of the condition
     */
    private String generateFromCondP0J(final Node node, final String leftVarName) {
        if (node.getChildren().size() > 1) {
            // If of the form "or <Cond-p1>"
            String rightVarName = generateFromExprArithP1(node.getChildren().get(1));
            String tempVarName = llvmVarName(String.valueOf(this.nUnnamedVariables++));
            String instruction = tempVarName + " = or i32 " + leftVarName + ", " + rightVarName;
            this.templateEngine.insert(instruction);
            return tempVarName;
        } else {
            // If of the form "epsilon"
            return leftVarName;
        }
    }

    /**
     * Generates llvm code from a <CondP1> node of the parse tree.
     * 
     * @param node Current node (must be <CondP1>)
     * @return Name of the variable containing the result of the condition
     */
    private String generateFromCondP1(final Node node) {
        String tempVarName = generateFromCondP1I(node.getChildren().get(0));
        return generateFromCondP1J(node.getChildren().get(1), tempVarName);
    }

    /**
     * Generates llvm code from a <CondP1I> node of the parse tree.
     * 
     * @param node Current node (must be <CondP1I>)
     * @return Name of the variable containing the result of the condition
     */
    private String generateFromCondP1I(final Node node) {
        return generateFromCondP2(node.getChildren().get(0));
    }

    /**
     * Generates llvm code from a <CondP1J> node of the parse tree.
     * 
     * @param node Current node (must be <CondP1J>)
     * @param leftVarName Name of the left variable in the present condition
     * @return Name of the variable containing the result of the condition
     */
    private String generateFromCondP1J(final Node node, final String leftVarName) {
        if (node.getChildren().size() > 1) {
            // If of the form "and <Cond-p2>"
            String rightVarName = generateFromCondP2(node.getChildren().get(1));
            String tempVarName = llvmVarName(String.valueOf(this.nUnnamedVariables++));
            String instruction = tempVarName + " = and i32 " + leftVarName + ", " + rightVarName;
            this.templateEngine.insert(instruction);
            return tempVarName;
        } else {
            // If of the form "epsilon"
            return leftVarName;
        }
    }

    /**
     * Generates llvm code from a <CondP2> node of the parse tree.
     * 
     * @param node Current node (must be <CondP2>)
     * @return Name of the variable containing the result of the condition
     */
    private String generateFromCondP2(final Node node) {
        if (node.getChildren().size() > 1) {
            // If of the form "not <SimpleCond>"
            return generateFromSimpleCond(node.getChildren().get(1));
        } else {
            // If of the form "<SimpleCond>"
            return generateFromSimpleCond(node.getChildren().get(0));
        }
    }

    /**
     * Generates llvm code from a <SimpleCond> node of the parse tree.
     * The output llvm instruction is either >=, >, <=, < or <>.
     * 
     * @param node Current node (must be <SimpleCond>)
     * @return Name of the variable containing the result of the simple condition
     */
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
        this.templateEngine.oneLineComment("Logical operation " + opName);
        this.templateEngine.insert(instruction);
        return tempVarName;
    }

    /**
     * Generates llvm code from a <Comp> node of the parse tree.
     * 
     * @param node Current node (must be <Comp>)
     * @return Name of the variable containing the result of the comparison
     */
    private String generateFromComp(final Node node) {
        return (String) consumeOneToken().getValue();
    }

    /**
     * Generates llvm code from a <IfTail> node of the parse tree.
     * 
     * @param node Current node (must be <IfTail>)
     */
    private void generateFromIfTail(final Node node) throws UndefinedFunctionException {
        if (node.getChildren().size() > 1) {
            // If of the form "else <Code> endif"
            String condName = "if.n" + String.valueOf(this.nConditions);
            String falseLabelName = condName + ".false";
            consumeOneToken(LexicalUnit.ELSE);
            this.templateEngine.addLabel(falseLabelName);
            generateFromCode(node.getChildren().get(1));
            consumeOneToken(LexicalUnit.ENDIF);
            this.templateEngine.insert("br label %" + condName);
        } else {
            // If of the form "endif"
            consumeOneToken(LexicalUnit.ENDIF);
        }
    }

    /**
     * Generates llvm code from a <InstListTail> node of the parse tree.
     * 
     * @param node Current node (must be <InstListTail>)
     */
    private void generateFromInstListTail(final Node node) throws UndefinedFunctionException {
        if (node.getChildren().size() > 1) {
            // If of the form "; <InstList>"
            consumeOneToken(LexicalUnit.SEMICOLON);
            this.templateEngine.newLine();
            generateFromInstList(node.getChildren().get(1));
        }
    }

    /**
     * Generates llvm code from a <ExprArithP0> node of the parse tree.
     * 
     * @param node Current node (must be <ExprArithP0>)
     * @return Name of the variable containing the result of the arithmetic expression
     */
    private String generateFromExprArithP0(final Node node) {
        String tempVarName = generateFromExprArithP0I(node.getChildren().get(0));
        return generateFromExprArithP0J(node.getChildren().get(1), tempVarName);
    }

    /**
     * Generates llvm code from a <ExprArithP0I> node of the parse tree.
     * 
     * @param node Current node (must be <ExprArithP0I>)
     * @return Name of the variable containing the result of the arithmetic expression
     */
    private String generateFromExprArithP0I(final Node node) {
        return generateFromExprArithP1(node.getChildren().get(0));
    }

    /**
     * Generates llvm code from a <ExprArithP0J> node of the parse tree.
     * 
     * @param node Current node (must be <ExprArithP0J>)
     * @param leftVarName Name of the left variable in the present arithmetic expression
     * @return Name of the variable containing the result of the arithmetic expression
     */
    private String generateFromExprArithP0J(final Node node, final String leftVarName) {
        if (node.getChildren().size() > 1) {
            // If of the form "<Op-p0> <ExprArith-p1>"
            String opName = generateFromOpP0(node.getChildren().get(0));
            String rightVarName = generateFromExprArithP1(node.getChildren().get(1));
            String tempVarName = llvmVarName(String.valueOf(this.nUnnamedVariables++));
            String instruction = null;
            if (opName.equals("+")) { // Imp Addition
                instruction = tempVarName + " = add i32 " + leftVarName + ", " + rightVarName;
            } else if (opName.equals("-")) { // Imp subtraction
                instruction = tempVarName + " = sub i32 " + leftVarName + ", " + rightVarName;
            } else {
                // TODO: raise exception
            }
            this.templateEngine.oneLineComment("Arithmetic operation " + opName);
            this.templateEngine.insert(instruction);
            return tempVarName;
        } else {
            // If of the form "epsilon"
            return leftVarName;
        }
    }

    /**
     * Generates llvm code from a <ExprArithP1> node of the parse tree.
     * 
     * @param node Current node (must be <ExprArithP1>)
     * @return Name of the variable containing the result of the arithmetic expression
     */
    private String generateFromExprArithP1(final Node node) {
        String tempVarName = generateFromExprArithP1I(node.getChildren().get(0));
        tempVarName = generateFromExprArithP1J(node.getChildren().get(1), tempVarName);
        return tempVarName;
    }

    /**
     * Generates llvm code from a <OpP0> node of the parse tree by returning the string
     * representation of the arithmetic operator.
     * 
     * @param node Current node (must be <OpP0>)
     * @return String representation of the arithmetic operator
     */
    private String generateFromOpP0(final Node node) {
        return (String) consumeOneToken().getValue();
    }

    /**
     * Generates llvm code from a <ExprArithP1I> node of the parse tree.
     * 
     * @param node Current node (must be <ExprArithP1I>)
     * @return Name of the variable containing the result of the arithmetic expression
     */
    private String generateFromExprArithP1I(final Node node) {
        return generateFromAtom(node.getChildren().get(0));
    }

    /**
     * Generates llvm code from a <ExprArithP1J> node of the parse tree.
     * 
     * @param node Current node (must be <ExprArithP1J>)
     * @param leftVarName Name of the left variable in the present arithmetic expression
     * @return Name of the variable containing the result of the arithmetic expression
     */
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
            this.templateEngine.oneLineComment("Arithmetic operation " + opName);
            this.templateEngine.insert(instruction);
            return tempVarName;
        } else {
            return leftVarName;
        }
    }

    /**
     * Generates llvm code from a <OpP1> node of the parse tree by returning the string
     * representation of the arithmetic operator.
     * 
     * @param node Current node (must be <OpP1>)
     * @return String representation of the arithmetic operator
     */
    private String generateFromOpP1(final Node node) {
        return (String) consumeOneToken().getValue();
    }

    /**
     * Generates llvm code from a <Atom> node of the parse tree and returns the name
     * of the variable containing the result of the atom evaluation. If the latter is
     * a varname, the named variable is returned directly.
     * 
     * @param node Current node (must be <Atom>)
     * @return Name of the variable containing the result of the atom evaluation
     */
    private String generateFromAtom(final Node node) {
        String symbolName = node.getChildren().get(0).getSymbol().withoutChevrons();
        String tempVarName;
        if (symbolName.equals("[VarName]")) {
            // If the atom is a named variable
            tempVarName = llvmVarName(String.valueOf(this.nUnnamedVariables++));
            String varName = (String) consumeOneToken(LexicalUnit.VARNAME).getValue();
            String instruction = tempVarName + " = load i32, i32* " + llvmVarName(varName);
            this.templateEngine.insert(instruction);
        } else if (symbolName.equals("[Number]")) {
            // If the atom is a number
            tempVarName = llvmVarName(String.valueOf(this.nUnnamedVariables++));
            Integer number = Integer.parseInt((String) consumeOneToken(LexicalUnit.NUMBER).getValue());
            String instruction = tempVarName + " = add i32 0, " + number;
            this.templateEngine.insert(instruction);
        } else if (symbolName.equals("(")) {
            // If the atom is of the form: ( <ExprArith-p0> )
            consumeOneToken(LexicalUnit.LPAREN);
            tempVarName = generateFromExprArithP0(node.getChildren().get(1));
            consumeOneToken(LexicalUnit.RPAREN);
        } else if (symbolName.equals("-")) {
            // If the atom is of the form: - <Atom>
            consumeOneToken(LexicalUnit.MINUS);
            String atomVarName = generateFromAtom(node.getChildren().get(1));
            tempVarName = llvmVarName(String.valueOf(this.nUnnamedVariables++));
            String instruction = tempVarName + " = sub i32 0, " + atomVarName;
            this.templateEngine.insert(instruction);
        } else {
            tempVarName = null; // TODO: raise exception
        }
        return tempVarName;
    }

    /**
     * Converts an Imp variable name to a llvm named variable.
     * This is achieved by simply adding "%" at the start of the varname.
     *
     * @param varName Name of the Imp variable
     * @return Name of the variable in the llvm code
     */
    private String llvmVarName(final String varName) {
        return "%" + varName;
    }
}
