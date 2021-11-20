package com.intellij.jira.ui;

import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.jql.JQLSearcherManager;
import com.intellij.jira.listener.SearcherListener;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.jira.ui.panels.JiraIssuesPanel;
import com.intellij.jira.ui.table.JiraIssueTable;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;
import java.util.List;

public class DefaultIssuesUi extends AbstractIssuesUi {

    private static final String ISSUES_ID = "Issues";

    protected final JiraIssuesPanel myIssuesPanel;

    public DefaultIssuesUi(JiraIssuesData issuesData) {
        super(ISSUES_ID, issuesData);

        myIssuesPanel = new JiraIssuesPanel(issuesData, this, this);
        updateHighlighters();

        issuesData.getProject().getMessageBus().connect()
                .subscribe(JQLSearcherManager.JQL_SEARCHERS_CHANGE, new MySearcherListener());
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

    private class MySearcherListener implements SearcherListener {

        @Override
        public void onAdded(JQLSearcher editedSearcher) {
            // Do nothing
        }

        @Override
        public void onChange(JQLSearcher searcher) {
            refresh();
        }

        @Override
        public void onRemoved(JQLSearcher removedSearcher) {
            refresh();
        }
    }

}
