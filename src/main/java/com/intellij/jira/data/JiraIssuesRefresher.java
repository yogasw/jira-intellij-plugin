package com.intellij.jira.data;

public interface JiraIssuesRefresher {

    void addVisibleIssueChangeListener(JiraIssuesRefresherImpl.VisibleIssueChangeListener listener);

    void removeVisibleIssueChangeListener(JiraIssuesRefresherImpl.VisibleIssueChangeListener listener);

    void getIssues(String jql);

}
