package com.intellij.jira.listener;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.util.messages.Topic;

import java.util.List;

public interface JiraIssuesRefreshedListener {

    Topic<JiraIssuesRefreshedListener> TOPIC = Topic.create("jira_issue_refreshed_listener", JiraIssuesRefreshedListener.class);

    void issuesRefreshed(List<JiraIssue> issues);

}
