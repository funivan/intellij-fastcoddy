package org.funivan.intellij.FastCoddy.LanguageProcessor;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.psi.PsiFile;
import org.funivan.intellij.FastCoddy.CodeBuilders.CodeTemplate;
import org.funivan.intellij.FastCoddy.CodeBuilders.CodeBuilderInterface;

/**
 * User: ivan
 * Date: 12/25/13
 * Time: 10:58 AM
 * To change this template use File | SettingsService | File Templates.
 */
public abstract class CodeExpandProcessor implements CodeExpandInterface {

    protected CodeBuilderInterface codeBuilder;


    public final CodeExpandProcessor setCodeBuilder(CodeBuilderInterface codeBuilder) {
        this.codeBuilder = codeBuilder;
        return this;
    }

    public final CodeBuilderInterface getCodeBuilder() {
        return codeBuilder;
    }

    @Override
    public CodeTemplate getCode(AnActionEvent anActionEvent) {

        String shortCode = detectShortCode(anActionEvent);

        if (shortCode == null || shortCode.isEmpty()) {
            return null;
        }

        PsiFile psiFile = CommonDataKeys.PSI_FILE.getData(anActionEvent.getDataContext());

        CodeTemplate codeTemplate = this.getCodeBuilder().expandCodeFromShortcut(shortCode, psiFile);

        return codeTemplate;
    }

    /**
     *
     *
     * if we have following code
     * ```
     * if(<CURSOR>){
     *
     * }
     * ```
     * We type `!e&isf` Then our code is
     * ```
     * if(!e&isf){
     *
     * }
     * ```
     * This method detect typed short code and return `!e&isf`
     *
     * @param anActionEvent
     * @return
     */
    private String detectShortCode(AnActionEvent anActionEvent) {
        DataContext dataContext = anActionEvent.getDataContext();
        Editor editor = PlatformDataKeys.EDITOR.getData(dataContext);


        int offset = editor.getCaretModel().getOffset();
        int lineStart = editor.getCaretModel().getVisualLineStart();

        SelectionModel selectionMode = editor.getSelectionModel();
        String shortCode = selectionMode.getSelectedText();

        if (shortCode == null && offset > 0) {

            Integer rightPosition = offset;
            Integer leftPosition = offset;

            Boolean captureItem = true;
            Integer maxChars = 100;


            do {

                if (leftPosition <= lineStart) {
                    break;
                }

                leftPosition--;
                selectionMode.setSelection(leftPosition, rightPosition);
                shortCode = selectionMode.getSelectedText();

                String firstSymbol = shortCode.substring(0, 1);
                if (firstSymbol.equals(" ") || firstSymbol.equals("(")) {
                    captureItem = false;
                    selectionMode.setSelection(leftPosition + 1, rightPosition);
                }

                maxChars--;
            } while (captureItem && leftPosition > 0 && maxChars > 0);
        }

        shortCode = selectionMode.getSelectedText();

        if (shortCode == null || shortCode.isEmpty()) {
            return null;
        }
        return shortCode;
    }
}
