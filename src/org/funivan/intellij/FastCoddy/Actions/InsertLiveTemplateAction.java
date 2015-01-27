package org.funivan.intellij.FastCoddy.Actions;

import com.intellij.codeInsight.template.CustomTemplateCallback;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import org.funivan.intellij.FastCoddy.CodeBuilders.IntellijLiveTemplate;

/**
 * @author funivan <dev@funivan.com>
 *         Date: 12/24/13
 */
public class InsertLiveTemplateAction {

    protected IntellijLiveTemplate template;
    protected PsiElement el;
    protected Editor editor;

    public InsertLiveTemplateAction(IntellijLiveTemplate template, PsiElement el, Editor editor) {
        this.template = template;
        this.el = el;
        this.editor = editor;
    }

    public void run() {

        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                CommandProcessor.getInstance().executeCommand(el.getProject(), new Runnable() {
                    public void run() {

                        System.out.println("deletion start");

                        int selectionStart = editor.getSelectionModel().getSelectionStart();
                        int selectionEnd = editor.getSelectionModel().getSelectionEnd();


                        CustomTemplateCallback customTemplateCallback = new CustomTemplateCallback(editor, el.getContainingFile());

                        template.expandTemplate(customTemplateCallback);
                        // delete our shortcut
                        editor.getDocument().deleteString(selectionStart, selectionEnd);
                    }
                }, "Expand custom template", "CUSTOM_TEMPLATE_GENERATOR");
            }
        });
    }
}
