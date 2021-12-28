package com.intellij.jira.filter.status;

import com.intellij.jira.filter.IssueFilterComponent;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.DumbAwareAction;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

        Set<String> issueStatus = myFilterModel.getIssues().get().stream().map(issue -> issue.getStatus().getName()).collect(Collectors.toSet());

        issueStatus.forEach(status -> actionGroup.add(new IssueStatusAction(status)));

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
