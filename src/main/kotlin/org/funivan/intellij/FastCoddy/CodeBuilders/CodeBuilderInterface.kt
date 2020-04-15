package org.funivan.intellij.FastCoddy.CodeBuilders;

import com.intellij.psi.PsiFile;

import org.jetbrains.annotations.Nullable;

/**
 * @author Ivan Shcherbak <alotofall@gmail.com>
 */
interface CodeBuilderInterface {

    CodeTemplate expandCodeFromShortcut(String shortcut, PsiFile psiFile);


}
