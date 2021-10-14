package com.intellij.jira.actions;

import com.intellij.icons.AllIcons;
import com.intellij.jira.JiraTabsManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class OpenNewIssuesTabAction extends JiraIssueAction {
    private static final ActionProperties properties = ActionProperties.of("Open New Filtered Issues Tab", AllIcons.Actions.OpenNewTab);

    public OpenNewIssuesTabAction() {
        super(properties);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (Objects.nonNull(project)) {
            JiraTabsManager.getInstance(project).openFilteredIssuesTab();
        }
    }
}
