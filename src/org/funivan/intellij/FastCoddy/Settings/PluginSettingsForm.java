package org.funivan.intellij.FastCoddy.Settings;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import org.funivan.intellij.FastCoddy.CoddyModuleComponent;
import org.funivan.intellij.FastCoddy.Listeners.CustomMouseListener;
import org.funivan.intellij.FastCoddy.Productivity.UsageStatistic;
import org.jetbrains.annotations.Nls;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class PluginSettingsForm implements Configurable {


    private JPanel panel1;
    private JButton resetPathButton;
    private TextFieldWithBrowseButton configurationDirectory;
    private JButton resetConfigurationFilesButton;
    private JTextPane statPane;

    @Nls
    @Override
    public String getDisplayName() {
        return "FastCoddy";
    }

    @Override
    public JComponent createComponent() {

        configurationDirectory.getButton().addMouseListener(createPathButtonMouseListener(configurationDirectory.getTextField(), FileChooserDescriptorFactory.createSingleFolderDescriptor()));
        resetPathButton.addMouseListener(createResetPathButtonMouseListener(configurationDirectory.getTextField(), PluginSettings.DEFAULT_FULL_PATH));
        resetConfigurationFilesButton.addMouseListener(createResetConfigurationButtonMouseListener());

        String statAsString = UsageStatistic.getStatAsString();
        statPane.setText("Stat:\n" + statAsString);
        return (JComponent) panel1;
    }


    @Override
    public void apply() throws ConfigurationException {
        getSettings().configurationDirectory = configurationDirectory.getText();
    }

    @Override
    public boolean isModified() {
        return !configurationDirectory.getText().equals(getSettings().configurationDirectory);

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

    private PluginSettings getSettings() {
        return PluginSettings.getSettings();
    }

    private void updateUIFromSettings() {
        configurationDirectory.setText(getSettings().configurationDirectory);
    }


    private MouseListener createPathButtonMouseListener(final JTextField textField, final FileChooserDescriptor fileChooserDescriptor) {
        class MouseListener extends CustomMouseListener {

            @Override
            public void mousePressed(MouseEvent mouseEvent) {

                VirtualFile selectedFile = FileChooser.chooseFile(fileChooserDescriptor, null, null);
                if (selectedFile == null) {
                    return;
                }

                String path = selectedFile.getPath();
                if (path == null || path.isEmpty()) {
                    return;
                }
                textField.setText(path);
            }
        }

        return new MouseListener();
    }

    private MouseListener createResetPathButtonMouseListener(final JTextField textField, final String defaultValue) {
        class MouseListener extends CustomMouseListener {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                textField.setText(defaultValue);
            }
        }

        return new MouseListener();
    }

    private MouseListener createResetConfigurationButtonMouseListener() {
        class MouseListener extends CustomMouseListener {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                System.out.println(this + ":::createResetConfigurationButtonMouseListener");
                CoddyModuleComponent.copyTemplateFiles(CoddyModuleComponent.FORCE_REWRITE_FILES);
            }
        }

        return new MouseListener();
    }

    private void createUIComponents() {

    }
}
