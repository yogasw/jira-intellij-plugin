package com.intellij.jira.ui.panels;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBSplitter;
import org.jetbrains.annotations.NotNull;

public class JiraIssueStatusActivityPanel extends JBSplitter implements Disposable {

    private JiraIssueStatusPanel jiraIssueStatusPanel;
    private JiraIssueActivityPanel jiraIssueActivityPanel;

    public JiraIssueStatusActivityPanel(@NotNull Project project, JiraIssue issue) {
        super(false);

        this.jiraIssueStatusPanel = new JiraIssueStatusPanel(project, issue);
        this.jiraIssueActivityPanel = new JiraIssueActivityPanel(project, issue);

        setProportion(0.4f);
        setFirstComponent(this.jiraIssueStatusPanel);
        setSecondComponent(this.jiraIssueActivityPanel);
        setDividerWidth(1);
    }

    @Override
    public void dispose() {
        this.jiraIssueStatusPanel.dispose();
        this.jiraIssueActivityPanel.dispose();
    }

}
