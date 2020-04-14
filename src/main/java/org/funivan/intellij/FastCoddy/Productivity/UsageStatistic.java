package org.funivan.intellij.FastCoddy.Productivity;

import com.intellij.featureStatistics.FeatureUsageTracker;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;

/**
 * @author Ivan Shcherbak <alotofall@gmail.com>
 */
@State(
    name = "CoddyUsageStatistic",
    storages = {
        @Storage("fast-coddy.statistics.xml")
    }
)
public class UsageStatistic implements PersistentStateComponent<UsageStatistic> {

    public Integer used = 0;

    public Integer maximumShortCodes = 0;

    public Integer usedShortCodes = 0;

    public Integer typedChars = 0;

    public Integer expandedChars = 0;

    private long firstStart = 0;

    public static void used() {
        FeatureUsageTracker.getInstance().triggerFeatureUsed(TemplatesProductivityFeatureProvider.LIVE_TEMPLATE_INVOKE);

        UsageStatistic.getSettings().used++;
    }

    public static void usedShortCodes(Integer shortCodesNum) {
        UsageStatistic statistic = UsageStatistic.getSettings();

        if (shortCodesNum > statistic.maximumShortCodes) {
            statistic.maximumShortCodes = shortCodesNum;
        }

        statistic.usedShortCodes += shortCodesNum;
    }


    public static void typedChars(Integer charsNum) {
        UsageStatistic.getSettings().typedChars += charsNum;
    }

    public static void expandedChars(Integer charsNum) {
        UsageStatistic.getSettings().expandedChars += charsNum;
    }


    @Nullable
    @Override
    public UsageStatistic getState() {

        if (firstStart == 0) {
            firstStart = System.currentTimeMillis();
        }
        return this;
    }

    public static UsageStatistic getSettings() {
        return ServiceManager.getService(UsageStatistic.class);
    }

    @Override
    public void loadState(UsageStatistic settings) {
        XmlSerializerUtil.copyBean(settings, this);
    }

}

