package org.funivan.intellij.FastCoddy.Productivity;

import com.intellij.featureStatistics.FeatureUsageTracker;
import com.intellij.openapi.components.*;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 */
@State(
        name = "CoddyUsageStatistic",
        storages = {
                @Storage(id = "default", file = StoragePathMacros.APP_CONFIG + "/fast-coddy.statistics.xml")
        }
)

public class UsageStatistic implements PersistentStateComponent<UsageStatistic> {

    public Integer used = 0;

    public Integer maximumShortCodes = 0;

    public Integer usedShortCodes = 0;

    public Integer typedChars = 0;

    public Integer expandedChars = 0;

    public long firstStart = 0;

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


    public static String getStatAsString() {
        UsageStatistic service = UsageStatistic.getSettings();

        String result = "";
        result += "Used                : " + service.used + "\n";
        result += "UsedShortCodes      : " + service.usedShortCodes + "\n";
        result += "TypedChars          : " + service.typedChars + "\n";
        result += "ExpandedChars       : " + service.expandedChars + "\n";
        result += "maximumShortCodes   : " + service.maximumShortCodes + "\n";

        return result;
    }

    public static void show() {
        System.out.println(getStatAsString());
    }

    @Nullable
    @Override
    public UsageStatistic getState() {
        System.out.println("UsageStatistic");

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

