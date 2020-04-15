package org.funivan.intellij.FastCoddy.Actions

import com.intellij.codeInsight.template.CustomTemplateCallback
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import org.funivan.intellij.FastCoddy.CodeBuilders.IntellijLiveTemplate

/**
 * @author Ivan Shcherbak <alotofall@gmail.com>
 */
class InsertLiveTemplateAction(protected var template: IntellijLiveTemplate, private val el: PsiElement, private val editor: Editor) {
    fun run() {
        ApplicationManager.getApplication().runWriteAction {
            CommandProcessor.getInstance().executeCommand(el.project, {
                val selectionStart = editor.selectionModel.selectionStart
                val selectionEnd = editor.selectionModel.selectionEnd
                val customTemplateCallback = CustomTemplateCallback(editor, el.containingFile)
                template.expandTemplate(customTemplateCallback)
                // delete our shortcut
                editor.document.deleteString(selectionStart, selectionEnd)
            }, "Expand custom template", "CUSTOM_TEMPLATE_GENERATOR")
        }
    }

}
