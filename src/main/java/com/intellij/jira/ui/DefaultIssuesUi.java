package com.intellij.jira.ui;

import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.jira.ui.panels.JiraIssuesPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;

public class DefaultIssuesUi extends AbstractIssuesUi {

    private static final String ISSUES_ID = "Issues";

    public DefaultIssuesUi(JiraIssuesData issuesData, JQLSearcher searcher) {
        super(ISSUES_ID, issuesData, searcher);
    }

    @NotNull
    @Override
    public JComponent getMainComponent() {
        return new JiraIssuesPanel(myIssuesData, mySearcher);
    }

}
