package org.funivan.intellij.FastCoddy.Productivity

import com.intellij.featureStatistics.FeatureUsageTracker
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

/**
 * @author Ivan Shcherbak <alotofall@gmail.com>
 */
@State(name = "CoddyUsageStatistic", storages = [Storage("fast-coddy.statistics.xml")])
class UsageStatistic : PersistentStateComponent<UsageStatistic?> {
    var used = 0
    var maximumShortCodes: Int = 0
    var usedShortCodes: Int = 0
    var typedChars = 0
    var expandedChars = 0
    private var firstStart: Long = 0
    override fun getState(): UsageStatistic? {
        if (firstStart == 0L) {
            firstStart = System.currentTimeMillis()
        }
        return this
    }

    companion object {
        fun used() {
            FeatureUsageTracker.getInstance().triggerFeatureUsed(TemplatesProductivityFeatureProvider.Companion.LIVE_TEMPLATE_INVOKE)
            settings.used++
        }

        fun usedShortCodes(shortCodesNum: Int?) {
            val statistic = settings
            if (shortCodesNum!! > statistic.maximumShortCodes!!) {
                statistic.maximumShortCodes = shortCodesNum
            }
            statistic.usedShortCodes += shortCodesNum
        }

        fun typedChars(charsNum: Int) {
            settings.typedChars += charsNum
        }

        fun expandedChars(charsNum: Int) {
            settings.expandedChars += charsNum
        }

        val settings: UsageStatistic
            get() = ServiceManager.getService(UsageStatistic::class.java)
    }

    override fun loadState(state: UsageStatistic) {
        XmlSerializerUtil.copyBean(state, this)
    }
}
