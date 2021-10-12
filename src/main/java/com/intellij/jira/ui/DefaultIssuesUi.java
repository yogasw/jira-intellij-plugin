package com.intellij.jira.ui;

import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.ui.panels.JiraIssuesPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class DefaultIssuesUi extends AbstractIssuesUi {

    private static final String MAIN_ISSUES_ID = "MAIN_ISSUES_ID";

    private final JiraIssuesPanel myIssuesPanel;

    public DefaultIssuesUi(JiraIssuesData issuesData) {
        super(MAIN_ISSUES_ID, issuesData);
        this.myIssuesPanel = new JiraIssuesPanel(issuesData);
    }

    @NotNull
    @Override
    public JComponent getMainComponent() {
        return myIssuesPanel;
    }

}
