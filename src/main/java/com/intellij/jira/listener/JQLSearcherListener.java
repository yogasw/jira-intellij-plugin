package com.intellij.jira.listener;

import com.intellij.jira.rest.model.jql.JQLSearcher;

import java.util.List;

public interface JQLSearcherListener {

    void onChange(List<JQLSearcher> editedSearchers);

    void onRemoved(List<JQLSearcher> removedSearchers);

}
