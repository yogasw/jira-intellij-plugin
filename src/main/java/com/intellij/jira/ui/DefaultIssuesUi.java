package com.intellij.jira.ui;

import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.jql.JQLSearcherManager;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.jira.ui.panels.JiraIssuesPanel;
import com.intellij.jira.ui.table.JiraIssueTable;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;
import java.util.List;

public class DefaultIssuesUi extends AbstractIssuesUi {

    private static final String ISSUES_ID = "Issues";

    private final JiraIssuesPanel myIssuesPanel;

    public DefaultIssuesUi(JiraIssuesData issuesData) {
        super(ISSUES_ID, issuesData);

        myIssuesPanel = new JiraIssuesPanel(issuesData, this, this);
        updateHighlighters();
    }

    @NotNull
    @Override
    public JComponent getMainComponent() {
        return myIssuesPanel;
    }

    @NotNull
    @Override
    public JiraIssueTable getTable() {
        return myIssuesPanel.getJiraIssueTable();
    }

    @NotNull
    @Override
    public JQLSearcher getSearcher() {
        return JQLSearcherManager.getInstance().getSelectedSearcher(myIssuesData.getProject());
    }

    @Override
    void setIssues(List<JiraIssue> issues) {
        myIssuesPanel.setIssues(issues);
    }
}
