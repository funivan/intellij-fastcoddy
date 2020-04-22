package org.funivan.intellij.FastCoddy.CodeBuilders

import org.funivan.intellij.FastCoddy.CodeBuilders.Configuration.VariableConfiguration
import java.util.*

/**
 * Final configuration of template
 * Contain expanded template, variables configuration and some statistic data (usedShortCodesNum and initialString)
 *
 * @author Ivan Shcherbak <alotofall@gmail.com>
 */
class CodeTemplate(var code: String, val variablesConfiguration: LinkedHashMap<String, VariableConfiguration>) {
    var initialString = ""
    var usedShortCodesNum = 0
    fun isEmpty() = code.isEmpty()
}
