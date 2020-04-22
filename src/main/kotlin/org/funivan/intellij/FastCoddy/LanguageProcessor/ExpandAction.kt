package org.funivan.intellij.FastCoddy.LanguageProcessor

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.psi.PsiManager
import org.codehaus.jettison.json.JSONException
import org.funivan.intellij.FastCoddy.Actions.InsertLiveTemplateAction
import org.funivan.intellij.FastCoddy.CodeBuilders.IntellijLiveTemplate
import org.funivan.intellij.FastCoddy.FastCoddyAppComponent
import org.funivan.intellij.FastCoddy.Productivity.UsageStatistic

/**
 * @author Ivan Shcherbak <alotofall@gmail.com>
 */
class ExpandAction : AnAction() {
    override fun actionPerformed(anActionEvent: AnActionEvent) {
        val dataContext = anActionEvent.dataContext
        val editor = PlatformDataKeys.EDITOR.getData(dataContext) ?: return
        val project = editor.project ?: return
        val virtualFile = PlatformDataKeys.VIRTUAL_FILE.getData(dataContext) ?: return
        val psiFile = PsiManager.getInstance(editor.project!!).findFile(virtualFile) ?: return
        val offset = editor.caretModel.offset - 1
        val el = psiFile.findElementAt(offset) ?: return
        var languageId = el.language.id
        // check if we have injected part of string
        val injectedElementAt = InjectedLanguageManager.getInstance(psiFile.project).findInjectedElementAt(psiFile, offset)
        if (injectedElementAt != null) {
            languageId = injectedElementAt.language.id
        }
        var codeExpands: Map<String?, CodeExpandInterface?>? = null
        try {
            codeExpands = FastCoddyAppComponent.instance.codeExpand
        } catch (e: JSONException) {
            val notification = Notification("FastCoddy Plugin", "Load configuration error", e.message!!, NotificationType.ERROR)
            Notifications.Bus.notify(notification, project)
        }
        if (codeExpands == null) {
            return
        }
        val codeExpandAction = codeExpands[languageId] ?: return

        // remember current state
        val initialSelectedText = editor.selectionModel.selectedText
        val initialOffset = offset + 1
        val newCodeTemplate = codeExpandAction.getCode(anActionEvent)
        if (newCodeTemplate == null || newCodeTemplate.isEmpty()) {

            // restore cursor configuration
            if (initialSelectedText == null) {
                editor.selectionModel.removeSelection()
                editor.caretModel.moveToOffset(initialOffset)
            }
            return
        }
        val template = IntellijLiveTemplate(newCodeTemplate)
        val insertAction = InsertLiveTemplateAction(template, el, editor)
        insertAction.run()
        UsageStatistic.used()
        UsageStatistic.usedShortCodes(newCodeTemplate.usedShortCodesNum)
        UsageStatistic.expandedChars(newCodeTemplate.code.length)
        UsageStatistic.typedChars(newCodeTemplate.initialString.length)
    }
}
