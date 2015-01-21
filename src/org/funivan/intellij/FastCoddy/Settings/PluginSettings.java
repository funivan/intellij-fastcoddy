package org.funivan.intellij.FastCoddy.Settings;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.components.*;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;

@State(
        name = "FastCoddyGeneralSettingsService",
        storages = {
                @Storage(id = "default", file = StoragePathMacros.APP_CONFIG + "/fast-coddy.xml")
        }
)
public class PluginSettings implements PersistentStateComponent<PluginSettings> {

    public static final String DEFAULT_FULL_PATH = PathManager.getConfigPath() + "/fast-coddy";

    public String configurationDirectory = DEFAULT_FULL_PATH;

    @Nullable
    @Override
    public PluginSettings getState() {
        return this;
    }

    public static PluginSettings getSettings() {
        return ServiceManager.getService(PluginSettings.class);
    }

    @Override
    public void loadState(PluginSettings settings) {
        XmlSerializerUtil.copyBean(settings, this);
    }


}
