package com.intellij.jira.filter;

import com.intellij.jira.data.Issues;
import com.intellij.jira.rest.model.JiraIssue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.intellij.jira.filter.IssueFilterCollectionImpl.ISSUE_TYPE_FILTER;

public class IssueFilterer {

    public Issues filter(Issues issues, IssueFilterCollection filters) {
        List<JiraIssue> filteredIssues = new ArrayList<>(issues.get());
        IssueTypeFilter issueTypeFilter = filters.get(ISSUE_TYPE_FILTER);
        Collection<String> issueTypes = issueTypeFilter.getIssueTypes();
        if (!issueTypes.isEmpty()) {
            filteredIssues = filteredIssues.stream().filter(issue -> issueTypes.contains(issue.getIssuetype().getName())).collect(Collectors.toList());
        }


        return Issues.of(filteredIssues);
    }

}
