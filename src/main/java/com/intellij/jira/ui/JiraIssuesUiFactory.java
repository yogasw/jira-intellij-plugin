package com.intellij.jira.ui;

import com.intellij.jira.data.JiraIssuesData;

public final class JiraIssuesUiFactory {

    private JiraIssuesUiFactory() { }

    public static JiraIssuesUi createDefaultUi(JiraIssuesData issuesData) {
        return new DefaultIssuesUi(issuesData);
    }

    public static JiraIssuesUi createJQLUi(String id, JiraIssuesData issuesData) {
        return new JqlIssuesUi(id, issuesData);
    }

}
