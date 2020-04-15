package org.funivan.intellij.FastCoddy.CodeBuilders

import com.intellij.codeInsight.template.CustomLiveTemplate
import com.intellij.codeInsight.template.CustomTemplateCallback
import com.intellij.codeInsight.template.LiveTemplateBuilder
import com.intellij.codeInsight.template.impl.TemplateImpl
import com.intellij.codeInsight.template.impl.TemplateSettings
import org.funivan.intellij.FastCoddy.CodeBuilders.Configuration.VariableConfiguration
import java.util.*
import java.util.regex.Pattern

/**
 * @author Ivan Shcherbak <alotofall@gmail.com>
 */
class IntellijLiveTemplate(private val codeTemplateConfigurationTemplate: CodeTemplate) : CustomLiveTemplate {
    override fun computeTemplateKey(customTemplateCallback: CustomTemplateCallback): String? {
        return null
    }

    override fun isApplicable(customTemplateCallback: CustomTemplateCallback, i: Int, b: Boolean): Boolean {
        return false
    }

    override fun supportsWrapping(): Boolean {
        return false
    }

    fun expandTemplate(callback: CustomTemplateCallback) {
        expand(codeTemplateConfigurationTemplate.code, callback)
    }

    override fun expand(code: String, customTemplateCallback: CustomTemplateCallback) {
        var result = code

        result = result.replace("\\$(TAB[0-9_]+|VAR_[0-9_A-Z]+)\\$".toRegex(), "__$1__")
        result = result.replace("$", "$$")
        result = result.replace("__(TAB[0-9_]+|VAR_[0-9_A-Z]+)__".toRegex(), "\\$$1\\$")
        val template = TemplateImpl("", result, "")
        template.isToReformat = true
        template.isToIndent = true
        val variableTabMatch = Pattern.compile("\\$(TAB[0-9_]+|VAR_[0-9_A-Z]+)\\$").matcher(result)
        val variableNamesList = mutableListOf<String>()

        // Find all variables
        while (variableTabMatch.find()) {
            val variableName = variableTabMatch.group(1)
            if (!variableNamesList.contains(variableName)) {
                variableNamesList.add(variableName)
            }
        }
        val addedVariableNames: MutableList<String?> = ArrayList()

        val variables = codeTemplateConfigurationTemplate.variablesConfiguration
        // Add variables to the template in the order defined inside our configuration
        for (variableName in variables.keys) {
            if (!addedVariableNames.contains(variableName)) {
                if (variableNamesList.contains(variableName)) {
                    val variableConfiguration = variables[variableName] as VariableConfiguration
                    template.addVariable(variableName, variableConfiguration.expression, variableConfiguration.defaultValue, variableConfiguration.alwaysStopAt)
                    addedVariableNames.add(variableName)
                }
            }
        }

        // Add other not defined variables according to the position inside the template
        for (variableName in variableNamesList) {
            if (!addedVariableNames.contains(variableName)) {
                template.addVariable(variableName, "", "", true)
                addedVariableNames.add(variableName)
            }
        }
        val builder = LiveTemplateBuilder()
        builder.insertTemplate(0, template, null)
        val templateComplete = builder.buildTemplate()
        customTemplateCallback.startTemplate(templateComplete, null, null)
    }

    override fun wrap(s: String, customTemplateCallback: CustomTemplateCallback) {}
    override fun getTitle(): String {
        return "customTemplate"
    }

    override fun getShortcut(): Char {
        return TemplateSettings.TAB_CHAR
    }

}
