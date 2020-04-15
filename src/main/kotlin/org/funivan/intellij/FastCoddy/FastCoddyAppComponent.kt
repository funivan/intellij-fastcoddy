package org.funivan.intellij.FastCoddy

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.components.ApplicationComponent
import com.intellij.openapi.diagnostic.Logger
import org.codehaus.jettison.json.JSONException
import org.funivan.intellij.FastCoddy.CodeBuilders.CodeBuilder
import org.funivan.intellij.FastCoddy.LanguageProcessor.CodeExpandInterface
import org.funivan.intellij.FastCoddy.LanguageProcessor.CodeExpandProcessor
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * @author Ivan Shcherbak <alotofall@gmail.com>
 */
class FastCoddyAppComponent : ApplicationComponent {
    private var codeExpands: MutableMap<String?, CodeExpandInterface?>? = null

    /**
     * On first initialization copy default template files
     */
    @Throws(IOException::class)
    private fun copyTemplateFiles(forceRewrite: Boolean) {
        val defaultDir = File(DEFAULT_FULL_PATH)
        if (!defaultDir.exists()) {
            if (!defaultDir.mkdir()) {
                throw IOException("Can not create configuration directory")
            }
        }
        if (!defaultDir.exists()) {
            return
        }
        copyTemplate(forceRewrite, "php.json")
        copyTemplate(forceRewrite, "javascript.json")
        copyTemplate(forceRewrite, "xml.json")
    }

    private fun copyTemplate(forceRewrite: Boolean, fileName: String) {
        val `is` = FastCoddyAppComponent::class.java.classLoader.getResourceAsStream(fileName)
        val destinationFile = File("$DEFAULT_FULL_PATH/$fileName")
        if (`is` == null) {
            println("fs is null")
            return
        }
        if (destinationFile.isFile && !forceRewrite) {
            return
        }
        try {
            Files.copy(`is`, destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun initComponent() {
        try {
            copyTemplateFiles(false)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun disposeComponent() {}
    override fun getComponentName(): String {
        return "FastCoddyAppComponent"
    }

    fun flushConfiguration() {
        codeExpands = null
    }

    /**
     * Load code expands
     */
    @get:Throws(JSONException::class)
    val codeExpand: Map<String?, CodeExpandInterface?>?
        get() {
            if (codeExpands == null) {
                val dir = PathManager.getConfigPath() + "/fast-coddy"
                codeExpands = hashMapOf(
                        "PHP" to CodeExpandProcessor(CodeBuilder("$dir/php.json")),
                        "XML" to CodeExpandProcessor(CodeBuilder("$dir/xml.json")),
                        "JavaScript" to CodeExpandProcessor(CodeBuilder("$dir/javascript.json"))
                )
            }
            return codeExpands
        }

    companion object {
        val LOG = Logger.getInstance("FastCoddy-Plugin")
        private val DEFAULT_FULL_PATH = PathManager.getConfigPath() + "/fast-coddy"
        val instance: FastCoddyAppComponent
            get() = ApplicationManager.getApplication().getComponent(FastCoddyAppComponent::class.java)
    }
}
