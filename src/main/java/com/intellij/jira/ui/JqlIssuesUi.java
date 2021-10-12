package com.intellij.jira.ui;

import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.ui.panels.JiraJqlIssuesPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;

public class JqlIssuesUi extends AbstractIssuesUi {

    public JqlIssuesUi(String myId, JiraIssuesData myIssuesData) {
        super(myId, myIssuesData);
    }

    @Override
    public @NotNull JComponent getMainComponent() {
        return new JiraJqlIssuesPanel(myIssuesData);
    }
}
