package org.funivan.intellij.FastCoddy.LanguageProcessor;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.funivan.intellij.FastCoddy.CodeBuilders.CodeTemplate;

/**
 * @author Ivan Scherbak <dev@funivan>
 */
public interface CodeExpandInterface {
    
    CodeTemplate getCode(AnActionEvent anActionEvent);

    
}
