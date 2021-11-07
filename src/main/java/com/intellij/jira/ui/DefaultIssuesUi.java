package com.intellij.jira.ui;

import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.jira.ui.panels.JiraIssuesPanel;
import com.intellij.jira.ui.table.JiraIssueTable;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;

public class DefaultIssuesUi extends AbstractIssuesUi {

    private static final String ISSUES_ID = "Issues";

    private final JiraIssuesPanel myIssuesPanel;

    public DefaultIssuesUi(JiraIssuesData issuesData, JQLSearcher searcher) {
        super(ISSUES_ID, issuesData, searcher);

        myIssuesPanel = new JiraIssuesPanel(issuesData, searcher, this);
        updateHighlighters();
    }

    @NotNull
    @Override
    public JComponent getMainComponent() {
        return myIssuesPanel;
    }

    @Override
    public JiraIssueTable getTable() {
        return myIssuesPanel.getJiraIssueTable();
    }

}
