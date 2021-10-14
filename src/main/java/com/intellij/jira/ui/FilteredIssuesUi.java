package com.intellij.jira.ui;

import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.ui.panels.JiraFilteredIssuesPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;

public class FilteredIssuesUi extends AbstractIssuesUi {

    public FilteredIssuesUi(String myId, JiraIssuesData myIssuesData) {
        super(myId, myIssuesData);
    }

    @Override
    public @NotNull JComponent getMainComponent() {
        return new JiraFilteredIssuesPanel(myIssuesData);
    }
}
