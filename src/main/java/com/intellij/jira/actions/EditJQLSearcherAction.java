package com.intellij.jira.actions;

import com.intellij.icons.AllIcons;
import com.intellij.jira.components.JQLSearcherManager;
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
        super("Edit JQL searcher", null, AllIcons.Actions.Edit);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if(nonNull(project)){
            JQLSearcher deafaultJQLSearcher = project.getComponent(JQLSearcherManager.class).getSelectedSearcher();

            EditJQLSearcherDialog dialog = new EditJQLSearcherDialog(project, deafaultJQLSearcher);
            dialog.show();
        }
    }


    @Override
    public void update(AnActionEvent event) {
        Project project = event.getProject();
        if (isNull(project)|| !project.isInitialized() || project.isDisposed()) {
            event.getPresentation().setEnabled(false);
        } else {
            JiraServerManager manager = project.getComponent(JiraServerManager.class);
            if(manager.hasJiraServerConfigured()){
                if(project.getComponent(JQLSearcherManager.class).hasSelectedSearcher()){
                    event.getPresentation().setEnabled(true);
                }
                else{
                    event.getPresentation().setEnabled(false);
                }
            }
            else{
                event.getPresentation().setEnabled(false);
            }
        }
    }



}
