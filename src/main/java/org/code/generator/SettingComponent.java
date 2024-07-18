package org.code.generator;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;

import javax.swing.*;

public class SettingComponent {
    private JPanel panel;
    private TextFieldWithBrowseButton textSettingFilePath;
    private TextFieldWithBrowseButton textTemplateDirectoryPath;
    private SettingService settingService;


    public SettingComponent() {
        settingService = SettingService.getInstance();

        FileChooserDescriptor fileDescriptor = FileChooserDescriptorFactory.createSingleFileDescriptor();
        textSettingFilePath.addBrowseFolderListener("Select File", null, null, fileDescriptor);

        FileChooserDescriptor folderDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        textTemplateDirectoryPath.addBrowseFolderListener("Select Folder", null, null, folderDescriptor);

        textSettingFilePath.setText(settingService.getSettingFilePath());
        textTemplateDirectoryPath.setText(settingService.getTemplateDirectoryPath());


    }

    public JPanel getPanel() {
        return this.panel;
    }

    public boolean isModified() {
        return !textSettingFilePath.getText().equals(settingService.getSettingFilePath()) || !textTemplateDirectoryPath.getText().equals(settingService.getTemplateDirectoryPath());
    }

    public void apply() {
        settingService.setSettingFilePath(textSettingFilePath.getText());
        settingService.setTemplateDirectoryPath(textTemplateDirectoryPath.getText());
    }
}
