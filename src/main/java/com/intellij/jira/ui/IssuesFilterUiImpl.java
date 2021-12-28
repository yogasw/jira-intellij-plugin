package com.intellij.jira.ui;

import com.intellij.jira.data.Issues;
import com.intellij.jira.filter.FilterModel;
import com.intellij.jira.filter.IssueFilterCollection;
import com.intellij.jira.filter.IssueFilterCollectionImpl;
import com.intellij.jira.filter.IssueFilterComponent;
import com.intellij.jira.filter.status.IssueStatusFilterComponent;
import com.intellij.jira.filter.status.IssueStatusFilterModel;
import com.intellij.jira.filter.type.IssueTypeFilterComponent;
import com.intellij.jira.filter.type.IssueTypeFilterModel;
import com.intellij.jira.filter.priority.PriorityFilterComponent;
import com.intellij.jira.filter.priority.PriorityFilterModel;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ex.CustomComponentAction;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.NlsActions;
import com.intellij.openapi.util.NotNullComputable;
import com.intellij.util.Consumer;
import com.intellij.util.EventDispatcher;
import com.intellij.util.ui.UIUtil;
import com.intellij.vcs.log.ui.MainVcsLogUi;
import com.intellij.vcs.log.ui.VcsLogInternalDataKeys;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;
import java.awt.Component;
import java.util.List;
import java.util.function.Supplier;

public class IssuesFilterUiImpl implements IssuesFilterUi {

    private Issues myIssues;

    private final IssueTypeFilterModel myIssueTypeFilterModel;
    private final IssueStatusFilterModel myIssueStatusFilterModel;
    private final PriorityFilterModel myPriorityFilterModel;

    private final EventDispatcher<IssueFilterListener> myFilterListenerDispatcher = EventDispatcher.create(IssueFilterListener.class);

    public IssuesFilterUiImpl(Consumer<IssueFilterCollection> filterConsumer, IssueFilterCollection filters) {
        myIssues = Issues.EMPTY;

        NotNullComputable<Issues> issuesGetter = () -> myIssues;
        myIssueTypeFilterModel = new IssueTypeFilterModel(issuesGetter, filters);
        myIssueStatusFilterModel = new IssueStatusFilterModel(issuesGetter, filters);
        myPriorityFilterModel = new PriorityFilterModel(issuesGetter, filters);

        FilterModel[] models = {myIssueTypeFilterModel, myPriorityFilterModel, myIssueStatusFilterModel};
        for(FilterModel model : models) {
            model.addSetFilterListener(() -> {
                filterConsumer.consume(getFilters());
                myFilterListenerDispatcher.getMulticaster().onFiltersChanged();
            });
        }

    }

    @Override
    public IssueFilterCollection getFilters() {
        return new IssueFilterCollectionImpl(List.of(myIssueTypeFilterModel.getIssueTypeFilter(),
                                                    myPriorityFilterModel.getPriorityFilter(),
                                                    myIssueStatusFilterModel.getIssueTypeFilter()));
    }

    @NotNull
    @Override
    public ActionGroup createActionGroup() {
        DefaultActionGroup actionGroup = new DefaultActionGroup();

        FilterActionComponent issueTypeComponent = createIssueTypeComponent();
        actionGroup.add(issueTypeComponent);

        FilterActionComponent priorityComponent = createPriorityComponent();
        actionGroup.add(priorityComponent);

        FilterActionComponent issueStatusComponent = createIssueStatusComponent();
        actionGroup.add(issueStatusComponent);

        return actionGroup;
    }

    @Override
    public void updateIssues(Issues issues) {
        myIssues = issues;
    }

    @Override
    public void addFilterListener(@NotNull IssueFilterListener listener) {
        myFilterListenerDispatcher.addListener(listener);
    }

    protected FilterActionComponent createIssueTypeComponent() {
        return new FilterActionComponent(() -> "Filter by Issue Type",
                () -> new IssueTypeFilterComponent(myIssueTypeFilterModel).initUi());
    }

    private FilterActionComponent createPriorityComponent() {
        return new FilterActionComponent(() -> "Filter by Issue Priority",
                () -> new PriorityFilterComponent(myPriorityFilterModel).initUi());
    }

    private FilterActionComponent createIssueStatusComponent() {
        return new FilterActionComponent(() -> "Filter by Status",
                () -> new IssueStatusFilterComponent(myIssueStatusFilterModel).initUi());
    }

    protected static class FilterActionComponent extends DumbAwareAction implements CustomComponentAction {

        @NotNull private final Computable<? extends JComponent> myComponentCreator;

        public FilterActionComponent(@NotNull Supplier<@Nls @NlsActions.ActionText String> dynamicText,
                                     @NotNull Computable<? extends JComponent> componentCreator) {
            super(dynamicText);
            myComponentCreator = componentCreator;
        }

        @NotNull
        @Override
        public JComponent createCustomComponent(@NotNull Presentation presentation, @NotNull String place) {
            return myComponentCreator.compute();
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            MainVcsLogUi vcsLogUi = e.getData(VcsLogInternalDataKeys.MAIN_UI);
            if (vcsLogUi == null) return;

            Component actionComponent = UIUtil.uiTraverser(vcsLogUi.getToolbar()).traverse().find(component ->
                UIUtil.getClientProperty(component, ACTION_KEY) == this
            );

            if (actionComponent instanceof IssueFilterComponent) {
                ((IssueFilterComponent)actionComponent).showPopupMenu();
            }
        }
    }
}
