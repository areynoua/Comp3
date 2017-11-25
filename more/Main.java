import java.util.List;
import java.util.ArrayList;
import lexer.BadTerminalContextException;
import lexer.BadTerminalException;
import lexer.LexicalAnalyzer;
import lexer.Symbol;
import parser.LL1Grammar;
import parser.LL1Parser;


public class Main {
    private static final int REMOVE_USELESS = 0;
    private static final int LEFT_LEFT = 1;
    private static final int ACTION_TABLE = 2;
    private static final int PARSE = 3;

    private static String encodingName;
    private static String inputCodeFileName;
    private static String inputGrammarFileName;
    private static String outputFileName;
    private static Integer action;

    /**
     * Parse command line arguments
     */
    public static boolean argParse(String argv[]) {
        if (argv.length == 0 || argv.length % 2 == 1) {
            System.out.println("Arguments :\n	(1) [OPTIONS] --ru <grammar file> -o <grammar output file>\n	(2) [OPTIONS] --ll <grammar file> -o <grammar output file>\n	(3) [OPTIONS] --at <ll1 unambiguous grammar file>\n	(4) [OPTIONS] <ll1 unambiguous grammar file> <code> -o <latex output file>");
            System.out.println("(1) remove useless\n(2) left factorization and removing of left recursion\n(3) print action table\n(4) save parse tree");
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
        return action != null;
    }

    /**
     * Runs the scanner and the parser on input files.
     */
    public static void main(String argv[]) {
        if (argParse(argv)) {
            LexicalAnalyzer scanner = null;
            LL1Grammar grammar = null;
            LL1Parser parser = null;

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
                        System.out.println("TODO");
                        break;
                    case PARSE :
                        System.out.println("TODO");
                        break;
                    default :
                        System.out.println("???");
                        break;
                }

                if (action == REMOVE_USELESS || action == LEFT_LEFT) {
                    grammar.saveRulesToFiles(outputFileName);
                    // grammar.saveLatexRulesToFiles(outputFileName + ".tex");
                }

                /*
                java.io.FileInputStream stream = new java.io.FileInputStream(argv[i]);
                java.io.Reader reader = new java.io.InputStreamReader(stream, encodingName);
                scanner = new LexicalAnalyzer(reader);
                while ( !scanner.isAtEOF() )
                    scanner.yylex();
                 */

                /*
                parser = new LL1Parser(grammar);

                List<Symbol> symbols = scanner.getTokens();
                System.out.println("--------------------------------------------------------------------------------");
                parser.parse(symbols);
                */
            }
            catch (java.io.FileNotFoundException e) {
                System.out.println(e.getMessage());
                System.out.println(e);
                System.out.println("We are sorry");
                System.exit(2);
            }
            catch (java.io.IOException e) {
                System.out.println(e.getMessage());
                System.out.println(e);
            }
            /*
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
            */
        }
    }


}
