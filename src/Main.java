import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ProcessBuilder;
import java.lang.Runtime;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.lang.System;

import lexer.BadTerminalContextException;
import lexer.BadTerminalException;
import lexer.LexicalAnalyzer;
import lexer.Symbol;
import parser.LL1Grammar;
import parser.LL1Parser;
import generator.CodeGenerator;
import generator.UndefinedFunctionException;
import parser.UnexpectedEndOfFileException;
import parser.UnexpectedSymbolException;

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
    /** Command line argument: ACTION -exec */
    private static final int EXEC = 4;

    /** Uncommented command line argument */
    private static String encodingName;
    /** Command line argument: input code file */
    private static String inputCodeFileName;
    /** Command line argument: input grammar file */
    private static String inputGrammarFileName;
    /** Command line argument: output file */
    private static String outputFileName;
    /** Command line argument: tree output file */
    private static String treeFileName;
    /** Command line argument: choosen ACTION */
    private static Integer action;

    /**
     * Parse command line arguments
     */
    public static boolean argParse(String argv[]) {
        if (argv == null) {
            System.out.println("Arguments :\n	(1) --ru <grammar file> -o <grammar output file>\n	(2) --ll <grammar file> -o <grammar output file>\n	(3) --at <ll1 unambiguous grammar file> -o <latex output file>");
            System.out.println("\t(4) <code file> [-g <input: ll1 unambiguous grammar file>] [-o <output: LLVM IR code file>] [-t <output: parse tree output file>]\n	(5) -exec <inputFile.imp>");
            System.out.println("(1) remove useless\n(2) left factorization and removing of left recursion\n(3) print action table\n(4) Output the llvm intermediary code (or save it in the file denoted by -o) and optionnaly save the parse tree\n(5) Compile and execute the given Imp program");
            return false;
        }
        encodingName = "UTF-8";
        for (int i = 0; i < argv.length; i+=2) {
            switch (argv[i]) {
                case "--encoding":
                    if (i+1 >= argv.length) return false;
                    encodingName = argv[i+1];
                    try {
                        java.nio.charset.Charset.forName(encodingName);
                    } catch (Exception e) {
                        System.out.println("Invalid encoding '" + encodingName + "'");
                        return false;
                    }
                    break;
                case "--ru":
                    if (i+1 >= argv.length) return false;
                    if (action != null) return false;
                    action = REMOVE_USELESS;
                    inputGrammarFileName = argv[i+1];
                    break;
                case "--ll":
                    if (i+1 >= argv.length) return false;
                    if (action != null) return false;
                    action = LEFT_LEFT;
                    inputGrammarFileName = argv[i+1];
                    break;
                case "--at":
                    if (i+1 >= argv.length) return false;
                    if (action != null) return false;
                    action = ACTION_TABLE;
                    inputGrammarFileName = argv[i+1];
                    break;
                case "-o":
                    if (i+1 >= argv.length) return false;
                    outputFileName = argv[i+1];
                    break;
                case "-t":
                    if (i+1 >= argv.length) return false;
                    treeFileName = argv[i+1];
                    break;
                case "-g":
                    if (i+1 >= argv.length) return false;
                    inputGrammarFileName = argv[i+1];
                    break;
                case "-exec":
                    if (i+1 >= argv.length) return false;
                    if (action != null) return false;
                    action = EXEC;
                    inputCodeFileName = argv[i+1];
                    break;
                default :
                    if (action != null) return false;
                    action = PARSE;
                    if (inputGrammarFileName == null) {
                        inputGrammarFileName = "../more/grammars/imp_ll.grammar";
                    }
                    inputCodeFileName = argv[i];
                    --i;
            }
        }
        return action != null && (outputFileName != null || ((action == PARSE || action == EXEC) && inputCodeFileName != null));
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
                if (inputGrammarFileName == null) {
                    inputGrammarFileName = "../more/grammars/imp_ll.grammar";
                }
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
                    case EXEC :
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
                            if (treeFileName != null) {
                                parser.saveTextTreeToFile(treeFileName + ".txt");
                                parser.saveLatexTreeToFile(treeFileName + ".tex");
                                parser.saveJavascriptToFile("../more/trees/rules.js");
                            }
                            // System.out.println("Parsing done");
                            // System.out.println("Rules used:");
                            // System.out.println(parser.rulesUsedToString());
                        }
                        else {
                            System.out.println("Syntax error");
                            System.out.println(parser);
                        }
                        codeGenerator = new CodeGenerator("../more/templates/template.ll");
                        if (action == EXEC) outputFileName = "temp.ll";
                        codeGenerator.generate(symbols, parser.getParseTree(), identifiers, outputFileName);
                        if (action == PARSE) {
                            break;
                        }
                        else {
                            Runtime runTime = Runtime.getRuntime();
                            runTime.exec("llvm-as " + outputFileName + " -o=temp.bc");
                            ProcessBuilder pb = new ProcessBuilder("lli", "temp.bc");
                            pb.inheritIO();
                            pb.start();
                            Process process = pb.start();

                            // runTime.exec("rm -rf temp.ll temp.bc");
                            break;
                        }
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
                System.exit(1);
            }
            catch (BadTerminalException e) {
                System.out.println("Unknown terminal:");
                System.out.println(e.getMessage());
                System.exit(16);
            }
            catch (BadTerminalContextException e) {
                System.out.println("Bad terminal context:");
                System.out.println(e.getMessage());
                System.exit(17);
            }
            catch (UndefinedFunctionException e) {
                System.out.println("Call to undefined function:");
                System.out.println(e.getMessage());
                System.exit(18);
            }
            catch (UnexpectedSymbolException e) {
                System.out.println("Unexpected symbol:");
                System.out.println(e.getMessage());
                System.out.println(parser);
                System.exit(19);
            }
            catch (UnexpectedEndOfFileException e) {
                System.out.println("Unexpected End Of File:");
                System.out.println(e.getMessage());
                System.exit(19);
            }
            catch (Exception e) {
                System.out.println("Unexpected exception:");
                e.printStackTrace();
                System.exit(-1);
            }
        }
        else {
            argParse(null);
        }
    }
}
