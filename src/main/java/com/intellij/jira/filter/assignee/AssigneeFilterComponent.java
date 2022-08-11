package com.intellij.jira.filter.assignee;

import com.intellij.jira.filter.IssueFilterComponent;
import com.intellij.jira.util.JiraIssueUtil;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AssigneeFilterComponent extends IssueFilterComponent<AssigneeFilter, AssigneeFilterModel> {

    public AssigneeFilterComponent(@NotNull AssigneeFilterModel filterModel) {
        super(() -> "Assignee", filterModel);
    }

    @Override
    protected String getText(@NotNull AssigneeFilter filter) {
        return filter.getDisplayText();
    }

    @Override
    protected ActionGroup createActionGroup() {
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(createAllAction());

        Set<String> users = myFilterModel.getIssues().get().stream()
                .map(JiraIssueUtil::getAssignee)
                .filter(user -> !StringUtil.isEmpty(user))
                .collect(Collectors.toSet());

        users.forEach(issueType -> actionGroup.add(new IssueAssigneeAction(issueType)));

        actionGroup.add(new UnassignedAction());

        return actionGroup;
    }

    protected class IssueAssigneeAction extends DumbAwareAction {
        @NotNull protected final String myUser;

        public IssueAssigneeAction(@NotNull String value) {
            getTemplatePresentation().setText(value);
            myUser = value;
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            myFilterModel.setFilter(new AssigneeFilterImpl(List.of(myUser)));
        }
    }

    protected class UnassignedAction extends IssueAssigneeAction {

        public UnassignedAction() {
            super("Unassigned");
        }
    }
}
