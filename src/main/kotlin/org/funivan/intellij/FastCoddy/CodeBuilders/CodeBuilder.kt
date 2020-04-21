package org.funivan.intellij.FastCoddy.CodeBuilders

import com.intellij.psi.PsiFile
import org.codehaus.jettison.json.JSONException
import org.codehaus.jettison.json.JSONObject
import org.funivan.intellij.FastCoddy.CodeBuilders.Configuration.TemplateItem
import org.funivan.intellij.FastCoddy.CodeBuilders.Configuration.VariableConfiguration
import org.funivan.intellij.FastCoddy.FastCoddyAppComponent
import org.funivan.intellij.FastCoddy.Helper.FileHelper
import java.util.*
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

/**
 * @author Ivan Shcherbak <alotofall@gmail.com>
 */
class CodeBuilder(filePath: String) : CodeBuilderInterface {
    private val templateItems: ArrayList<TemplateItem>
    val delimiters: ArrayList<String?>

    /**
     * Return new code string
     */
    override fun expandCodeFromShortcut(shortcut: String): CodeTemplate? {
        val list = getShortcutItems(shortcut)
        val newCodeTemplate = getNewCode(list)
        newCodeTemplate.initialString = shortcut
        newCodeTemplate.usedShortCodesNum = list.size
        return newCodeTemplate
    }

    private fun loadConfigFromFile(filePath: String) {
        val jsonString = FileHelper.getFileContent(filePath)
        if (jsonString.isEmpty()) {
            return
        }
        val obj = JSONObject(jsonString)
        if (!obj.has("items")) {
            throw JSONException("Empty items section inside the file: $filePath")
        }
        if (!obj.has("delimiters")) {
            throw JSONException("Empty delimiters section inside the file: $filePath")
        }
        val delimiters = obj.getJSONArray("delimiters")
        for (delimiterIndex in delimiters.length() - 1 downTo 0) {
            val itemDelimiter = delimiters.getString(delimiterIndex)
            this.delimiters.add(itemDelimiter)
        }
        val itemTemplates = obj.getJSONArray("items")
        for (i in itemTemplates.length() - 1 downTo 0) {
            val itemConfiguration = itemTemplates.getJSONObject(i)
            if (itemConfiguration is JSONObject) {
                val templateItem = TemplateItem.initFromJson(itemConfiguration)
                if (templateItem != null) {
                    templateItems.add(templateItem)
                }
            }
        }
    }

    /**
     *
     *
     * we have string if!e
     * iterate
     * if is top than i in our config so take if and cut is
     *
     *
     * we have string !e
     * iterate over config and match. ! is matched so cut it
     *
     *
     * we have string e
     *
     *
     */
    private fun getNewCode(localShortcutItemList: List<LocalShortcutItem>): CodeTemplate {
        var newCode = ""
        val variablesConfiguration = LinkedHashMap<String, VariableConfiguration>()
        val insertedTabs: MutableList<String> = ArrayList()
        for (index in localShortcutItemList.indices) {
            val localShortcutItem = localShortcutItemList[index]
            val shortcutKey = localShortcutItem.key
            var shortcutTpl = localShortcutItem.code
            val item = templateItems[shortcutKey]

            // merge variables
            val variables = localShortcutItem.templateItem.vars
            variables.forEach { key, value -> variablesConfiguration[key] = value }

            // prepare new template
            shortcutTpl = shortcutTpl.replace("\$TAB([0-9]+)\\$".toRegex(), "\$TAB_" + index + "_$1\\$")
            if (newCode.isEmpty() || index == 0) {
                newCode = shortcutTpl
            } else {
                val insertPosition = getInsertPosition(localShortcutItemList, item, index)
                newCode = newCode.replace(insertPosition, shortcutTpl + insertPosition)
                insertedTabs.add(insertPosition)
            }
            // refresh end position
            newCode = newCode.replace("\$END\$", "")
            newCode += "\$END\$"
        }
        newCode = newCode.replace("\$LAST\$", "")
        newCode = newCode.replace("\$END\$", "")
        newCode = newCode.replace(Regex("(\$TAB_[0-9]+_[0-9]+\\$)+"), "$1")


        // remove previous inserted positions
        for (tabId in insertedTabs) {
            newCode = newCode.replace(tabId, "")
        }
        return CodeTemplate(newCode, variablesConfiguration)
    }

