package com.intellij.jira.tasks;

import com.intellij.jira.components.JiraNotificationManager;
import com.intellij.notification.Notifications;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vcs.changes.LocalChangeList;
import org.jetbrains.annotations.NotNull;

public class AddChangelistTask extends Task.Backgroundable {

    private final String changelistName;
    private final boolean isDefault;

    public AddChangelistTask(@NotNull Project project, @NotNull String changelistName, boolean isDefault) {
        super(project, "Creating changelist...");
        this.changelistName = changelistName;
        this.isDefault = isDefault;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        ChangeListManager changeListManager = ChangeListManager.getInstance(myProject);
        LocalChangeList localChangeList = changeListManager.addChangeList(changelistName, null);
        if (isDefault) {
            changeListManager.setDefaultChangeList(localChangeList);
        }
    }

    @Override
    public void onSuccess() {
        Notifications.Bus.notify(JiraNotificationManager.getInstance().createSilentNotification("Created '" + changelistName + "' Changelist", ""));
    }

}
