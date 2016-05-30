package org.funivan.intellij.FastCoddy.Settings;

import com.intellij.openapi.options.Configurable;
import org.funivan.intellij.FastCoddy.Productivity.UsageStatistic;
import org.jetbrains.annotations.Nls;

import javax.swing.*;

public class PluginSettingsForm implements Configurable {


    private JPanel panel1;
    private JTextPane statPane;

    @Nls
    @Override
    public String getDisplayName() {
        return "FastCoddy";
    }

    @Override
    public JComponent createComponent() {

        String statAsString = UsageStatistic.getStatAsString();
        statPane.setText("Stat:\n" + statAsString);
        return (JComponent) panel1;
    }


    @Override
    public void apply() {
    }

    @Override
    public boolean isModified() {
        return false;
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


    private void updateUIFromSettings() {

    }


    private void createUIComponents() {

    }
}
