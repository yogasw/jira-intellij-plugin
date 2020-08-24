package com.intellij.jira.actions;

import com.intellij.jira.components.JiraNotificationManager;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.settings.ChangelistSettings;
import com.intellij.jira.settings.ChangelistState;
import com.intellij.jira.util.factory.JiraIssueFactory;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vcs.changes.LocalChangeList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddIssueChangeListAction extends AnAction {

    private final JiraIssueFactory issueFactory;

    public AddIssueChangeListAction(JiraIssueFactory factory) {
        super("New Changelist");
        this.issueFactory = factory;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (Objects.isNull(project)) {
            return;
        }

        ChangelistSettings settings = ChangelistSettings.getInstance();
        ChangelistState state = settings.getState();
        JiraIssue issue = issueFactory.create();

        String changelistName = getChangelistName(state, issue);

        ChangeListManager changeListManager = ChangeListManager.getInstance(project);
        LocalChangeList localChangeList = changeListManager.addChangeList(changelistName, "");

        if (state.isActive()) {
            changeListManager.setDefaultChangeList(localChangeList);
        }

        Notifications.Bus.notify(JiraNotificationManager.getInstance().createNotification("Created " + localChangeList.getName() + " Changelist", ""));
    }


    private String getChangelistName(ChangelistState state, JiraIssue issue) {
        List<String> issueFieldValues = new ArrayList<>();
        for (String fieldName : state.getFieldNames()) {
            String value = null;
            if ("issueKey".endsWith(fieldName)) {
                value = issue.getKey();
            } else if ("projectKey".equals(fieldName)) {
                value = issue.getProject().getKey();
            }

            if (Objects.nonNull(value)) {
                issueFieldValues.add(value);
            }
        }

        return StringUtil.join(issueFieldValues, state.getFieldSeparator().getSeparator());
    }


}
