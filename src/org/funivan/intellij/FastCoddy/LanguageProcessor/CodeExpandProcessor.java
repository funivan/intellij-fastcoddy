package org.funivan.intellij.FastCoddy.LanguageProcessor;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.psi.PsiFile;
import org.funivan.intellij.FastCoddy.CodeBuilders.CodeBuilderInterface;
import org.funivan.intellij.FastCoddy.CodeBuilders.CodeTemplate;

import java.util.Arrays;

/**
 * User: ivan
 * Date: 12/25/13
 * Time: 10:58 AM
 * To change this template use File | SettingsService | File Templates.
 */
public abstract class CodeExpandProcessor implements CodeExpandInterface {

    public CodeBuilderInterface codeBuilder;
    protected String[] delimiterSymbols;

    public CodeExpandProcessor(CodeBuilderInterface codeBuilder) {
        this.codeBuilder = codeBuilder;
        this.delimiterSymbols = getDelimiterSymbols();
    }

    public final CodeBuilderInterface getCodeBuilder() {
        return codeBuilder;
    }

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
     * if we have following code
     * ```
     * if(<CURSOR>){
     * <p/>
     * }
     * ```
     * We type `!e&isf` Then our code is
     * ```
     * if(!e&isf){
     * <p/>
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

                String symbol = shortCode.substring(0, 1);
                if (validSymbolForCapture(symbol) == false) {
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


    protected Boolean validSymbolForCapture(String symbol) {

        if (Arrays.asList(this.delimiterSymbols).contains(symbol.toString())) {
            return false;
        }

        return true;
    }

}
