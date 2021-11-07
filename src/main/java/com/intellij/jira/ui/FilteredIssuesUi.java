package com.intellij.jira.ui;

import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.jira.ui.panels.JiraFilteredIssuesPanel;
import com.intellij.jira.ui.table.JiraIssueTable;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;

public class FilteredIssuesUi extends AbstractIssuesUi {

    private final JiraFilteredIssuesPanel myFilteredIssuesPanel;

    public FilteredIssuesUi(JiraIssuesData issuesData, JQLSearcher searcher) {
        super(searcher.getAlias(), issuesData, searcher);

        myFilteredIssuesPanel = new JiraFilteredIssuesPanel(issuesData, searcher, this);
        updateHighlighters();
    }

    @NotNull
    @Override
    public JComponent getMainComponent() {
        return myFilteredIssuesPanel;
    }

    @Override
    public JiraIssueTable getTable() {
        return myFilteredIssuesPanel.getJiraIssueTable();
    }

}
