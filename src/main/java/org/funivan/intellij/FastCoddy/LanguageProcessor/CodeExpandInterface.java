package org.funivan.intellij.FastCoddy.LanguageProcessor;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.funivan.intellij.FastCoddy.CodeBuilders.CodeTemplate;

/**
 * @author Ivan Shcherbak <alotofall@gmail.com>
 */
public interface CodeExpandInterface {
    
    CodeTemplate getCode(AnActionEvent anActionEvent);

    
}
