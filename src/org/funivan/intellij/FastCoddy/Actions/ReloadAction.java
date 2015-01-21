package org.funivan.intellij.FastCoddy.Actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.funivan.intellij.FastCoddy.CoddyProjectComponent;

/**
 * @author funivan <dev@funivan.com>
 * Date: 12/24/13
 */
public class ReloadAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        if (project == null) {
            return;
        }
        CoddyProjectComponent projectComponent = CoddyProjectComponent.getProjectComponent(project);
        if (projectComponent == null) {
            return;
        }
        projectComponent.flushConfiguration();
    }


}



