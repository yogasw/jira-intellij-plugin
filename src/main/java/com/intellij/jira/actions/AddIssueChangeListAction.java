package com.intellij.jira.actions;

import com.intellij.jira.JiraDataKeys;
import com.intellij.jira.components.JiraNotificationManager;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.settings.ChangelistSettings;
import com.intellij.jira.settings.ChangelistState;
import com.intellij.jira.util.ChangelistUtil;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vcs.changes.LocalChangeList;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AddIssueChangeListAction extends AnAction {

    public AddIssueChangeListAction() {
        super("New Changelist");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (Objects.isNull(project)) {
            return;
        }

        ChangelistSettings settings = ChangelistSettings.getInstance();
        ChangelistState state = settings.getState();
        JiraIssue issue = e.getRequiredData(JiraDataKeys.ISSUE);

        String changelistName = ChangelistUtil.getChangelistName(issue, settings);

        ChangeListManager changeListManager = ChangeListManager.getInstance(project);
        LocalChangeList localChangeList = changeListManager.addChangeList(changelistName, "");

        if (state.isActive()) {
            changeListManager.setDefaultChangeList(localChangeList);
        }

        Notifications.Bus.notify(JiraNotificationManager.getInstance().createNotification("Created " + localChangeList.getName() + " Changelist", ""));
    }

}
