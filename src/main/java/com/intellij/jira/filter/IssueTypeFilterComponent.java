package com.intellij.jira.filter;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.DumbAwareAction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class IssueTypeFilterComponent extends IssueFilterComponent<IssueTypeFilter, IssueTypeFilterModel> {

    @NotNull private final Collection<Runnable> mySetFilterListeners = new ArrayList<>();

    public IssueTypeFilterComponent(IssueTypeFilterModel issueTypeFilterModel) {
        super(() -> "Type", issueTypeFilterModel);
    }


    @Override
    protected String getText(@NotNull IssueTypeFilter issueTypeFilter) {
        return issueTypeFilter.getDisplayText();
    }

    @Override
    public void installChangeListener(@NotNull Runnable onChange) {
        mySetFilterListeners.add(onChange);
    }

    @Override
    protected ActionGroup createActionGroup() {
        DefaultActionGroup actionGroup = new DefaultActionGroup();

        actionGroup.add(createAllAction());
        // TODO: get all issue types to iterate
        actionGroup.add(new IssueTypeAction("Story"));
        actionGroup.add(new IssueTypeAction("Epic"));

        return actionGroup;
    }

    protected class IssueTypeAction extends DumbAwareAction {
        @NotNull protected final String myType;


        public IssueTypeAction(@NotNull String value) {
            getTemplatePresentation().setText(value);
            myType = value;

        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            myFilterModel.setFilter(new IssueTypeFilterImpl(List.of(myType)));
        }
    }

}
