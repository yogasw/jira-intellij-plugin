package com.intellij.jira.components;

import com.intellij.jira.rest.model.JiraIssueUser;
import com.intellij.jira.server.JiraServerManager;
import com.intellij.jira.util.result.Result;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;

import static com.intellij.jira.server.JiraServerManager.JIRA_SERVER_CHANGED;

public class JiraUserProvider {

    private final Project myProject;
    private JiraIssueUser myCurrentUser;

    public JiraUserProvider(Project project) {
        myProject = project;
        project.getMessageBus().connect().subscribe(JIRA_SERVER_CHANGED, this::clearCurrentUser);
    }

    public JiraIssueUser getCurrent() {
        if (myCurrentUser == null) {
            Result currentUser = getJiraServerManager().getJiraRestApi(myProject).getCurrentUser();
            if (currentUser.isValid()) {
                myCurrentUser = (JiraIssueUser) currentUser.get();
            }
        }

        return myCurrentUser;
    }

    private JiraServerManager getJiraServerManager() {
        return ApplicationManager.getApplication().getService(JiraServerManager.class);
    }

    public void clearCurrentUser() {
        myCurrentUser = null;
    }

    public static JiraUserProvider getInstance(Project project) {
        return project.getService(JiraUserProvider.class);
    }

}
