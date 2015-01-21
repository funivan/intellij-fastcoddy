package org.funivan.intellij.FastCoddy.CodeBuilders;

import com.intellij.psi.PsiFile;

import javax.annotation.Nullable;

/**
 * User: funivan
 * Date: 12/25/13
 */
public interface CodeBuilderInterface {

    public CodeTemplate expandCodeFromShortcut(String shortcut, PsiFile psiFile);

    public void loadConfigFromFile(String filePath, @Nullable String projectDirectory);

}
