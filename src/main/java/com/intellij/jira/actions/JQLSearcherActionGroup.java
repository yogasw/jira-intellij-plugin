package com.intellij.jira.actions;

import com.intellij.jira.server.JiraServerManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;

import static java.util.Objects.isNull;

public class JQLSearcherActionGroup extends DefaultActionGroup {

    @Override
    public void update(AnActionEvent event) {
        Project project = event.getProject();
        if (isNull(project)|| !project.isInitialized() || project.isDisposed()) {
            event.getPresentation().setVisible(false);
        } else {
            JiraServerManager manager = ApplicationManager.getApplication().getService(JiraServerManager.class);
            event.getPresentation().setVisible(manager.hasJiraServerConfigured(project));
        }
    }

}
