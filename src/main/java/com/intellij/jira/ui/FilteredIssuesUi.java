package com.intellij.jira.ui;

import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.jira.ui.panels.JiraFilteredIssuesPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;

public class FilteredIssuesUi extends AbstractIssuesUi {

    public FilteredIssuesUi(JiraIssuesData issuesData, JQLSearcher searcher) {
        super(searcher.getAlias(), issuesData, searcher);
    }

    @Override
    public @NotNull JComponent getMainComponent() {
        return new JiraFilteredIssuesPanel(myIssuesData, mySearcher);
    }
}
