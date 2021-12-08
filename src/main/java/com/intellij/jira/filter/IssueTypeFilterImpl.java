package com.intellij.jira.filter;

import com.intellij.jira.rest.model.JiraIssueType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class IssueTypeFilterImpl implements IssueTypeFilter {

    private final List<String> myIssueTypes;

    public IssueTypeFilterImpl() {
        this(new ArrayList<>());
    }

    public IssueTypeFilterImpl(List<String> issueTypes) {
        myIssueTypes = issueTypes;
    }

    @Override
    public Collection<String> getIssueTypes() {
        return myIssueTypes;
    }

    @Override
    public boolean matches(JiraIssueType issueType) {
        return getIssueTypes().contains(issueType.getName());
    }
}
