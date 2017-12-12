package generator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.lang.StringBuilder;
import java.util.Collections;

/**
* Template engine
*
* @author Antoine Passemiers
* @author Alexis Reynouard
*/
public class TemplateEngine {

    public static final String BODY = "@@body@@";

    private String body;
    private Integer bodyIndentLevel;

    private String templateFilepath;
    private String data;

    public TemplateEngine(final String templateFilepath) {
        this.templateFilepath = templateFilepath;
        init();
    }

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

    public void oneLineComment(String comment) {
        insert("; " + comment);
    }

    public void insert(String word) {
        insert(word, BODY);
        newLine();
    }

    public void insert(String word, String tag) {
        if (tag.equals(BODY)) {
            this.body += word;
        }
    }

    public void addLabel(String label) {
        String indent = indentFromLevel(this.bodyIndentLevel);
        this.body += ("\n" + indent);
        insert(label + ":");
    }

    public void newLine() {
        String indent = indentFromLevel(this.bodyIndentLevel + 1);
        this.body += ("\n" + indent);
    }

    public String indentFromLevel(Integer level) {
        return String.join("", Collections.nCopies(level, "    "));
    }

    private void finishBlock(String tag, String block) {
        String[] parts = this.data.split(tag);
        this.data = parts[0] + block + parts[1];
    }

    public String finish() {
        finishBlock(BODY, body);
        return this.data;
    }
}
