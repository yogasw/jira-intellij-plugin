package com.intellij.jira.ui;

import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractIssuesUi implements IssuesUi {

    private final String myId;
    protected final JiraIssuesData myIssuesData;
    protected final JQLSearcher mySearcher;

    protected AbstractIssuesUi(String id, JiraIssuesData issuesData, JQLSearcher searcher) {
        myId = id;
        myIssuesData = issuesData;
        mySearcher = searcher;
    }

    @Override
    public @NotNull String getId() {
        return myId;
    }


    @Override
    public void dispose() {

    }

}