    private fun getInsertPosition(shortCodeConfiguration: List<LocalShortcutItem>, item: TemplateItem, prevIndex: Int): String {
        var iTmp = 0
        var insertPosition = ""
        var leftPreviousIndexes = prevIndex
        do {
            leftPreviousIndexes--
            if (leftPreviousIndexes < 0) {
                break
            }
            val previousLocalShortcutItem = shortCodeConfiguration[leftPreviousIndexes]
            val prevItem = templateItems[previousLocalShortcutItem.key]
            if (prevItem.hasTabs()) {
                // detect in what place we need to insert our code
                val tabs = prevItem.tabs
                val keys: Iterator<*> = tabs.keys.iterator()
                var detectPosition = false
                if (insertPosition != "") {
                    break
                }
                while (keys.hasNext() && !detectPosition) {
                    val tabIndex = keys.next() as String
                    val currentTabGroups = tabs[tabIndex]
                    val itemGroup = item.group
                    val tabGroupsLen = currentTabGroups!!.size
                    if (tabGroupsLen == 0) {
                        insertPosition = "$" + tabIndex.replace("TAB", "TAB_" + leftPreviousIndexes + '_') + "$"
                    } else {
                        for (placeToGroup in currentTabGroups) {
                            if (placeToGroup == itemGroup) {
                                insertPosition = "$" + tabIndex.replace("TAB", "TAB_" + leftPreviousIndexes + '_') + "$"
                                detectPosition = true
                                break
                            }
                        }
                    }
                }
            }
            iTmp++
        } while (prevIndex > 0 && insertPosition.isEmpty() && iTmp < 50)
        // default position is end
        if (insertPosition.isEmpty()) {
            insertPosition = "\$END$"
        }
        return insertPosition
    }

    /**
     * We have following string `!e&isf` each word represented as LocalShortcutItem
     * ! - LocalShortcutItem    (not)
     * e - LocalShortcutItem    empty
     * & - LocalShortcutItem    and
     * isf - LocalShortcutItem  is_file
     *
     * In this method we detect pars from string according to our configuration
     */
    private fun getShortcutItems(typedString: String): List<LocalShortcutItem> {
        var typedStr = typedString
        val shortcutsExpand: MutableList<LocalShortcutItem> = ArrayList()
        typedStr = typedStr.trim { it <= ' ' }
        if (typedStr.length == 0) {
            return shortcutsExpand
        }
        while (typedStr.length > 0) {
            var added = false
            for (index in templateItems.indices.reversed()) {
                val templateItem = templateItems[index]
                typedStr = typedStr.trim { it <= ' ' }
                val shortcut = templateItem.shortcut
                if (templateItem.isRegex) {
                    val regex = "^$shortcut(.*)$"
                    var pattern: Pattern = Pattern.compile(regex)
                    val matcher = pattern.matcher(typedStr)
                    if (matcher.find()) {
                        var expandToString = templateItem.expand
                        val regexReplace = templateItem.regexReplaces
                        val groupsNum = matcher.groupCount()
                        for (groupIndex in 1..groupsNum) {
                            var groupString = matcher.group(groupIndex)
                            groupString = groupString ?: ""
                            if (groupIndex == groupsNum) {
                                typedStr = groupString
                            } else {
                                val newGroupString = regexReplace[groupString]
                                if (newGroupString != null) {
                                    groupString = newGroupString
                                }
                                expandToString = expandToString.replace("$" + groupIndex, groupString)
                            }
                        }
                        val localShortcutItem = LocalShortcutItem(index, expandToString, templateItem)
                        shortcutsExpand.add(localShortcutItem)
                        added = true
                        break
                    }
                } else if (typedStr.indexOf(shortcut) == 0) {
                    val localShortcutItem = LocalShortcutItem(index, templateItem.expand, templateItem)
                    shortcutsExpand.add(localShortcutItem)
                    typedStr = typedStr.substring(shortcut.length)
                    added = true
                    break
                }
            }
            if (!added) {
                shortcutsExpand.clear()
                return shortcutsExpand
            }
        }
        return shortcutsExpand
    }

    init {
        templateItems = ArrayList()
        delimiters = ArrayList()
        loadConfigFromFile(filePath)
    }
}
