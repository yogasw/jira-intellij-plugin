package com.intellij.jira.ui.dialog;

import com.intellij.jira.helper.TransitionFieldHelper;
import com.intellij.jira.rest.model.JiraIssueWorklog;
import com.intellij.jira.tasks.EditWorklogTask;
import com.intellij.jira.ui.editors.DateTimeFieldEditor;
import com.intellij.jira.ui.editors.TimeSpentEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

public class EditWorklogDialog extends DialogWrapper {

    protected final Project myProject;
    protected String issueKey;
    private JiraIssueWorklog worklog;

    protected TimeSpentEditor timeSpentEditor;
    protected DateTimeFieldEditor startedEditor;

    protected List<TransitionFieldHelper.FieldEditorInfo> worklogFields = new ArrayList<>();

    public EditWorklogDialog(@Nullable Project project, String issueKey, JiraIssueWorklog worklog) {
        super(project, false);
        this.myProject = project;
        this.issueKey = issueKey;
        this.worklog = worklog;

        setTitle("Edit Log Work: " + issueKey);
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {

        this.timeSpentEditor = new TimeSpentEditor(this.issueKey, this.worklog.getTimeSpent());
        this.startedEditor = new DateTimeFieldEditor("Date Started", this.worklog.getStarted(), this.issueKey, true);

        worklogFields.add(TransitionFieldHelper.createFieldEditorInfo("timeSpentSeconds", timeSpentEditor));
        worklogFields.add(TransitionFieldHelper.createFieldEditorInfo("started", startedEditor));

        return FormBuilder.createFormBuilder()
                .addComponent(timeSpentEditor.createPanel())
                .addComponent(startedEditor.createPanel())
                .getPanel();
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return timeSpentEditor.getMyTextField();
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        return new Action[]{new EditWorklogExecuteAction(), myCancelAction};
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        for(TransitionFieldHelper.FieldEditorInfo info : worklogFields){
            ValidationInfo fieldValidation = info.validateField();
            if(nonNull(fieldValidation)){
                return fieldValidation;
            }
        }

        return null;
    }

    @Override
    protected void doOKAction() {
        if(nonNull(myProject)){
            new EditWorklogTask(myProject, issueKey, worklog.getId(), worklogFields).queue();
        }

        close(0);
    }

    private class EditWorklogExecuteAction extends OkAction{ }

}
