package org.funivan.intellij.FastCoddy.Actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.awt.RelativePoint;
import org.funivan.intellij.FastCoddy.FastCoddyAppComponent;

/**
 * @author Ivan Scherbak <dev@funivan>
 */
public class ReloadAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        FastCoddyAppComponent.getInstance().flushConfiguration();

        Editor editor = anActionEvent.getData(PlatformDataKeys.EDITOR);

        if (editor == null) {
            return;
        }

        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder("FastCoddy. Reload done", MessageType.INFO, null)
                .setFadeoutTime(3500)
                .createBalloon()
                .show(RelativePoint.getSouthEastOf(editor.getComponent()), Balloon.Position.above);

    }


}



