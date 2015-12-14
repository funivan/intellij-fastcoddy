package org.funivan.intellij.FastCoddy.Actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.funivan.intellij.FastCoddy.CoddyAppComponent;

/**
 * @author funivan <dev@funivan.com> 12/24/13
 */
public class ReloadAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        CoddyAppComponent.getInstance().flushConfiguration();
    }


}



