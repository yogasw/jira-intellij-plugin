package com.intellij.jira.listener;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.util.messages.Topic;
import org.jetbrains.annotations.NotNull;

public interface JiraIssueChangeListener {

    Topic<JiraIssueChangeListener> TOPIC = Topic.create("jira_issue_listener", JiraIssueChangeListener.class);

    void issueChanged(@NotNull JiraIssue issue);

}
