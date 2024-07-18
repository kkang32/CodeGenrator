package org.code.generator;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@State(
        name = "org.example.demo.SettingService",
        storages = {@Storage("plugin.xml")}
)
public class SettingService implements PersistentStateComponent<SettingService.State> {
    private State state = new State();

    @Override
    public @Nullable SettingService.State getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state = state;
    }

    @Nullable
    public static SettingService getInstance() {
        return ApplicationManager.getApplication().getService(SettingService.class);
    }

    public String getSettingFilePath(){
        return this.state.settingFilePath;
    }

    public void setSettingFilePath(String settingFilePath){
        this.state.settingFilePath = settingFilePath;
    }

    public String getTemplateDirectoryPath(){
        return this.state.templateDirectoryPath;
    }

    public void setTemplateDirectoryPath(String templateDirectoryPath){
        this.state.templateDirectoryPath = templateDirectoryPath;
    }


    public static class State {
        public String settingFilePath = "";
        public String templateDirectoryPath = "";
    }
}
