package com.intellij.jira.filter;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.DumbAwareAction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class IssueStatusFilterComponent extends IssueFilterComponent<IssueStatusFilter, IssueStatusFilterModel> {


    public IssueStatusFilterComponent(@NotNull IssueStatusFilterModel filterModel) {
        super(() -> "Status", filterModel);
    }

    @Override
    protected String getText(@NotNull IssueStatusFilter issueStatusFilter) {
        return issueStatusFilter.getDisplayText();
    }

    @Override
    protected ActionGroup createActionGroup() {
        DefaultActionGroup actionGroup = new DefaultActionGroup();

        actionGroup.add(createAllAction());
        // TODO: get all issue status to iterate
        actionGroup.add(new IssueStatusAction("To Do"));
        actionGroup.add(new IssueStatusAction("In Progress"));
        actionGroup.add(new IssueStatusAction("Done"));

        return actionGroup;
    }

    protected class IssueStatusAction extends DumbAwareAction {
        @NotNull protected final String myStatus;


        public IssueStatusAction(@NotNull String value) {
            getTemplatePresentation().setText(value);
            myStatus = value;

        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            myFilterModel.setFilter(new IssueStatusFilterImpl(List.of(myStatus)));
        }
    }
}
