package org.funivan.intellij.FastCoddy.LanguageProcessor.Lang;

import org.funivan.intellij.FastCoddy.CodeBuilders.CodeBuilderInterface;
import org.funivan.intellij.FastCoddy.LanguageProcessor.CodeExpandProcessor;

/**
 * @author Ivan Scherbak <dev@funivan>
 */
public class JavaScriptCodeExpandProcessor extends CodeExpandProcessor {


    public JavaScriptCodeExpandProcessor(CodeBuilderInterface codeBuilder) {
        super(codeBuilder);
    }

    @Override
    public String[] getDelimiterSymbols() {
        return new String[]{" ", "("};
    }
}
