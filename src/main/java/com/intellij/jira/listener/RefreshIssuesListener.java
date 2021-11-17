package com.intellij.jira.listener;

import com.intellij.util.messages.Topic;

public interface RefreshIssuesListener {

    Topic<RefreshIssuesListener> TOPIC = Topic.create("Refresh Issues", RefreshIssuesListener.class);

    void onRefresh();

}
