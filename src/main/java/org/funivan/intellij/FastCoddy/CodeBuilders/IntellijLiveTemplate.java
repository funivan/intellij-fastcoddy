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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ivan Scherbak <dev@funivan>
 */
public class IntellijLiveTemplate implements CustomLiveTemplate {

    private CodeTemplate codeTemplateConfigurationTemplate;

    public IntellijLiveTemplate(CodeTemplate codeTemplateConfigurationTemplate) {
        this.codeTemplateConfigurationTemplate = codeTemplateConfigurationTemplate;
    }

    @Nullable
    public String computeTemplateKey(@NotNull CustomTemplateCallback customTemplateCallback) {
        return null;
    }

    @java.lang.Override
    public boolean isApplicable(@NotNull CustomTemplateCallback customTemplateCallback, int i, boolean b) {
        return false;
    }

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
    public void expand(@NotNull String code, @NotNull CustomTemplateCallback customTemplateCallback) {

        LinkedHashMap<String, VariableConfiguration> variables = codeTemplateConfigurationTemplate.getVariablesConfiguration();
        code = code.replaceAll("\\$(TAB[0-9_]+|VAR_[0-9_A-Z]+)\\$", "__$1__");
        code = code.replace("$", "$$");
        code = code.replaceAll("__(TAB[0-9_]+|VAR_[0-9_A-Z]+)__", "\\$$1\\$");


        TemplateImpl template = new TemplateImpl("", code, "");
        template.setToReformat(true);
        template.setToIndent(true);

        Matcher variableTabMatch = Pattern.compile("\\$(TAB[0-9_]+|VAR_[0-9_A-Z]+)\\$").matcher(code);
        List<String> variableNamesList = new ArrayList<>();

        // Find all variables to the
        while (variableTabMatch.find()) {
            String variableName = variableTabMatch.group(1);

            if (variableNamesList.contains(variableName)) {
                continue;
            }

            variableNamesList.add(variableName);
        }

        List<String> addedVariableNames = new ArrayList<>();

        // Add variables to the template in the order defined inside our configuration
        for (String variableName : variables.keySet()) {
            if (addedVariableNames.contains(variableName)) {
                continue;
            }
            if (variableNamesList.contains(variableName)) {
                VariableConfiguration variableConfiguration = variables.get(variableName);
                template.addVariable(variableName, variableConfiguration.getExpression(), variableConfiguration.getDefaultValue(), variableConfiguration.getAlwaysStopAt());
                addedVariableNames.add(variableName);
            }
        }

        // Add other not defined variables according to the position inside the template
        for (String variableName : variableNamesList) {
            if (addedVariableNames.contains(variableName)) {
                continue;
            }
            template.addVariable(variableName, "", "", true);
            addedVariableNames.add(variableName);
        }


        LiveTemplateBuilder builder = new LiveTemplateBuilder();


        builder.insertTemplate(0, template, null);
        TemplateImpl templateComplete = builder.buildTemplate();

        customTemplateCallback.startTemplate(templateComplete, null, null);
    }

    @Override
    public void wrap(@NotNull String s, @NotNull CustomTemplateCallback customTemplateCallback) {

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
