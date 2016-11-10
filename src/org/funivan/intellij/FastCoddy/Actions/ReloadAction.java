package org.funivan.intellij.FastCoddy.Actions;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.funivan.intellij.FastCoddy.FastCoddyAppComponent;

/**
 * @author Ivan Scherbak <dev@funivan>
 */
public class ReloadAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        FastCoddyAppComponent.getInstance().flushConfiguration();
        Notification notification = new Notification("FastCoddy Plugin", "FastCoddy Plugin", "Plugin reloaded", NotificationType.INFORMATION);
        Notifications.Bus.notify(notification, anActionEvent.getProject());

    }


}



