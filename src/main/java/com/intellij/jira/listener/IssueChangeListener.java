package com.intellij.jira.listener;

import com.intellij.util.messages.Topic;

public interface IssueChangeListener {

    Topic<IssueChangeListener> TOPIC = Topic.create("Issue has changed", IssueChangeListener.class);

    void onChange(String issueKey);

}
