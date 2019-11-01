package com.intellij.jira.ui.dialog;

import com.intellij.jira.rest.model.JiraIssueWorklog;
import com.intellij.jira.tasks.EditWorklogTask;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.UI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.regex.Pattern;

import static com.intellij.openapi.util.text.StringUtil.isEmpty;
import static com.intellij.openapi.util.text.StringUtil.trim;
import static java.util.Objects.nonNull;

public class EditWorklogDialog extends DialogWrapper {

    private static final Pattern TIME_SPENT_PATTERN = Pattern.compile("(\\d+[wdhm])(\\s{1}\\d+[wdhm])*");

    private final static int DEFAULT_WIDTH = 300;
    private final static int DEFAULT_HEIGHT = 24;

    protected final Project myProject;
    protected String issueKey;
    private JiraIssueWorklog worklog;

    private JBLabel timeSpentLabel;
    protected JBTextField timeSpentField;

    public EditWorklogDialog(@Nullable Project project, String issueKey, JiraIssueWorklog worklog) {
        super(project, false);
        this.myProject = project;
        this.issueKey = issueKey;
        this.worklog = worklog;

        setTitle("Edit Work Log");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {

        this.timeSpentLabel = new JBLabel("Time Spent: ", 4);
        this.timeSpentField = new JBTextField(worklog.getTimeSpent());
        this.timeSpentField.setPreferredSize(UI.size(DEFAULT_WIDTH, DEFAULT_HEIGHT));

        return FormBuilder.createFormBuilder()
                .addLabeledComponent(timeSpentLabel, timeSpentField)
                .getPanel();
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return timeSpentField;
    }


    @NotNull
    @Override
    protected Action[] createActions() {
        return new Action[]{new EditWorklogExecuteAction(), myCancelAction};
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        String timeSpent = trim(timeSpentField.getText());
        if(isEmpty(timeSpent)){
            return new ValidationInfo("Time Spent is required", timeSpentField);
        }

        if(!TIME_SPENT_PATTERN.matcher(timeSpent).matches()){
            return new ValidationInfo("Invalid time duration entered", timeSpentField);
        }

        return null;
    }

    @Override
    protected void doOKAction() {
        if(nonNull(myProject)){
            new EditWorklogTask(myProject, issueKey, worklog.getId(), timeSpentField.getText()).queue();
        }

        close(0);
    }

    private class EditWorklogExecuteAction extends OkAction{ }

}
