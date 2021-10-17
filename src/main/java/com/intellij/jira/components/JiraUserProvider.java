package com.intellij.jira.components;

import com.intellij.jira.rest.model.JiraIssueUser;
import com.intellij.jira.server.JiraServerManager;
import com.intellij.jira.util.result.Result;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;

public class JiraUserProvider {

    private final Project myProject;
    private JiraIssueUser myCurrentUser;

    public JiraUserProvider(Project project) {
        this.myProject = project;
        //JiraServerManager.getInstance(myProject).addConfigurationServerChangedListener(() -> clearCurrentUser());
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
