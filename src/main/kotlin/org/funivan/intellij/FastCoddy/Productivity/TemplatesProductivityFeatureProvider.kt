package org.funivan.intellij.FastCoddy.Productivity

import com.intellij.featureStatistics.ApplicabilityFilter
import com.intellij.featureStatistics.FeatureDescriptor
import com.intellij.featureStatistics.GroupDescriptor
import com.intellij.featureStatistics.ProductivityFeaturesProvider
import com.intellij.openapi.components.ApplicationComponent
import com.intellij.openapi.project.Project
import java.util.*

/**
 * @author Ivan Shcherbak <alotofall@gmail.com>
 */
class TemplatesProductivityFeatureProvider : ProductivityFeaturesProvider(), ApplicationComponent {
    private var descriptors: Array<FeatureDescriptor> = arrayOf()
    override fun disposeComponent() {}
    override fun getComponentName(): String {
        return "TemplatesProductivityFeatureProvider"
    }

    override fun initComponent() {
        val list: MutableList<FeatureDescriptor> = ArrayList()
        list.add(createFeatureDescriptor())
        descriptors = list.toTypedArray()
    }

    private fun createFeatureDescriptor(): FeatureDescriptor {
        return FeatureDescriptor(LIVE_TEMPLATE_INVOKE, GROUP_DESCRIPTOR_ID, "TemplateInvoke.html",
                "Expand template",
                0, 1, null, 1, this)
    }

    override fun getFeatureDescriptors(): Array<FeatureDescriptor> {
        return descriptors
    }

    override fun getGroupDescriptors(): Array<GroupDescriptor> {
        return arrayOf(GroupDescriptor(GROUP_DESCRIPTOR_ID, "FastCoddy"))
    }

    override fun getApplicabilityFilters(): Array<ApplicabilityFilter> {
        return arrayOf(
                object : ApplicabilityFilter {
                    override fun getPrefix(): String {
                        return GROUP_DESCRIPTOR_ID
                    }

                    override fun isApplicable(string: String, project: Project): Boolean {
                        return true
                    }
                }
        )
    }

    companion object {
        private const val GROUP_DESCRIPTOR_ID = "fast-coddy.feature"
        const val LIVE_TEMPLATE_INVOKE = "fast-coddy.template.invoke"
    }
}
