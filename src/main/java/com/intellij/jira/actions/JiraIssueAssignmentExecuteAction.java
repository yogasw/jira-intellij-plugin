package com.intellij.jira.actions;

import com.intellij.jira.tasks.AssignUserTask;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

import static java.util.Objects.nonNull;

public class JiraIssueAssignmentExecuteAction extends JiraIssueAction {

    private String accoundId;
    private String username;
    private String issueKey;


    public static JiraIssueAssignmentExecuteAction assignAnyone(String issueKey){
        return new JiraIssueAssignmentExecuteAction("Unassign", null, "-1", issueKey);
    }

    public static JiraIssueAssignmentExecuteAction assignUser(String displayName, String accountId,  String username, String issueKey){
        return new JiraIssueAssignmentExecuteAction(displayName, accountId, username, issueKey);
    }

    private JiraIssueAssignmentExecuteAction(String actionName, String accoundId, String username, String issueKey) {
        super(ActionProperties.of(actionName));
        this.accoundId = accoundId;
        this.username = username;
        this.issueKey = issueKey;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if(nonNull(project)) {
            new AssignUserTask(project, accoundId, username, issueKey).queue();
        }
    }
}
