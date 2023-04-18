package com.intellij.jira.settings.branch;

import com.intellij.jira.settings.ChangelistSettings;
import com.intellij.jira.settings.ChangelistState;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "BranchSettings",
        storages = @Storage("branch.xml")
)
public class BranchSettings implements PersistentStateComponent<BranchSettingsState> {

    private BranchSettingsState myState;

    public static BranchSettings getInstance() {
        return ApplicationManager.getApplication().getService(BranchSettings.class);
    }

    @Override
    public @Nullable BranchSettingsState getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull BranchSettingsState state) {
        myState = state;
    }
}
