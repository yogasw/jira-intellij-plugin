package com.intellij.jira.ui;

import com.intellij.jira.data.Issues;
import com.intellij.jira.filter.FilterModel;
import com.intellij.jira.filter.IssueFilterCollection;
import com.intellij.jira.filter.IssueFilterCollectionImpl;
import com.intellij.jira.filter.IssueFilterComponent;
import com.intellij.jira.filter.IssueStatusFilterComponent;
import com.intellij.jira.filter.IssueStatusFilterModel;
import com.intellij.jira.filter.IssueTypeFilterComponent;
import com.intellij.jira.filter.IssueTypeFilterModel;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ex.CustomComponentAction;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.NlsActions;
import com.intellij.util.Consumer;
import com.intellij.util.EventDispatcher;
import com.intellij.util.ui.UIUtil;
import com.intellij.vcs.log.ui.MainVcsLogUi;
import com.intellij.vcs.log.ui.VcsLogInternalDataKeys;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import java.awt.Component;
import java.util.List;
import java.util.function.Supplier;

public class IssuesFilterUiImpl implements IssuesFilterUi {

    private Issues myIssues;

    private final IssueTypeFilterModel myIssueTypeFilterModel;
    private final IssueStatusFilterModel myIssueStatusFilterModel;

    private final EventDispatcher<IssueFilterListener> myFilterListenerDispatcher = EventDispatcher.create(IssueFilterListener.class);

    public IssuesFilterUiImpl(Consumer<IssueFilterCollection> filterConsumer, IssueFilterCollection filters) {
        myIssues = Issues.EMPTY;

        myIssueTypeFilterModel = new IssueTypeFilterModel(filters);
        myIssueStatusFilterModel = new IssueStatusFilterModel(filters);

        FilterModel[] models = {myIssueTypeFilterModel, myIssueStatusFilterModel};
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
                                                    myIssueStatusFilterModel.getIssueTypeFilter()));
    }

    @NotNull
    @Override
    public ActionGroup createActionGroup() {
        DefaultActionGroup actionGroup = new DefaultActionGroup();

        FilterActionComponent issueTypeComponent = createIssueTypeComponent();
        if (issueTypeComponent != null) {
            actionGroup.add(issueTypeComponent);
        }

        FilterActionComponent issueStatusComponent = createIssueStatusComponent();
        if (issueStatusComponent != null) {
            actionGroup.add(issueStatusComponent);
        }

        return actionGroup;
    }

    private FilterActionComponent createIssueStatusComponent() {
        return new FilterActionComponent(() -> "Filter by Issue Type",
                () -> new IssueStatusFilterComponent(myIssueStatusFilterModel).initUi());
    }

    @Override
    public void updateIssues(Issues issues) {
        myIssues = issues;
    }

    @Override
    public void addFilterListener(@NotNull IssueFilterListener listener) {
        myFilterListenerDispatcher.addListener(listener);
    }

    @Nullable
    protected FilterActionComponent createIssueTypeComponent() {
        return new FilterActionComponent(() -> "Filter by Issue Type",
                () -> new IssueTypeFilterComponent(myIssueTypeFilterModel).initUi());
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
