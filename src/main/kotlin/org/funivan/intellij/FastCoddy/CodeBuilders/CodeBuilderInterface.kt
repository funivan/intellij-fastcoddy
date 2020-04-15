package org.funivan.intellij.FastCoddy.CodeBuilders

import com.intellij.psi.PsiFile

/**
 * @author Ivan Shcherbak <alotofall@gmail.com>
 */
internal interface CodeBuilderInterface {
    fun expandCodeFromShortcut(shortcut: String, psiFile: PsiFile?): CodeTemplate?
}
