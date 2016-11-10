package org.funivan.intellij.FastCoddy.LanguageProcessor;

import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
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
import org.codehaus.jettison.json.JSONException;
import org.funivan.intellij.FastCoddy.Actions.InsertLiveTemplateAction;
import org.funivan.intellij.FastCoddy.CodeBuilders.CodeTemplate;
import org.funivan.intellij.FastCoddy.CodeBuilders.IntellijLiveTemplate;
import org.funivan.intellij.FastCoddy.FastCoddyAppComponent;
import org.funivan.intellij.FastCoddy.Productivity.UsageStatistic;

import java.util.Map;

/**
 * @author Ivan Scherbak <dev@funivan>
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


        VirtualFile virtualFile = PlatformDataKeys.VIRTUAL_FILE.getData(dataContext);

        if (virtualFile == null) {
            return;
        }

        PsiFile psiFile = PsiManager.getInstance(editor.getProject()).findFile(virtualFile);

        if (psiFile == null) {
            return;
        }

        int offset = editor.getCaretModel().getOffset() - 1;

        PsiElement el = psiFile.findElementAt(offset);

        if (el == null) {
            return;
        }

        String languageId = el.getLanguage().getID();


        // check if we have injected part of string
        PsiElement injectedElementAt = InjectedLanguageManager.getInstance(psiFile.getProject()).findInjectedElementAt(psiFile, offset);
        if (injectedElementAt != null) {
            languageId = injectedElementAt.getLanguage().getID();
        }


        Map<String, CodeExpandInterface> codeExpands = null;
        try {
            codeExpands = FastCoddyAppComponent.getInstance().getCodeExpand();
        } catch (JSONException e) {
            Notification notification = new Notification("FastCoddy Plugin", "Load configuration error", e.getMessage(), NotificationType.ERROR);
            Notifications.Bus.notify(notification, project);
        }

        if (codeExpands == null) {
            return;
        }

        CodeExpandInterface codeExpandAction = codeExpands.get(languageId);
        if (codeExpandAction == null) {
            return;
        }

        // remember current state
        String initialSelectedText = editor.getSelectionModel().getSelectedText();
        int initialOffset = offset + 1;

        CodeTemplate newCodeTemplate = codeExpandAction.getCode(anActionEvent);
        if (newCodeTemplate == null || newCodeTemplate.isEmpty()) {

            // restore cursor configuration
            if (initialSelectedText == null) {
                editor.getSelectionModel().removeSelection();
                editor.getCaretModel().moveToOffset(initialOffset);
            }

            return;
        }


        IntellijLiveTemplate template = new IntellijLiveTemplate(newCodeTemplate);
        InsertLiveTemplateAction insertAction = new InsertLiveTemplateAction(template, el, editor);


        insertAction.run();


        UsageStatistic.used();
        UsageStatistic.usedShortCodes(newCodeTemplate.getUsedShortCodesNum());
        UsageStatistic.expandedChars(newCodeTemplate.getCode().length());
        UsageStatistic.typedChars(newCodeTemplate.getInitialString().length());


    }


}



