package com.intellij.jira.actions;

import com.intellij.icons.AllIcons;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.jira.ui.dialog.EditSearcherDialog;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

import static java.util.Objects.nonNull;

public class EditSearcherAction extends JiraIssueSearcherAction {

    private static final ActionProperties properties = ActionProperties.of("Edit Searcher", AllIcons.Actions.Edit);

    public EditSearcherAction() {
        super(properties);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if(nonNull(project)){
            JQLSearcher searcher = getSearcher(e);

            EditSearcherDialog dialog = new EditSearcherDialog(project, searcher);
            dialog.show();
        }
    }


}
