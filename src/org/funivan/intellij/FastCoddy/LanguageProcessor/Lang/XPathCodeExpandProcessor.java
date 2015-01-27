package org.funivan.intellij.FastCoddy.LanguageProcessor.Lang;

import org.funivan.intellij.FastCoddy.CodeBuilders.CodeBuilderInterface;
import org.funivan.intellij.FastCoddy.LanguageProcessor.CodeExpandProcessor;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 12/25/13
 * Time: 10:24 AM
 */
public class XPathCodeExpandProcessor extends CodeExpandProcessor {


    public XPathCodeExpandProcessor(CodeBuilderInterface codeBuilder) {
        super(codeBuilder);
    }

    @Override
    public String[] getDelimiterSymbols() {
        return new String[]{" ", "[", "]", "/", "*"};
    }
    
}

