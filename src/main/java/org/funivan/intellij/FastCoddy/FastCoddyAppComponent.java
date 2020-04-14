package org.funivan.intellij.FastCoddy;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.containers.HashMap;
import org.codehaus.jettison.json.JSONException;
import org.funivan.intellij.FastCoddy.CodeBuilders.CodeBuilder;
import org.funivan.intellij.FastCoddy.LanguageProcessor.CodeExpandInterface;
import org.funivan.intellij.FastCoddy.LanguageProcessor.CodeExpandProcessor;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;

/**
 * @author Ivan Scherbak <dev@funivan>
 */
public class FastCoddyAppComponent implements ApplicationComponent {

    final public static Logger LOG = Logger.getInstance("Symfony-Plugin");
    private static final String DEFAULT_FULL_PATH = PathManager.getConfigPath() + "/fast-coddy";

    private Map<String, CodeExpandInterface> codeExpands = null;

    public static FastCoddyAppComponent getInstance() {
        return ApplicationManager.getApplication().getComponent(FastCoddyAppComponent.class);
    }

    /**
     * On first initialization copy default template files
     */
    private void copyTemplateFiles(Boolean forceRewrite) throws IOException {
        String defaultPath = DEFAULT_FULL_PATH;
        File defaultDir = new File(defaultPath);
        if (!defaultDir.exists()) {
            if (!defaultDir.mkdir()) {
                throw new IOException("Can not create configuration directory");
            }
        }

        if (!defaultDir.exists()) {
            return;
        }

        copyTemplate(forceRewrite, "php.json");
        copyTemplate(forceRewrite, "javascript.json");
        copyTemplate(forceRewrite, "xml.json");

    }

    private void copyTemplate(Boolean forceRewrite, String fileName) {
        InputStream is = FastCoddyAppComponent.class.getClassLoader().getResourceAsStream(fileName);

        File destinationFile = new File(DEFAULT_FULL_PATH + "/" + fileName);
        if (is == null) {
            System.out.println("fs is null");
            return;
        }
        if (destinationFile.isFile() && !forceRewrite) {
            return;
        }

        try {
            Files.copy(is, destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initComponent() {
        try {
            copyTemplateFiles(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "FastCoddyAppComponent";
    }

    public void flushConfiguration() {
        this.codeExpands = null;
    }

    /**
     * Load code expands
     */
    public Map<String, CodeExpandInterface> getCodeExpand() throws JSONException {

        if (this.codeExpands == null) {
            String dir = PathManager.getConfigPath() + "/fast-coddy";

            codeExpands = new HashMap<>();

            CodeBuilder phpCodeBuilder = new CodeBuilder(dir + "/php.json");
            codeExpands.put("PHP", new CodeExpandProcessor(phpCodeBuilder));


            CodeBuilder xmlCodeBuilder = new CodeBuilder(dir + "/xml.json");
            codeExpands.put("XML", new CodeExpandProcessor(xmlCodeBuilder));


            CodeBuilder javascriptCodeBuilder = new CodeBuilder(dir + "/javascript.json");
            codeExpands.put("JavaScript", new CodeExpandProcessor(javascriptCodeBuilder));


        }

        return this.codeExpands;
    }

}
