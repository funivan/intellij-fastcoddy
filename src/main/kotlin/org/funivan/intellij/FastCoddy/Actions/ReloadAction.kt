package org.funivan.intellij.FastCoddy.Actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.ui.MessageType
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.awt.RelativePoint
import org.funivan.intellij.FastCoddy.FastCoddyAppComponent

/**
 * @author Ivan Shcherbak <alotofall@gmail.com>
 */
class ReloadAction : AnAction() {
    override fun actionPerformed(anActionEvent: AnActionEvent) {
        FastCoddyAppComponent.instance.flushConfiguration()
        val editor = anActionEvent.getData(PlatformDataKeys.EDITOR) ?: return
        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder("FastCoddy. Reload done", MessageType.INFO, null)
                .setFadeoutTime(3500)
                .createBalloon()
                .show(RelativePoint.getSouthEastOf(editor.component), Balloon.Position.above)
    }
}
