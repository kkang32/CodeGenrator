package org.code.generator;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Configuration implements Configurable {

    private SettingComponent settingComponent;

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "GenerateCodes Settings";
    }

    @Override
    public @Nullable JComponent createComponent() {
        settingComponent = new SettingComponent();
        return settingComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        return settingComponent.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
        settingComponent.apply();
    }
}
