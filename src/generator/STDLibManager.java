package generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class STDLibManager {
    private List<String> importedModules;
    private List<String> loadedFunctions;

    STDLibManager() {
        this.importedModules = new ArrayList<>();
        this.loadedFunctions = new ArrayList<>();
    }

   /**
    * Opens a module header, and lists the functions defined in the pre-compiled
    * llvm module.
    *
    * @param moduleName  Name of the module to be included
    */
    public void loadFunctions(String moduleName) {
        String pathToHeader = "../more/imp_stdlib/" + moduleName + ".sih";
        File file = new File(pathToHeader);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                this.loadedFunctions.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Whether the module that defines the function has been loaded or not */
    public boolean isFunctionLoaded(String funcName) {
        return this.loadedFunctions.contains(funcName);
    }

    /**
     * Imports module given its name, and copy its content at the beginning
     * of the target llvm file.
     *
     * @param moduleName Name of the module to be included
     */
     public String includeModuleToLLVM(String moduleName) {
        String code = null;
        if (!importedModules.contains(moduleName)) {
            this.loadFunctions(moduleName);
            String pathToLibrary = "../more/imp_stdlib/" + moduleName + ".ll";
            File file = new File(pathToLibrary);
            FileInputStream fis;
            byte[] data = null;
            try {
                fis = new FileInputStream(file);
                data = new byte[(int) file.length()];
                fis.read(data);
                fis.close();
                code = new String(data, "UTF-8");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            importedModules.add(moduleName);
        }
        return code;
    }
}
