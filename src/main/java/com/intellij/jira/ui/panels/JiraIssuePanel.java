package com.intellij.jira.ui.panels;

import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.ui.OnePixelSplitter;
import org.jetbrains.annotations.NotNull;

public class JiraIssuePanel extends OnePixelSplitter {

    private final JiraIssueDescriptionPanel jiraIssueDescriptionPanel;
    private final JiraIssueStatusActivityPanel jiraIssueStatusActivityPanel;

    public JiraIssuePanel(@NotNull JiraIssuesData issuesData, String issueKey) {
        super(0.5f);

        JiraIssue issue = issuesData.getIssue(issueKey);

        this.jiraIssueDescriptionPanel = new JiraIssueDescriptionPanel(issuesData, issue);
        this.jiraIssueStatusActivityPanel = new JiraIssueStatusActivityPanel(issuesData, issue);

        setFirstComponent(this.jiraIssueDescriptionPanel);
        setSecondComponent(this.jiraIssueStatusActivityPanel);
    }

}
