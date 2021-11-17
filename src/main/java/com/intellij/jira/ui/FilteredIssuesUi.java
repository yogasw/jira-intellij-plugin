package com.intellij.jira.ui;

import com.intellij.jira.JiraTabsManager;
import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.jql.JQLSearcherManager;
import com.intellij.jira.listener.JQLSearcherListener;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.jira.ui.panels.JiraFilteredIssuesPanel;
import com.intellij.jira.ui.table.JiraIssueTable;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;
import java.util.List;
import java.util.Optional;

public class FilteredIssuesUi extends AbstractIssuesUi {

    private JQLSearcher mySearcher;
    private final JiraFilteredIssuesPanel myFilteredIssuesPanel;

    public FilteredIssuesUi(JiraIssuesData issuesData, JQLSearcher searcher) {
        super(searcher.getAlias(), issuesData);

        mySearcher = searcher;
        myFilteredIssuesPanel = new JiraFilteredIssuesPanel(issuesData, this, this);
        updateHighlighters();

        issuesData.getProject().getMessageBus().connect()
                .subscribe(JQLSearcherManager.JQL_SEARCHERS_CHANGE, new MyJQLSearcherListener());

    }

    @NotNull
    @Override
    public String getId() {
        return mySearcher.getAlias();
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

    private class MyJQLSearcherListener implements JQLSearcherListener {

        @Override
        public void onChange(List<JQLSearcher> editedSearchers) {
            Optional<JQLSearcher> first = editedSearchers.stream().filter(s -> s.getId().equals(mySearcher.getId())).findFirst();
            first.ifPresent(searcher -> {
                mySearcher = searcher;

                // Update tab name
                JiraTabsManager.getInstance(myIssuesData.getProject()).updateTabName(FilteredIssuesUi.this);

                // Refresh
                refresh();
            });
        }

        @Override
        public void onRemoved(List<JQLSearcher> removedSearchers) {
            removedSearchers.forEach(searcher -> JiraTabsManager.getInstance(myIssuesData.getProject()).closeTab(searcher.getAlias()));
        }
    }

}
