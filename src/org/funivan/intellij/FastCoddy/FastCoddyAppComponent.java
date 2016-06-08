package org.funivan.intellij.FastCoddy;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.util.containers.HashMap;
import org.codehaus.jettison.json.JSONException;
import org.funivan.intellij.FastCoddy.CodeBuilders.CodeBuilder;
import org.funivan.intellij.FastCoddy.LanguageProcessor.CodeExpandInterface;
import org.funivan.intellij.FastCoddy.LanguageProcessor.Lang.JavaScriptCodeExpandProcessor;
import org.funivan.intellij.FastCoddy.LanguageProcessor.Lang.PhpCodeExpandProcessor;
import org.funivan.intellij.FastCoddy.LanguageProcessor.Lang.XmlCodeExpandProcessor;
import org.funivan.intellij.FastCoddy.Settings.PluginSettings;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;

/**
 * Created by ivan on 14.12.15.
 */
public class FastCoddyAppComponent implements ApplicationComponent {

    private static final String TEMPLATE_LOCATION = "/resources";
    public static Boolean FORCE_REWRITE_FILES = true;
    public static Boolean SKIP_REWRITE_FILES = false;
    private Map<String, CodeExpandInterface> codeExpands = null;

    public static FastCoddyAppComponent getInstance() {
        return ApplicationManager.getApplication().getComponent(FastCoddyAppComponent.class);
    }

    /**
     * On first initialization copy default template files
     *
     * @param forceRewrite
     */
    public void copyTemplateFiles(Boolean forceRewrite) {
        String defaultPath = PluginSettings.DEFAULT_FULL_PATH;
        File defaultDir = new File(defaultPath);
        if (!defaultDir.exists()) {
            defaultDir.mkdir();
        }

        if (!defaultDir.exists()) {
            System.out.println("Can not create directory");
            return;
        }

        copyTemplate(forceRewrite, "php.json");
        copyTemplate(forceRewrite, "javascript.json");
        copyTemplate(forceRewrite, "xml.json");

    }

    private void copyTemplate(Boolean forceRewrite, String fileName) {
        String phpFileName = TEMPLATE_LOCATION + "/" + fileName;

        InputStream is = FastCoddyAppComponent.class.getClassLoader().getResourceAsStream(phpFileName);


        File destinationFile = new File(PluginSettings.DEFAULT_FULL_PATH + "/" + fileName);

        if (destinationFile.isFile() && forceRewrite == false) {
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
        copyTemplateFiles(SKIP_REWRITE_FILES); // in production mode
//        copyTemplateFiles(FORCE_REWRITE_FILES); // in debug mode

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
     *
     * @return
     */
    public Map<String, CodeExpandInterface> getCodeExpand() {

        if (this.codeExpands == null) {

            try {

                String dir = PathManager.getConfigPath() + "/fast-coddy";


                codeExpands = new HashMap<String, CodeExpandInterface>();

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
