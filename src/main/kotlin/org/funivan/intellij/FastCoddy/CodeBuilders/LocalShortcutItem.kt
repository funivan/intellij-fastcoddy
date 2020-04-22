package org.funivan.intellij.FastCoddy.CodeBuilders

import org.funivan.intellij.FastCoddy.CodeBuilders.Configuration.TemplateItem

/**
 * Represent one item from typed string
 *
 * @author Ivan Shcherbak <alotofall@gmail.com>
 */
internal class LocalShortcutItem(
        val key: Int, var code: String, val templateItem: TemplateItem
)
