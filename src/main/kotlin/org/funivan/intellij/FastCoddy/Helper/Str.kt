package org.funivan.intellij.FastCoddy.Helper

/**
 * @author Ivan Shcherbak <alotofall@gmail.com>
 */
object Str {
    fun plural(num: Int, singular: String, plural: String): String {
        return if (num == 1) {
            Integer.toString(num) + ' ' + singular
        } else Integer.toString(num) + ' ' + plural
    }
}
