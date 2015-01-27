package org.funivan.intellij.FastCoddy.LanguageProcessor;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.funivan.intellij.FastCoddy.CodeBuilders.CodeTemplate;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 12/25/13
 * Time: 10:37 AM
 * To change this template use File | SettingsService | File Templates.
 */
public interface CodeExpandInterface {
    
    public CodeTemplate getCode(AnActionEvent anActionEvent);

    public String[] getDelimiterSymbols();
    
}
