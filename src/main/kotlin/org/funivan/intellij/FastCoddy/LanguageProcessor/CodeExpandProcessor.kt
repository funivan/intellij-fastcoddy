package org.funivan.intellij.FastCoddy.LanguageProcessor

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import org.funivan.intellij.FastCoddy.CodeBuilders.CodeBuilder
import org.funivan.intellij.FastCoddy.CodeBuilders.CodeTemplate

/**
 * @author Ivan Shcherbak <alotofall@gmail.com>
 */
class CodeExpandProcessor(private val codeBuilder: CodeBuilder) : CodeExpandInterface {

    override fun getCode(anActionEvent: AnActionEvent): CodeTemplate? {
        val shortCode = detectShortCode(anActionEvent)
        if (shortCode == null || shortCode.isEmpty()) {
            return null
        }
        val psiFile = CommonDataKeys.PSI_FILE.getData(anActionEvent.dataContext)
        return codeBuilder.expandCodeFromShortcut(shortCode, psiFile)
    }

    /**
     * if we have following code
     * ```
     * if(<CURSOR>){
     *
     *
     * }
     * ```
     * We type `!e&isf` Then our code is
     * ```
     * if(!e&isf){
     *
     *
     * }
     * ```
     * This method detect typed short code and return `!e&isf`
    </CURSOR> */
    private fun detectShortCode(anActionEvent: AnActionEvent): String? {
        val dataContext = anActionEvent.dataContext
        val editor = PlatformDataKeys.EDITOR.getData(dataContext) ?: return null
        val offset = editor.caretModel.offset
        val lineStart = editor.caretModel.visualLineStart
        val selectionMode = editor.selectionModel
        var shortCode = selectionMode.selectedText
        if (shortCode == null && offset > 0) {
            var leftPosition = offset
            var captureItem = true
            var maxChars = 100
            do {
                if (leftPosition <= lineStart) {
                    break
                }
                leftPosition--
                selectionMode.setSelection(leftPosition, offset)
                shortCode = selectionMode.selectedText
                val symbol = shortCode!!.substring(0, 1)
                if (!validSymbolForCapture(symbol)) {
                    captureItem = false
                    selectionMode.setSelection(leftPosition + 1, offset)
                }
                maxChars--
            } while (captureItem && leftPosition > 0 && maxChars > 0)
        }
        shortCode = selectionMode.selectedText
        return if (shortCode == null || shortCode.isEmpty()) {
            null
        } else shortCode
    }

    private fun validSymbolForCapture(symbol: String): Boolean {
        return symbol != " " && !codeBuilder.delimiters.contains(symbol)
    }

}
