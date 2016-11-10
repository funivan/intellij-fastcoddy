package org.funivan.intellij.FastCoddy.Settings;

import com.intellij.openapi.options.Configurable;
import org.funivan.intellij.FastCoddy.Helper.Str;
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

        UsageStatistic statistic = UsageStatistic.getSettings();


        String result = "";

        result += "Hello\n";

        result += "You have used this action about " + Str.plural(statistic.used, "time", "times") + " ";
        result += "This plugin made about " + (Float.toString((statistic.expandedChars * 100) / (statistic.typedChars + statistic.expandedChars))) + "% of your work. \n";

        result += "You typed " + Str.plural(statistic.typedChars, "symbol", "symbols") + " and plugin expand this to " + statistic.expandedChars + " chars. ";
        result += "According to the standard live templates system you save about " + Str.plural(statistic.hotKeysEconomy, "keystroke", "keystrokes") + "\n";

        if (statistic.maximumShortCodes > 3) {
            result = result + "You are definitely cool coder. ";
        }

        result = result + "Max short codes inside you template is about " + statistic.maximumShortCodes;

        result = result + "\n\nP.S. Number of templates you are using: " + statistic.usedShortCodes;

        statPane.setText(result);

        return panel1;
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
