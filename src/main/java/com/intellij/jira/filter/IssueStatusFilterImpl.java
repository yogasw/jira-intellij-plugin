package com.intellij.jira.filter;

import com.intellij.jira.rest.model.JiraIssueStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class IssueStatusFilterImpl implements IssueStatusFilter {

    private final List<String> myStatus;

    public IssueStatusFilterImpl() {
        this(new ArrayList<>());
    }

    public IssueStatusFilterImpl(List<String> statuses) {
        myStatus = statuses;
    }

    @Override
    public Collection<String> getIssueStatus() {
        return myStatus;
    }

    @Override
    public boolean matches(JiraIssueStatus status) {
        return getIssueStatus().contains(status.getName());
    }

}
