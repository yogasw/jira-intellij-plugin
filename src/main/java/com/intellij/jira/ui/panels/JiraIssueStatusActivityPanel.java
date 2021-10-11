package com.intellij.jira.ui.panels;

import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.ui.OnePixelSplitter;
import org.jetbrains.annotations.NotNull;

public class JiraIssueStatusActivityPanel extends OnePixelSplitter {

    private final JiraIssueStatusPanel jiraIssueStatusPanel;
    private final JiraIssueActivityPanel jiraIssueActivityPanel;

    public JiraIssueStatusActivityPanel(@NotNull JiraIssuesData issuesData, JiraIssue issue) {
        super(0.35f);

        this.jiraIssueStatusPanel = new JiraIssueStatusPanel(issuesData, issue);
        this.jiraIssueActivityPanel = new JiraIssueActivityPanel(issuesData, issue);

        setFirstComponent(this.jiraIssueStatusPanel);
        setSecondComponent(this.jiraIssueActivityPanel);
    }

}
