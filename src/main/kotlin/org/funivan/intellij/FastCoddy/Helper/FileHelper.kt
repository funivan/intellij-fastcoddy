package org.funivan.intellij.FastCoddy.Helper

import java.io.File
import java.io.FileReader
import java.io.IOException

/**
 * @author Ivan Shcherbak <alotofall@gmail.com>
 */
object FileHelper {
    /**
     * Helper function for reading file
     */
    fun getFileContent(filename: String): String {
        var content: String = ""
        val file = File(filename)
        if (!file.exists()) {
            return ""
        }
        try {
            val reader = FileReader(file)
            val chars = CharArray(file.length().toInt())
            reader.read(chars)
            content = String(chars)
            reader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return content
    }
}
