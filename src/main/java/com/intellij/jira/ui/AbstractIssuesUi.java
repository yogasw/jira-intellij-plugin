package com.intellij.jira.ui;

import com.intellij.jira.data.JiraIssuesData;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractIssuesUi implements JiraIssuesUi {

    private final String myId;
    protected final JiraIssuesData myIssuesData;

    protected AbstractIssuesUi(String id, JiraIssuesData issuesData) {
        myId = id;
        myIssuesData = issuesData;
    }

    @Override
    public @NotNull String getId() {
        return myId;
    }

    @Override
    public @NotNull JiraIssuesData getData() {
        return myIssuesData;
    }

}
