package com.intellij.jira.ui;

import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import org.jetbrains.annotations.NotNull;

public final class JiraUiFactory {

    private JiraUiFactory() { }

    @NotNull
    public static JiraUi createIssuesUi(JiraIssuesData issuesData, JQLSearcher searcher) {
        return new DefaultIssuesUi(issuesData, searcher);
    }

    @NotNull
    public static JiraUi createFilteredIssuesUi(JiraIssuesData issuesData, JQLSearcher searcher) {
        return new FilteredIssuesUi(issuesData, searcher);
    }

    @NotNull
    public static JiraUi createDetailsIssueUi(String id, JiraIssuesData issuesData) {
        return new DetailsIssueUi(issuesData, id);
    }

}
