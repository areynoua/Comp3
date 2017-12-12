package generator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.lang.StringBuilder;
import java.util.Collections;

/**
* Template engine used to make a pretty string representation of
* of the llvm code. To write the code to the output file, we
* call TemplateEngine.finish to retrieve the string and write it
* to the given file.
*
* @see finish
* @author Antoine Passemiers
* @author Alexis Reynouard
*/
public class TemplateEngine {

    // Tag placed in the template file to locate where to write the main function
    public static final String BODY = "@@body@@";

    private String body; // Code contained in the main function
    private Integer bodyIndentLevel; // Current number of indents

    private String templateFilepath; // Path to the template file
    private String data;

    /**
     * Initialization of the template engine.
     *
     * @param templateFilepath Path to the llvm template file
     */
    public TemplateEngine(final String templateFilepath) {
        this.templateFilepath = templateFilepath;
        init();
    }

    /**
     * Loads the llvm template file, locate where the tags
     * are located and splits the file content at the different tag locations.
     */
    public void init() {
        this.body = "";
        this.bodyIndentLevel = 0;

        File file = new File(this.templateFilepath);
        FileInputStream fis;
        byte[] data = null;
        try {
            fis = new FileInputStream(file);
            data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            this.data = new String(data, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes a single-line comment and starts a new line.
     *
     * @param comment Comment to write
     */
    public void oneLineComment(String comment) {
        insert("; " + comment);
    }

    /**
     * Inserts code at current cursor position in main function.
     * After insertion, the cursor position is moved to the location
     * of the last inserted character.
     *
     * @param content Code to add
     */
    public void insert(String content) {
        insert(content, BODY);
        newLine();
    }

    /**
     * Inserts code at current cursor position of the given tag
     * After insertion, the cursor position is moved to the location
     * of the last inserted character.
     *
     * @param content Code to add
     * @param tag Tag that gives the location where to write
     */
    public void insert(String word, String tag) {
        if (tag.equals(BODY)) {
            this.body += word;
        }
    }

    /**
     * Adds a new llvm label at current position in main function.
     *
     * @param label Label to add to the main function
     */
    public void addLabel(String label) {
        String indent = indentFromLevel(this.bodyIndentLevel);
        this.body += ("\n" + indent);
        insert(label + ":");
    }

    /** Add a new line to the code */
    public void newLine() {
        String indent = indentFromLevel(this.bodyIndentLevel + 1);
        this.body += ("\n" + indent);
    }

    /** Returns tabs, given the current indentation level */
    public String indentFromLevel(Integer level) {
        return String.join("", Collections.nCopies(level, "    "));
    }

    /** 
     * Finalize code formatting for given tag
     *
     * @param tag Tag that gives the location where finalize the code
     * @param block Content to write at current tag position
     */
    private void finishBlock(String tag, String block) {
        String[] parts = this.data.split(tag);
        this.data = parts[0] + block + parts[1];
    }

    /** Returns the whole code as a string */
    public String finish() {
        finishBlock(BODY, body);
        return this.data;
    }
}
