package com.intellij.jira.actions;

import com.intellij.icons.AllIcons;
import com.intellij.jira.server.JiraServerManager;
import com.intellij.jira.ui.dialog.NewJQLSearcherDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class AddJQLSearcherAction extends AnAction {

    public AddJQLSearcherAction() {
        super("New JQL Searcher", null, AllIcons.General.Add);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if(nonNull(project)){
            NewJQLSearcherDialog dialog = new NewJQLSearcherDialog(project);
            dialog.show();
        }

    }

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


}
