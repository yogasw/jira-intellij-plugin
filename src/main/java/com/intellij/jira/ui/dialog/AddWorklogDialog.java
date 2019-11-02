package com.intellij.jira.ui.dialog;

import com.intellij.jira.rest.model.JiraIssueTimeTracking;
import com.intellij.jira.rest.model.JiraIssueWorklog;
import com.intellij.jira.tasks.AddWorklogTask;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static java.util.Objects.nonNull;

public class AddWorklogDialog extends EditWorklogDialog {

    public AddWorklogDialog(@Nullable Project project, String issueKey, JiraIssueTimeTracking timeTracking) {
        super(project, issueKey, new JiraIssueWorklog(), timeTracking,true);
        setTitle("Add Log Work: " + issueKey);
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        return new Action[]{new AddWorklogExecuteAction(), myCancelAction};
    }

    @Override
    protected void doOKAction() {
        if(nonNull(myProject)){
            new AddWorklogTask(myProject, issueKey, worklogFields, remainingEstimateEditor.getJsonValue()).queue();
        }

        close(0);
    }

    private class AddWorklogExecuteAction extends OkAction{ }

}
