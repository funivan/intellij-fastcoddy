package org.funivan.intellij.FastCoddy.LanguageProcessor;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.psi.PsiFile;
import org.funivan.intellij.FastCoddy.CodeBuilders.CodeBuilder;
import org.funivan.intellij.FastCoddy.CodeBuilders.CodeTemplate;
import org.jetbrains.annotations.Nullable;

/**
 * @author Ivan Scherbak <dev@funivan>
 */
public class CodeExpandProcessor implements CodeExpandInterface {

    private CodeBuilder codeBuilder;

    public CodeExpandProcessor(CodeBuilder codeBuilder) {
        this.codeBuilder = codeBuilder;
    }

    private CodeBuilder getCodeBuilder() {
        return codeBuilder;
    }

    public CodeTemplate getCode(AnActionEvent anActionEvent) {

        String shortCode = detectShortCode(anActionEvent);

        if (shortCode == null || shortCode.isEmpty()) {
            return null;
        }

        PsiFile psiFile = CommonDataKeys.PSI_FILE.getData(anActionEvent.getDataContext());

        return this.getCodeBuilder().expandCodeFromShortcut(shortCode, psiFile);
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
     */
    @Nullable
    private String detectShortCode(AnActionEvent anActionEvent) {
        DataContext dataContext = anActionEvent.getDataContext();
        Editor editor = PlatformDataKeys.EDITOR.getData(dataContext);


        if (editor == null) {
            return null;
        }
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
                if (!validSymbolForCapture(symbol)) {
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


    private Boolean validSymbolForCapture(String symbol) {
        return !symbol.equals(" ") && !codeBuilder.getDelimiters().contains(symbol);

    }

}
