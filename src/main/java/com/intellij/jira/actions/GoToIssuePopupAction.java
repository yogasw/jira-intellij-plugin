package com.intellij.jira.actions;

import com.intellij.icons.AllIcons;
import com.intellij.jira.server.JiraServerManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

import static java.util.Objects.isNull;

public class GoToIssuePopupAction extends JiraIssueAction {
    private static final ActionProperties properties = ActionProperties.of("Go to",  AllIcons.Actions.Find, "control shift G");

    public GoToIssuePopupAction() {
        super(properties);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if(isNull(project)){
            return;
        }

        // TODO: use e.getData()
        /*JiraIssuesPanel issuesPanel = (JiraIssuesPanel) getComponent();
        if(isNull(issuesPanel)){
            return;
        }*/

       /* List<String> issueKeys = issuesPanel.getTableListModel().getItems().stream().map(issue -> issue.getKey()).collect(toList());
        GoToIssuePopup popup = new GoToIssuePopup(project, issueKeys, key -> issuesPanel.goToIssue(key));
        popup.show(issuesPanel.getIssueTable());*/

    }

    @Override
    public void update(AnActionEvent event) {
        Project project = event.getProject();
        if (isNull(project)|| !project.isInitialized() || project.isDisposed()) {
            event.getPresentation().setEnabled(false);
        } else {
            JiraServerManager manager = JiraServerManager.getInstance(project);
            event.getPresentation().setEnabled(manager.hasJiraServerConfigured());
        }
    }



}
