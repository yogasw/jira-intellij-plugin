package com.intellij.jira.listener;

import com.intellij.jira.rest.model.JiraCreatedIssue;
import com.intellij.util.messages.Topic;
import org.jetbrains.annotations.NotNull;

public interface IssueCreatedListener {

    Topic<IssueCreatedListener> TOPIC = Topic.create("Issue has been created", IssueCreatedListener.class);

    void onCreated(@NotNull JiraCreatedIssue createdIssue);

}
