package com.intellij.jira.actions;

import com.intellij.icons.AllIcons;
import com.intellij.jira.ui.dialog.NewSearcherDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

import static java.util.Objects.nonNull;

public class AddSearcherAction extends AnAction {

    public AddSearcherAction() {
        super("New Searcher", null, AllIcons.General.Add);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if(nonNull(project)){
            NewSearcherDialog dialog = new NewSearcherDialog(project);
            dialog.show();
        }

    }

}
