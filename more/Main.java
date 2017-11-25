import java.util.List;
import java.util.ArrayList;
import lexer.BadTerminalContextException;
import lexer.BadTerminalException;
import lexer.LexicalAnalyzer;
import lexer.Symbol;
import parser.LL1Grammar;
import parser.LL1Parser;


public class Main {
    private static List<String> fileList;
    private static String encodingName;
    private static String action;
    private static String output;

    public static void argParse(String argv[]) {
    }
    /**
     * Runs the scanner and the parser on input files.
     *
     * @param argv   the command line, contains the filenames to run
     *               the scanner on.
     */
    public static void main(String argv[]) {
        fileList = new ArrayList<>(argv.length);
        for (String arg : argv) {
            System.out.println(arg);
        }
        if (argv.length == 0) {
            System.out.println("Arguments :\n	(1) [OPTIONS] --ru <grammar file>\n	(2) [OPTIONS] --ll <grammar file>\n	(3) [OPTIONS] --at <ll1 unambiguous grammar file>\n	(4) [OPTIONS] <ll1 unambiguous grammar file> <code>");
        }
        else {
            int firstFilePos = 0;
            String encodingName = "UTF-8";
            if (argv[0].equals("--encoding")) {
                firstFilePos = 2;
                encodingName = argv[1];
                try {
                    java.nio.charset.Charset.forName(encodingName); // Side-effect: is encodingName valid? 
                } catch (Exception e) {
                    System.out.println("Invalid encoding '" + encodingName + "'");
                    return;
                }
            }
            for (int i = firstFilePos; i < argv.length; i++) {
                LexicalAnalyzer scanner = null;
                LL1Grammar grammar = null;
                LL1Parser parser = null;

                try {
                    java.io.FileInputStream stream = new java.io.FileInputStream(argv[i]);
                    java.io.Reader reader = new java.io.InputStreamReader(stream, encodingName);
                    scanner = new LexicalAnalyzer(reader);
                    while ( !scanner.isAtEOF() )
                        scanner.yylex();

                    grammar = new LL1Grammar("grammars/imp_prim.grammar");
                    parser = new LL1Parser(grammar);

                    List<Symbol> symbols = scanner.getTokens();
                    System.out.println("--------------------------------------------------------------------------------");
                    parser.parse(symbols);
                }
                catch (java.io.FileNotFoundException e) {
                    System.out.println("File not found : \""+argv[i]+"\"");
                }
                catch (java.io.IOException e) {
                    System.out.println("IO error scanning file \""+argv[i]+"\"");
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


}
