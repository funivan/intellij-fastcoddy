package org.funivan.intellij.FastCoddy.CodeBuilders.Configuration

import org.codehaus.jettison.json.JSONObject
import java.util.*

/**
 * Template item defined in the configuration file
 *
 * @author Ivan Shcherbak <alotofall@gmail.com>
 */
class TemplateItem(
        val shortcut: String,
        val expand: String,
        val group: String,
        val isRegex: Boolean,
        val regexReplaces: Map<String, String>,
        val tabs: Map<String, Array<String?>>,
        val vars: Map<String, VariableConfiguration>
) {


    fun hasTabs(): Boolean {
        return tabs.size > 0
    }


    companion object {
        fun initFromJson(configuration: JSONObject): TemplateItem? {
            if (!configuration.has("shortcut")) {
                return null
            }
            val shortcut = configuration.getString("shortcut")
            if (shortcut.length == 0) {
                return null
            }
            if (!configuration.has("expand")) {
                return null
            }
            val group = if (configuration.has("group")) {
                configuration.getString("group")
            } else {
                shortcut
            }
            val regexReplaces = mutableMapOf<String, String>()
            if (configuration.has("regexReplaces")) {
                val regexReplace = configuration.getJSONObject("regexReplaces")
                val keys = regexReplace.keys()
                while (keys.hasNext()) {
                    val from = keys.next()
                    if (from is String) {
                        val to = regexReplace.getString(from)
                        regexReplaces[from] = to
                    }
                }
            }
            val tabs = mutableMapOf<String, Array<String?>>()
            if (configuration.has("tabs")) {
                val jsonTabs = configuration.getJSONObject("tabs")
                val tabKeys = jsonTabs.keys()
                while (tabKeys.hasNext()) {
                    val tabName = tabKeys.next()
                    if (tabName is String) {
                        val tabData = jsonTabs.getJSONArray(tabName)
                        val tabInclude = arrayOfNulls<String>(tabData.length())
                        for (i in 0 until tabData.length()) {
                            tabInclude[i] = tabData.getString(i)
                        }
                        tabs[tabName] = tabInclude
                    }
                }
            }
            val vars = LinkedHashMap<String, VariableConfiguration>()
            if (configuration.has("vars")) {
                val variablesConfig = configuration.getJSONObject("vars")
                val varsNames = variablesConfig.keys()
                while (varsNames.hasNext()) {
                    val variableName = varsNames.next() as String
                    val variable = variablesConfig.getJSONObject(variableName)
                    val expression = if (variable.has("expression")) variable.getString("expression") else ""
                    val defaultValue = if (variable.has("defaultValue")) variable.getString("defaultValue") else ""
                    val alwaysStopAt = variable.has("alwaysStopAt") && variable.getBoolean("alwaysStopAt")
                    vars[variableName] = VariableConfiguration(expression, defaultValue, alwaysStopAt)
                }
            }
            return TemplateItem(
                    shortcut,
                    configuration.getString("expand"),
                    group,
                    configuration.has("isRegex") && configuration.getBoolean("isRegex"),
                    regexReplaces,
                    tabs,
                    vars
            )
        }
    }
}
