package org.funivan.intellij.FastCoddy;


import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import org.funivan.intellij.FastCoddy.Settings.PluginSettings;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * User: ivan
 * Date: 4/4/14
 * Time: 10:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class CoddyModuleComponent implements ModuleComponent {

    public static Boolean FORCE_REWRITE_FILES = true;
    public static Boolean SKIP_REWRITE_FILES = false;

    protected Module module;

    public CoddyModuleComponent(Module module) {
        this.module = module;
    }

    private static final String TEMPLATE_LOCATION = "/resources";

    public void initComponent() {
//        copyTemplateFiles(SKIP_REWRITE_FILES);
        copyTemplateFiles(FORCE_REWRITE_FILES);
    }

    public static void copyTemplateFiles(Boolean forceRewrite) {
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


    }

    private static void copyTemplate(Boolean forceRewrite, String fileName) {
        String phpFileName = TEMPLATE_LOCATION + "/" + fileName;
        InputStream is = CoddyModuleComponent.class.getClassLoader().getResourceAsStream(phpFileName);


        File destinationFile = new File(PluginSettings.DEFAULT_FULL_PATH + "/" + fileName);

        if (destinationFile.isFile() && forceRewrite == false) {
//            System.out.println("FastCoddy.:: SKIP " + phpFileName + " to " + destinationFile);
            return;
        }

//        System.out.println("FastCoddy.:: File copy from " + phpFileName + " to " + destinationFile);
        try {
            Files.copy(is, destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void disposeComponent() {

    }

    @NotNull
    public String getComponentName() {
        return "org.org.funivan.intellij.FastCoddy.CoddyModuleComponent";
    }

    public void projectOpened() {

    }

    public void projectClosed() {

    }

    public void moduleAdded() {

    }
}
