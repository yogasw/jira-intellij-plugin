package com.intellij.jira.actions;

import com.intellij.icons.AllIcons;
import com.intellij.jira.components.JiraNotificationManager;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vcs.changes.LocalChangeList;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AddIssueChangeListAction extends AnAction {

    private final String issueKey;

    public AddIssueChangeListAction(String issueKey) {
        super("New " + issueKey + " Changelist", null, AllIcons.Vcs.Changelist);
        this.issueKey = issueKey;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (Objects.isNull(project)) {
            return;
        }

        ChangeListManager changeListManager = ChangeListManager.getInstance(project);
        LocalChangeList localChangeList = changeListManager.addChangeList(issueKey, "");
        changeListManager.setDefaultChangeList(localChangeList);

        Notifications.Bus.notify(JiraNotificationManager.getInstance().createNotification("Created " + localChangeList.getName() + " Changelist", ""));
    }
}
