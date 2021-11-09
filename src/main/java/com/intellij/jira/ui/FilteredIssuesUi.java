package com.intellij.jira.ui;

import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.jira.ui.panels.JiraFilteredIssuesPanel;
import com.intellij.jira.ui.table.JiraIssueTable;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;
import java.util.List;

public class FilteredIssuesUi extends AbstractIssuesUi {

    private final JQLSearcher mySearcher;
    private final JiraFilteredIssuesPanel myFilteredIssuesPanel;

    public FilteredIssuesUi(JiraIssuesData issuesData, JQLSearcher searcher) {
        super(searcher.getAlias(), issuesData);

        mySearcher = searcher;
        myFilteredIssuesPanel = new JiraFilteredIssuesPanel(issuesData, this, this);
        updateHighlighters();
    }

    @NotNull
    @Override
    public JComponent getMainComponent() {
        return myFilteredIssuesPanel;
    }

    @NotNull
    @Override
    public JiraIssueTable getTable() {
        return myFilteredIssuesPanel.getJiraIssueTable();
    }

    @NotNull
    @Override
    public JQLSearcher getSearcher() {
        return mySearcher;
    }

    @Override
    void setIssues(List<JiraIssue> issues) {
        myFilteredIssuesPanel.setIssues(issues);
    }
}
