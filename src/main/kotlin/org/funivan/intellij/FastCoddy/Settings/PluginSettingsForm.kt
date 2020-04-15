package org.funivan.intellij.FastCoddy.Settings

import com.intellij.openapi.options.Configurable
import org.funivan.intellij.FastCoddy.Helper.Str
import org.funivan.intellij.FastCoddy.Productivity.UsageStatistic
import org.jetbrains.annotations.Nls
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextPane

class PluginSettingsForm : Configurable {
    private val panel1: JPanel? = null
    private val statPane: JTextPane? = null

    @Nls
    override fun getDisplayName(): String {
        return "FastCoddy"
    }

    override fun createComponent(): JComponent? {
        val statistic: UsageStatistic = UsageStatistic.settings
        var result = ""
        result += "Hello\n"
        result += "You have used this action about " + Str.plural(statistic.used, "time", "times") + " "
        val chars = statistic.typedChars + statistic.expandedChars
        if (chars > 0) {
            result += """
                This plugin made about ${java.lang.Float.toString(statistic.expandedChars * 100 / chars.toFloat())}% of your work. 
                
                """.trimIndent()
        }
        result += "You typed " + Str.plural(statistic.typedChars, "symbol", "symbols") + " and plugin expand this to " + statistic.expandedChars + " chars. "
        result += """
            According to the standard live templates system you save about ${Str.plural(statistic.usedShortCodes!! - statistic.used, "keystroke", "keystrokes")}
            
            """.trimIndent()
        if (statistic.maximumShortCodes!! > 3) {
            result = result + "You are definitely cool coder. "
        }
        result = result + "Max short codes inside you template is about " + statistic.maximumShortCodes
        result = """
            $result
            
            P.S. You have used about ${Str.plural(statistic.usedShortCodes!!, "short code", "short codes")}
            """.trimIndent()
        statPane!!.text = result
        return panel1
    }

    override fun apply() {}
    override fun isModified(): Boolean {
        return false
    }

    override fun getHelpTopic(): String? {
        return null
    }

    override fun disposeUIResources() {}
    override fun reset() {
        updateUIFromSettings()
    }

    private fun updateUIFromSettings() {}
    private fun createUIComponents() {}
}
