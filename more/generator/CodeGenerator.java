package generator;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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

    private TemplateEngine templateEngine;

    public CodeGenerator(final String templateFilepath) {
        this.templateEngine = new TemplateEngine(templateFilepath);
    }

    public void generate(final List<Symbol> tokens, final Node parseTree, final String filepath) {
        StringBuilder builder = new StringBuilder();

        String s = "I want to walk my dog";
        String[] arr = s.split("dog");

        PrintWriter writer;
        try {
            writer = new PrintWriter(filepath, "UTF-8");
            writer.println(builder.toString());
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}