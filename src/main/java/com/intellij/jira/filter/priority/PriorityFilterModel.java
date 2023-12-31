package com.intellij.jira.filter.priority;


import com.intellij.jira.data.Issues;
import com.intellij.jira.filter.FilterModel;
import com.intellij.jira.filter.IssueFilterCollection;
import com.intellij.openapi.util.Computable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.intellij.jira.filter.IssueFilterCollectionImpl.ISSUE_PRIORITY_FILTER;

public class PriorityFilterModel extends FilterModel<PriorityFilter> {


    public PriorityFilterModel(Computable<Issues> issuesGetter, IssueFilterCollection filters) {
        super(issuesGetter);
        myFilter = filters.get(ISSUE_PRIORITY_FILTER);
    }

    @Override
    protected void saveFilterToProperties(@Nullable PriorityFilter filter) {

    }

    @Override
    protected @Nullable PriorityFilter getFilterFromProperties() {
        return null;
    }

    @Override
    protected @Nullable PriorityFilter createFilter(@NotNull List<String> values) {
        return new PriorityFilterImpl(values);
    }

    @Override
    protected @NotNull List<String> getFilterValues(@NotNull PriorityFilter filter) {
        return new ArrayList<>(filter.getPriorities());
    }

    public PriorityFilter getPriorityFilter() {
        PriorityFilter filter = getFilter();
        if (filter == null) {
            filter = new PriorityFilterImpl();
        }

        return filter;
    }
}
