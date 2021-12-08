package com.intellij.jira.filter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class IssueFilterCollectionImpl implements IssueFilterCollection {

    public static final FilterKey<IssueTypeFilter> ISSUE_TYPE_FILTER = FilterKey.create("Issue Type Filter");
    public static final FilterKey<IssueStatusFilter> ISSUE_STATUS_FILTER = FilterKey.create("Issue Status Filter");

    private final Map<FilterKey, IssueFilter> myFilters = new HashMap<>();

    public IssueFilterCollectionImpl(Collection<IssueFilter> filters) {
        filters.forEach(filter -> myFilters.put(filter.getKey(), filter));
    }

    @Override
    public Collection<IssueFilter> getFilters() {
        return myFilters.values();
    }

    @Override
    public <T extends IssueFilter> T get(FilterKey<T> key) {
        return (T) myFilters.get(key);
    }
}
