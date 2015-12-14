package org.funivan.intellij.FastCoddy.CodeBuilders;

import com.intellij.psi.PsiFile;

import org.jetbrains.annotations.Nullable;

/**
 * User: funivan
 * Date: 12/25/13
 */
public interface CodeBuilderInterface {

    CodeTemplate expandCodeFromShortcut(String shortcut, PsiFile psiFile);

    void loadConfigFromFile(String filePath, @Nullable String projectDirectory);

}
