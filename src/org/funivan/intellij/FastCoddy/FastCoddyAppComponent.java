package org.funivan.intellij.FastCoddy;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.containers.HashMap;
import org.codehaus.jettison.json.JSONException;
import org.funivan.intellij.FastCoddy.CodeBuilders.CodeBuilder;
import org.funivan.intellij.FastCoddy.LanguageProcessor.CodeExpandInterface;
import org.funivan.intellij.FastCoddy.LanguageProcessor.Lang.JavaScriptCodeExpandProcessor;
import org.funivan.intellij.FastCoddy.LanguageProcessor.Lang.PhpCodeExpandProcessor;
import org.funivan.intellij.FastCoddy.LanguageProcessor.Lang.XmlCodeExpandProcessor;
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

    private static final String TEMPLATE_LOCATION = "/resources";
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
        String phpFileName = TEMPLATE_LOCATION + "/" + fileName;

        InputStream is = FastCoddyAppComponent.class.getClassLoader().getResourceAsStream(phpFileName);


        File destinationFile = new File(DEFAULT_FULL_PATH + "/" + fileName);

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
    public Map<String, CodeExpandInterface> getCodeExpand() {

        if (this.codeExpands == null) {

            try {

                String dir = PathManager.getConfigPath() + "/fast-coddy";


                codeExpands = new HashMap<>();

                // load global configuration
                CodeBuilder phpCodeBuilder = new CodeBuilder();

                phpCodeBuilder.loadConfigFromFile(dir + "/php.json", "");
                codeExpands.put("PHP", new PhpCodeExpandProcessor(phpCodeBuilder));


                CodeBuilder xmlCodeBuilder = new CodeBuilder();
                xmlCodeBuilder.loadConfigFromFile(dir + "/xml.json", "");
                codeExpands.put("XML", new XmlCodeExpandProcessor(xmlCodeBuilder));

                // load global configuration
                CodeBuilder javascriptCodeBuilder = new CodeBuilder();
                javascriptCodeBuilder.loadConfigFromFile(dir + "/javascript.json", "");
                codeExpands.put("JavaScript", new JavaScriptCodeExpandProcessor(javascriptCodeBuilder));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return this.codeExpands;
    }

}
