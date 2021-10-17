package com.intellij.jira.actions;

import com.intellij.jira.server.JiraServerManager;
import com.intellij.jira.tasks.RefreshIssuesTask;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class JiraIssuesRefreshAction extends AnAction {


    @Override
    public void update(AnActionEvent event) {
        Project project = event.getProject();
        if (isNull(project)|| !project.isInitialized() || project.isDisposed()) {
            event.getPresentation().setEnabled(false);
        } else {
            JiraServerManager manager = ApplicationManager.getApplication().getService(JiraServerManager.class);
            event.getPresentation().setEnabled(manager.hasJiraServerConfigured(project));
        }
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        if(nonNull(project)){
            new RefreshIssuesTask(project).queue();
        }
    }

}
