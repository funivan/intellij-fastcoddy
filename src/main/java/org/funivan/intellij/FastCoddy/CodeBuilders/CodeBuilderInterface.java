package org.funivan.intellij.FastCoddy.CodeBuilders;

import com.intellij.psi.PsiFile;

import org.jetbrains.annotations.Nullable;

/**
 * @author Ivan Scherbak <dev@funivan>
 */
interface CodeBuilderInterface {

    CodeTemplate expandCodeFromShortcut(String shortcut, PsiFile psiFile);


}
