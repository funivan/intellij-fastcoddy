package org.funivan.intellij.FastCoddy.Settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;

import javax.swing.*;

public class ProjectSettingsForm implements Configurable {


    private JCheckBox pluginEnabled;
    private JPanel panel1;


    Project project;

    public ProjectSettingsForm(Project project) {
        this.project = project;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "FastCoddy";
    }

    @Override
    public JComponent createComponent() {
        return (JComponent) panel1;
    }


    @Override
    public void apply() throws ConfigurationException {
        getSettings().pluginEnabled = pluginEnabled.isSelected();
    }

    @Override
    public boolean isModified() {
        ProjectSettings projectSettings = getSettings();
        return pluginEnabled.isSelected() != projectSettings.pluginEnabled;
    }

    @Override
    public String getHelpTopic() {
        return null;
    }

    @Override
    public void disposeUIResources() {

    }

    @Override
    public void reset() {
        updateUIFromSettings();
    }

    private ProjectSettings getSettings() {
        return ProjectSettings.getInstance(project);
    }

    private void updateUIFromSettings() {
        pluginEnabled.setSelected(getSettings().pluginEnabled);
    }

}
