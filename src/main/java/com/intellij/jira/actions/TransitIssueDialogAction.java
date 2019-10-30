package com.intellij.jira.actions;

import com.intellij.icons.AllIcons;
import com.intellij.jira.exceptions.InvalidPermissionException;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.JiraIssueTransition;
import com.intellij.jira.rest.model.JiraPermissionType;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.ui.dialog.IssueTransitionDialog;
import com.intellij.jira.util.JiraIssueFactory;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static java.util.Objects.nonNull;

public class TransitIssueDialogAction extends JiraIssueDialogAction {
    private static final ActionProperties properties = ActionProperties.of("Transit",  AllIcons.Actions.Forward);

    private JiraIssueFactory issueFactory;

    public TransitIssueDialogAction(JiraIssueFactory factory) {
        super(properties);
        this.issueFactory = factory;
    }

    @Override
    public void onClick(@NotNull AnActionEvent e, @NotNull Project project, @NotNull JiraRestApi jiraRestApi) {
        JiraIssue issue = issueFactory.create();
        boolean userHasPermission = jiraRestApi.userHasPermissionOnIssue(issue.getKey(), JiraPermissionType.TRANSITION_ISSUES);
        if(!userHasPermission){
            throw new InvalidPermissionException("Transition failed", "You don't have permission to transit issues");
        }

        List<JiraIssueTransition> transitions = jiraRestApi.getTransitions(issue.getId());

        IssueTransitionDialog dialog = new IssueTransitionDialog(project, issue, transitions);
        dialog.show();
    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabled(nonNull(issueFactory.create()));
    }
    
}
