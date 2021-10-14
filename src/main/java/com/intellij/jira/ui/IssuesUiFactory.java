package com.intellij.jira.ui;

import com.intellij.jira.data.JiraIssuesData;
import org.jetbrains.annotations.NotNull;

public final class IssuesUiFactory {

    private IssuesUiFactory() { }

    @NotNull
    public static IssuesUi createDefaultUi(JiraIssuesData issuesData) {
        return new DefaultIssuesUi(issuesData);
    }

    @NotNull
    public static IssuesUi createFilteredUi(String id, JiraIssuesData issuesData) {
        return new FilteredIssuesUi(id, issuesData);
    }

}
