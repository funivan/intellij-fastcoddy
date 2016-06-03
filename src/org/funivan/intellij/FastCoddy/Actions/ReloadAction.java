package org.funivan.intellij.FastCoddy.Actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.funivan.intellij.FastCoddy.FastCoddyAppComponent;

/**
 * @author funivan <dev@funivan.com> 12/24/13
 */
public class ReloadAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        FastCoddyAppComponent.getInstance().flushConfiguration();
    }


}



