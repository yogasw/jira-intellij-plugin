package com.intellij.jira.filter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.intellij.jira.filter.IssueFilterCollectionImpl.ISSUE_TYPE_FILTER;

public class IssueTypeFilterModel extends FilterModel<IssueTypeFilter> {

    public IssueTypeFilterModel(IssueFilterCollection filters) {
        myFilter = filters.get(ISSUE_TYPE_FILTER);
    }

    @Override
    protected void saveFilterToProperties(@Nullable IssueTypeFilter filter) {

    }

    @Override
    protected @Nullable IssueTypeFilter getFilterFromProperties() {
        return null;
    }

    @Nullable
    @Override
    protected IssueTypeFilter createFilter(@NotNull List<String> values) {
        return new IssueTypeFilterImpl(values);
    }

    @NotNull
    @Override
    protected List<String> getFilterValues(@NotNull IssueTypeFilter filter) {
        return new ArrayList<>(filter.getIssueTypes());
    }

    public IssueTypeFilter getIssueTypeFilter() {
        IssueTypeFilter filter = getFilter();
        if (filter == null) {
            filter = new IssueTypeFilterImpl();
        }

        return filter;
    }

}
