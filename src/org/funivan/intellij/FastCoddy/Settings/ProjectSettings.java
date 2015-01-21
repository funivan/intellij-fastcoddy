package org.funivan.intellij.FastCoddy.Settings;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;

@State(
        name = "CoddyProjectSettings",
        storages = {
                @Storage(id = "default", file = StoragePathMacros.PROJECT_FILE)
        }
)
public class ProjectSettings implements PersistentStateComponent<ProjectSettings> {

    public boolean pluginEnabled = true;

    protected Project project;

    public static ProjectSettings getInstance(Project project) {
        ProjectSettings projectSettings = ServiceManager.getService(project, ProjectSettings.class);
        projectSettings.project = project;

        return projectSettings;
    }

    @Nullable
    @Override
    public ProjectSettings getState() {
        return this;
    }

    @Override
    public void loadState(ProjectSettings projectSettings) {
        XmlSerializerUtil.copyBean(projectSettings, this);
    }


}
