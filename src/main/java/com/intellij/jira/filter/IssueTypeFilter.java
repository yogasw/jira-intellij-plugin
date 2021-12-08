package com.intellij.jira.filter;

import com.intellij.jira.rest.model.JiraIssueType;
import com.intellij.openapi.util.text.StringUtil;

import java.util.Collection;

import static com.intellij.jira.filter.IssueFilterCollectionImpl.ISSUE_TYPE_FILTER;

public interface IssueTypeFilter extends IssueFilter {

    @Override
    default IssueFilterCollection.FilterKey<IssueTypeFilter> getKey() {
        return ISSUE_TYPE_FILTER;
    }

    @Override
    default String getDisplayText() {
        return StringUtil.join(getIssueTypes(), ", ");
    }

    Collection<String> getIssueTypes();

    boolean matches(JiraIssueType issueType);

}