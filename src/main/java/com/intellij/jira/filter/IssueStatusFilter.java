package com.intellij.jira.filter;

import com.intellij.jira.rest.model.JiraIssueStatus;
import com.intellij.openapi.util.text.StringUtil;

import java.util.Collection;

import static com.intellij.jira.filter.IssueFilterCollectionImpl.ISSUE_STATUS_FILTER;

public interface IssueStatusFilter extends IssueFilter {

    @Override
    default IssueFilterCollection.FilterKey<IssueStatusFilter> getKey() {
        return ISSUE_STATUS_FILTER;
    }

    @Override
    default String getDisplayText() {
        return StringUtil.join(getIssueStatus(), ", ");
    }

    Collection<String> getIssueStatus();

    boolean matches(JiraIssueStatus status);

}
