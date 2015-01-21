package org.funivan.intellij.FastCoddy.CodeBuilders;

import com.intellij.codeInsight.template.CustomLiveTemplate;
import com.intellij.codeInsight.template.CustomTemplateCallback;
import com.intellij.codeInsight.template.LiveTemplateBuilder;
import com.intellij.codeInsight.template.impl.TemplateImpl;
import com.intellij.psi.PsiFile;
import org.funivan.intellij.FastCoddy.CodeBuilders.Configuration.VariableConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: ivan
 * Date: 12/23/13
 */
public class IntellijLiveTemplate implements CustomLiveTemplate {

    protected CodeTemplate codeTemplateConfigurationTemplate;

    public IntellijLiveTemplate(CodeTemplate codeTemplateConfigurationTemplate) {
        this.codeTemplateConfigurationTemplate = codeTemplateConfigurationTemplate;
    }

    @Nullable
    @Override
    public String computeTemplateKey(@NotNull CustomTemplateCallback customTemplateCallback) {
        return null;
    }

    @Override
    public boolean isApplicable(PsiFile psiFile, int i, boolean b) {
        return false;
    }

    @Override
    public boolean supportsWrapping() {
        return false;
    }

    public void expandTemplate(@NotNull CustomTemplateCallback callback) {
        if (codeTemplateConfigurationTemplate != null) {
            this.expand(codeTemplateConfigurationTemplate.getCode(), callback);
        }
    }

    @Override
    public void expand(String code, @NotNull CustomTemplateCallback customTemplateCallback) {

        HashMap<String, VariableConfiguration> variables = codeTemplateConfigurationTemplate.getVariablesConfiguration();
        code = code.replaceAll("\\$(TAB[0-9_]+|VAR_[0-9_A-Z]+)\\$", "__$1__");
        code = code.replace("$", "$$");
        code = code.replaceAll("__(TAB[0-9_]+|VAR_[0-9_A-Z]+)__", "\\$$1\\$");


        TemplateImpl template = new TemplateImpl("", code, "");
        template.setToReformat(true);
        template.setToIndent(true);

        Matcher matcher = Pattern.compile("\\$(TAB[0-9_]+|VAR_[0-9_A-Z]+)\\$").matcher(code);

        List<String> variableNamesList = new ArrayList<String>();
        while (matcher.find()) {
            String variableName = matcher.group(1);

            if (variableNamesList.contains(variableName)) {
                System.out.println("skip. variable exist :" + variableName);
                continue;
            }

//            System.out.println("add variable:" + variableName);

            VariableConfiguration variableConfiguration = variables.get(variableName);
            if (variableConfiguration != null) {
//                System.out.println("getExpression:" + variableConfiguration.getExpression());
//                System.out.println("getDefaultValue:" + variableConfiguration.getDefaultValue());
//                System.out.println("getAlwaysStopAt:" + variableConfiguration.getAlwaysStopAt());
//                System.out.println("");
                template.addVariable(variableName, variableConfiguration.getExpression(), variableConfiguration.getDefaultValue(), variableConfiguration.getAlwaysStopAt());
            } else {
                template.addVariable(variableName, "", "", true);
            }


            variableNamesList.add(variableName);
        }


        LiveTemplateBuilder builder = new LiveTemplateBuilder();


        builder.insertTemplate(0, template, null);
        TemplateImpl templateComplete = builder.buildTemplate();
        System.out.println("--");
        System.out.println(templateComplete);
        System.out.println("--");
        if (templateComplete == null) {
            System.out.println("templateComplete == null");
            return;
        }
        customTemplateCallback.startTemplate(templateComplete, null, null);
    }

    @Override
    public void wrap(String s, @NotNull CustomTemplateCallback customTemplateCallback) {

    }

    @NotNull
    @Override
    public String getTitle() {
        return "customTemplate";
    }

    @Override
    public char getShortcut() {
        return 0;
    }


}
