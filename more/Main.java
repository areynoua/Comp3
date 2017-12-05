import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import lexer.BadTerminalContextException;
import lexer.BadTerminalException;
import lexer.LexicalAnalyzer;
import lexer.Symbol;
import parser.LL1Grammar;
import parser.LL1Parser;
import generator.CodeGenerator;

/**
 * Entry point of the compiler
 */
public class Main {
    /** Command line argument: ACTION --ru */
    private static final int REMOVE_USELESS = 0;
    /** Command line argument: ACTION  --ll */
    private static final int LEFT_LEFT = 1;
    /** Command line argument: ACTION --at */
    private static final int ACTION_TABLE = 2;
    /** Command line argument: default ACTION*/
    private static final int PARSE = 3;

    /** Uncommented command line argument */
    private static String encodingName;
    /** Command line argument: input code file */
    private static String inputCodeFileName;
    /** Command line argument: input grammar file */
    private static String inputGrammarFileName;
    /** Command line argument: output file */
    private static String outputFileName;
    /** Command line argument: choosen ACTION */
    private static Integer action;

    /**
     * Parse command line arguments
     */
    public static boolean argParse(String argv[]) {
        if (argv.length == 0 || argv.length % 2 == 1) {
            System.out.println("Arguments :\n	(1) --ru <grammar file> -o <grammar output file>\n	(2) --ll <grammar file> -o <grammar output file>\n	(3) --at <ll1 unambiguous grammar file> -o <latex output file>\n	(4) <ll1 unambiguous grammar file> <code> [-o <parse tree output file>]");
            System.out.println("(1) remove useless\n(2) left factorization and removing of left recursion\n(3) print action table\n(4) save parse tree and output the rule used");
            return false;
        }
        encodingName = "UTF-8";
        for (int i = 0; i < argv.length; i+=2) {
            switch (argv[i]) {
                case "--encoding":
                    encodingName = argv[i+1];
                    try {
                        java.nio.charset.Charset.forName(encodingName); // Side-effect: is encodingName valid? 
                    } catch (Exception e) {
                        System.out.println("Invalid encoding '" + encodingName + "'");
                        return false;
                    }
                    break;
                case "--ru":
                    if (action != null) return false;
                    action = REMOVE_USELESS;
                    inputGrammarFileName = argv[i+1];
                    break;
                case "--ll":
                    if (action != null) return false;
                    action = LEFT_LEFT;
                    inputGrammarFileName = argv[i+1];
                    break;
                case "--at":
                    if (action != null) return false;
                    action = ACTION_TABLE;
                    inputGrammarFileName = argv[i+1];
                    break;
                case "-o":
                    outputFileName = argv[i+1];
                    break;
                default :
                    if (action != null) return false;
                    action = PARSE;
                    inputGrammarFileName = argv[i];
                    inputCodeFileName = argv[i+1];
            }
        }
        return action != null && (outputFileName != null || action == PARSE);
    }

    /**
     * Perform the choosen actions on the choosen file.
     */
    public static void main(String argv[]) {
        if (argParse(argv)) {
            LexicalAnalyzer scanner = null;
            LL1Grammar grammar = null;
            LL1Parser parser = null;
            CodeGenerator codeGenerator = null;

            try {
                grammar = new LL1Grammar(inputGrammarFileName);

                switch (action) {
                    case REMOVE_USELESS :
                        grammar.removeUnproductive();
                        grammar.removeInaccessible();
                        break;
                    case LEFT_LEFT :
                        grammar.leftFactor();
                        grammar.removeLeftRecursion();
                        break;
                    case ACTION_TABLE :
                        parser = new LL1Parser(grammar);
                        parser.saveLatexActionTableToFile(outputFileName);
                        grammar.saveLatexFirstFollowToFile("first_follow.tex");
                        break;
                    case PARSE :
                        java.io.FileInputStream stream = new java.io.FileInputStream(inputCodeFileName);
                        java.io.Reader reader = new java.io.InputStreamReader(stream, encodingName);
                        scanner = new LexicalAnalyzer(reader);
                        while ( !scanner.isAtEOF() ) {
                            scanner.yylex();
                        }
                        parser = new LL1Parser(grammar);
                        List<Symbol> symbols = scanner.getTokens();
                        HashMap<Integer, Symbol> identifiers = scanner.getIdentifiers();
                        boolean success = parser.parse(symbols);
                        if (success) {
                            if (outputFileName != null) {
                                parser.saveTextTreeToFile(outputFileName + ".txt");
                                parser.saveLatexTreeToFile(outputFileName + ".tex");
                                parser.saveJavascriptToFile("trees/rules.js");
                            }
                            System.out.println("Parsing done");
                            System.out.println("Rules used:");
                            System.out.println(parser.rulesUsedToString());
                        }
                        else {
                            System.out.println("Syntax error");
                            System.out.println(parser);
                        }
                        codeGenerator = new CodeGenerator("templates/template.ll");
                        codeGenerator.generate(symbols, parser.getParseTree(), identifiers, "main.ll");
                        break;
                    default :
                        System.out.println("Unknown action");
                        break;
                }

                if (action == REMOVE_USELESS || action == LEFT_LEFT) {
                    grammar.saveRulesToFile(outputFileName);
                    grammar.saveLatexRulesToFiles(outputFileName + ".tex");
                }
            }
            catch (java.io.FileNotFoundException e) {
                System.out.println(e.getMessage());
                System.out.println(e);
                System.exit(2);
            }
            catch (java.io.IOException e) {
                System.out.println(e.getMessage());
                System.out.println(e);
            }
            catch (java.io.UnsupportedEncodingException e) {
                System.out.println(e.getMessage());
                System.out.println(e);
            }
            catch (BadTerminalException e) {
                System.out.println("Unknown terminal:");
                System.out.println(e);
            }
            catch (BadTerminalContextException e) {
                System.out.println("Bad terminal context:");
                System.out.println(e);
            }
            catch (Exception e) {
                System.out.println("Unexpected exception:");
                e.printStackTrace();
            }
        }
    }
}
