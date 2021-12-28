package com.intellij.jira.filter.status;

import com.intellij.jira.data.Issues;
import com.intellij.jira.filter.FilterModel;
import com.intellij.jira.filter.IssueFilterCollection;
import com.intellij.openapi.util.Computable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.intellij.jira.filter.IssueFilterCollectionImpl.ISSUE_STATUS_FILTER;

public class IssueStatusFilterModel extends FilterModel<IssueStatusFilter> {

    public IssueStatusFilterModel(Computable<Issues> issuesGetter, IssueFilterCollection filters) {
        super(issuesGetter);
        myFilter = filters.get(ISSUE_STATUS_FILTER);
    }

    @Override
    protected void saveFilterToProperties(@Nullable IssueStatusFilter filter) {

    }

    @Override
    protected @Nullable IssueStatusFilter getFilterFromProperties() {
        return null;
    }

    @Override
    protected @Nullable IssueStatusFilter createFilter(@NotNull List<String> values) {
        return new IssueStatusFilterImpl(values);
    }

    @Override
    protected @NotNull List<String> getFilterValues(@NotNull IssueStatusFilter filter) {
        return new ArrayList<>(filter.getIssueStatus());
    }

    public IssueStatusFilter getIssueTypeFilter() {
        IssueStatusFilter filter = getFilter();
        if (filter == null) {
            filter = new IssueStatusFilterImpl();
        }

        return filter;
    }
}
