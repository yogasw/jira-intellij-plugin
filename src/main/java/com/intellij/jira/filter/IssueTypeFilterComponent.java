package com.intellij.jira.filter;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.DumbAwareAction;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class IssueTypeFilterComponent extends IssueFilterComponent<IssueTypeFilter, IssueTypeFilterModel> {

    public IssueTypeFilterComponent(IssueTypeFilterModel issueTypeFilterModel) {
        super(() -> "Type", issueTypeFilterModel);
    }

    @Override
    protected String getText(@NotNull IssueTypeFilter issueTypeFilter) {
        return issueTypeFilter.getDisplayText();
    }

    @Override
    protected ActionGroup createActionGroup() {
        DefaultActionGroup actionGroup = new DefaultActionGroup();

        actionGroup.add(createAllAction());

        Set<String> issueTypes = myFilterModel.getIssues().get().stream().map(issue -> issue.getIssuetype().getName()).collect(Collectors.toSet());

        issueTypes.forEach(issueType -> actionGroup.add(new IssueTypeAction(issueType)));

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
