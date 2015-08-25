package org.funivan.intellij.FastCoddy;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.util.containers.HashMap;
import org.codehaus.jettison.json.JSONException;
import org.funivan.intellij.FastCoddy.CodeBuilders.CodeBuilder;
import org.funivan.intellij.FastCoddy.LanguageProcessor.CodeExpandInterface;
import org.funivan.intellij.FastCoddy.LanguageProcessor.Lang.JavaScriptCodeExpandProcessor;
import org.funivan.intellij.FastCoddy.LanguageProcessor.Lang.PhpCodeExpandProcessor;
import org.funivan.intellij.FastCoddy.LanguageProcessor.Lang.XPathCodeExpandProcessor;
import org.funivan.intellij.FastCoddy.Settings.PluginSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Created by ivan on 7/28/14.
 */
public class CoddyProjectComponent implements ProjectComponent {

    protected boolean LOADED_CONFIGURATION = false;
    private Map<String, CodeExpandInterface> codeExpands;
    final Project project;
    protected String projectName;

    public CoddyProjectComponent(Project project) {
        this.project = project;
        this.projectName = project.getName();
    }

    public Map<String, CodeExpandInterface> getCodeExpand() {

        if (LOADED_CONFIGURATION == false) {
            this.loadCodeBuilders();
            LOADED_CONFIGURATION = true;
        }

        return this.codeExpands;
    }

    public void loadCodeBuilders() {
        Project project = this.project;

        try {


            codeExpands = new HashMap<String, CodeExpandInterface>();

            // load global configuration
            CodeBuilder phpCodeBuilder = new CodeBuilder();

            System.out.println("PluginSettings.getSettings().configurationDirectory:" + PluginSettings.getSettings().configurationDirectory);
            System.out.println("project.getWorkspaceFile().getParent().getPath():" + project.getWorkspaceFile().getParent().getPath());

            phpCodeBuilder.loadConfigFromFile(PluginSettings.getSettings().configurationDirectory + "/php.json", "");
            phpCodeBuilder.loadConfigFromFile(project.getWorkspaceFile().getParent().getPath() + "/fast-coddy/php.json", project.getBaseDir().getPath());
            codeExpands.put("PHP", new PhpCodeExpandProcessor(phpCodeBuilder));


            CodeBuilder xpathCodeBuilder = new CodeBuilder();
            xpathCodeBuilder.loadConfigFromFile(PluginSettings.getSettings().configurationDirectory + "/xpath.json", "");
            xpathCodeBuilder.loadConfigFromFile(project.getWorkspaceFile().getParent().getPath() + "/fast-coddy/xpath.json", project.getBaseDir().getPath());
            codeExpands.put("XPath", new XPathCodeExpandProcessor(xpathCodeBuilder));

            // load global configuration
            CodeBuilder javascriptCodeBuilder = new CodeBuilder();
            javascriptCodeBuilder.loadConfigFromFile(PluginSettings.getSettings().configurationDirectory + "/javascript.json", "");
            javascriptCodeBuilder.loadConfigFromFile(project.getWorkspaceFile().getParent().getPath() + "/fast-coddy/javascript.json", project.getBaseDir().getPath());
            codeExpands.put("JavaScript", new JavaScriptCodeExpandProcessor(javascriptCodeBuilder));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void projectOpened() {
        getCodeExpand();
    }

    @Override
    public void projectClosed() {
        this.codeExpands = null;
    }

    @Override
    public void initComponent() {
        System.out.println("init component:" + projectName);
    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "CoddyModuleComponent" + projectName;
    }

    @Nullable
    public static CoddyProjectComponent getProjectComponent(Project project) {
        CoddyProjectComponent c = (CoddyProjectComponent) project.getComponent("CoddyModuleComponent" + project.getName());
        return c;
    }

    public void flushConfiguration() {
        LOADED_CONFIGURATION = false;
    }
}
