package generator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.lang.StringBuilder;

/**
* Template engine
*
* @author Antoine Passemiers
* @author Alexis Reynouard
*/
public class TemplateEngine {

    private String templateFilepath;
    private String data;

    public TemplateEngine(final String templateFilepath) {
        this.templateFilepath = templateFilepath;
        init();
    }

    public void init() {
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
        System.out.println(this.data.split("@@body@@")[0]);
    }
}