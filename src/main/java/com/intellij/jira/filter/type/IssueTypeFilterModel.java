package com.intellij.jira.filter.type;

import com.intellij.jira.data.Issues;
import com.intellij.jira.filter.FilterModel;
import com.intellij.jira.filter.IssueFilterCollection;
import com.intellij.openapi.util.Computable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.intellij.jira.filter.IssueFilterCollectionImpl.ISSUE_TYPE_FILTER;

public class IssueTypeFilterModel extends FilterModel<IssueTypeFilter> {

    public IssueTypeFilterModel(Computable<Issues> issuesGetter, IssueFilterCollection filters) {
        super(issuesGetter);
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
