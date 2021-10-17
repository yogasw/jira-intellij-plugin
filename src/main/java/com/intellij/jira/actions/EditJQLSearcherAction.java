package com.intellij.jira.actions;

import com.intellij.icons.AllIcons;
import com.intellij.jira.jql.JQLSearcherManager;
import com.intellij.jira.jql.JQLSearcherProjectManager;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.jira.server.JiraServerManager;
import com.intellij.jira.ui.dialog.EditJQLSearcherDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class EditJQLSearcherAction extends AnAction {

    public EditJQLSearcherAction() {
        super("Edit JQL Searcher", null, AllIcons.Actions.Edit);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if(nonNull(project)){
            JQLSearcher defaultJQLSearcher = JQLSearcherManager.getInstance().getSelectedSearcher(project);

            EditJQLSearcherDialog dialog = new EditJQLSearcherDialog(project, defaultJQLSearcher);
            dialog.show();
        }
    }


    @Override
    public void update(AnActionEvent event) {
        Project project = event.getProject();
        if (isNull(project)|| !project.isInitialized() || project.isDisposed()) {
            event.getPresentation().setEnabled(false);
        } else {
            JiraServerManager manager = JiraServerManager.getInstance(project);
            if(manager.hasJiraServerConfigured()){
                JQLSearcherProjectManager jqlSearcherProjectManager = JQLSearcherProjectManager.getInstance(project);
                event.getPresentation().setEnabled(jqlSearcherProjectManager.hasSelectedSearcher());
            } else{
                event.getPresentation().setEnabled(false);
            }
        }
    }



}
