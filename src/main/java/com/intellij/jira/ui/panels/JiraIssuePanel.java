package com.intellij.jira.ui.panels;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBSplitter;
import org.jetbrains.annotations.NotNull;

public class JiraIssuePanel extends JBSplitter implements Disposable {

    private JiraIssueDescriptionPanel jiraIssueDescriptionPanel;
    private JiraIssueStatusActivityPanel jiraIssueStatusActivityPanel;

    public JiraIssuePanel(@NotNull Project project, JiraIssue issue) {
        super(false);

        this.jiraIssueDescriptionPanel = new JiraIssueDescriptionPanel(project, issue);
        this.jiraIssueStatusActivityPanel = new JiraIssueStatusActivityPanel(project, issue);

        setFirstComponent(this.jiraIssueDescriptionPanel);
        setSecondComponent(this.jiraIssueStatusActivityPanel);
        setDividerWidth(1);
    }

    @Override
    public void dispose() {
        this.jiraIssueDescriptionPanel.dispose();
        this.jiraIssueStatusActivityPanel.dispose();
    }
}
