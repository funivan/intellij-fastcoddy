package org.funivan.intellij.FastCoddy.LanguageProcessor;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.funivan.intellij.FastCoddy.CodeBuilders.CodeTemplate;
import org.funivan.intellij.FastCoddy.Actions.InsertLiveTemplateAction;
import org.funivan.intellij.FastCoddy.CodeBuilders.IntellijLiveTemplate;
import org.funivan.intellij.FastCoddy.Productivity.UsageStatistic;
import org.funivan.intellij.FastCoddy.CoddyProjectComponent;
import org.funivan.intellij.FastCoddy.Settings.ProjectSettings;

import java.util.Map;

/**
 * @author funivan
 */
public class ExpandAction extends AnAction {


    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        DataContext dataContext = anActionEvent.getDataContext();
        Editor editor = PlatformDataKeys.EDITOR.getData(dataContext);

        if (editor == null) {
            return;
        }

        Project project = editor.getProject();

        if (project == null) {
            return;
        }

        ProjectSettings projectSettings = ProjectSettings.getInstance(project);

        if (projectSettings == null || projectSettings.pluginEnabled == false) {
            return;
        }


        VirtualFile virtualFile = PlatformDataKeys.VIRTUAL_FILE.getData(dataContext);
        PsiFile psiFile = PsiManager.getInstance(editor.getProject()).findFile(virtualFile);
        PsiElement el = psiFile.findElementAt(editor.getCaretModel().getOffset() - 1);

        if (el == null) {
            return;
        }


        CoddyProjectComponent c = (CoddyProjectComponent) project.getComponent("CoddyModuleComponent" + project.getName());

        if (c == null) {
            return;
        }

        Map<String, CodeExpandInterface> codeExpands = c.getCodeExpand();

        String languageId = el.getLanguage().getID();


        CodeExpandInterface codeExpandAction = codeExpands.get(languageId);
        if (codeExpandAction == null) {
            System.out.println("empty codeExpandActions for language:" + languageId);
            return;
        }

        // remember current state
        String initialSelectedText = editor.getSelectionModel().getSelectedText();
        int initialOffset = editor.getCaretModel().getOffset();
        System.out.println("save:" + initialOffset);
        CodeTemplate newCodeTemplate = codeExpandAction.getCode(anActionEvent);
        if (newCodeTemplate == null || newCodeTemplate.isEmpty()) {

            // restore cursor configuration
            if (initialSelectedText == null) {
                System.out.println("restore:" + initialOffset);
                editor.getSelectionModel().removeSelection();
                editor.getCaretModel().moveToOffset(initialOffset);
            }

            return;
        }

//        System.out.println("newCodeTemplate" + newCodeTemplate);
        IntellijLiveTemplate template = new IntellijLiveTemplate(newCodeTemplate);
        InsertLiveTemplateAction insertAction = new InsertLiveTemplateAction(template, el, editor);
        insertAction.run();

        UsageStatistic.used();
        UsageStatistic.usedShortCodes(newCodeTemplate.getUsedShortCodesNum());
        UsageStatistic.expandedChars(newCodeTemplate.getCode().length());
        UsageStatistic.typedChars(newCodeTemplate.getInitialString().length());

        UsageStatistic.show();
    }


}



