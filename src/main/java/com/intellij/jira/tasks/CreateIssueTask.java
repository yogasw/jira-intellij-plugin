package com.intellij.jira.tasks;

import com.intellij.jira.exceptions.InvalidResultException;
import com.intellij.jira.helper.TransitionFieldHelper;
import com.intellij.jira.listener.IssueCreatedListener;
import com.intellij.jira.rest.model.JiraCreatedIssue;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.ui.editors.AttachmentFieldEditor;
import com.intellij.jira.util.result.Result;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Map;

import static com.intellij.jira.util.JiraIssueField.ATTACHMENT;

public class CreateIssueTask extends AbstractBackgroundableTask {

    private final Map<String, TransitionFieldHelper.FieldEditorInfo> myCreateIssueFields;
    private JiraCreatedIssue myCreatedIssue;

    public CreateIssueTask(@NotNull Project project, Map<String, TransitionFieldHelper.FieldEditorInfo> createIssueFields) {
        super(project, "Creating Issue...", null);
        myCreateIssueFields = createIssueFields;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        JiraRestApi jiraRestApi = getJiraRestApi();
        Result<JiraCreatedIssue> createdIssue = jiraRestApi.createIssue(myCreateIssueFields);
        if(!createdIssue.isValid()) {
            throw new InvalidResultException("Create issue error", "Issue has not been created");
        }

        myCreatedIssue = createdIssue.get();

        // Attach file if present
        TransitionFieldHelper.FieldEditorInfo attachmentFieldEditorInfo = myCreateIssueFields.get(ATTACHMENT);
        if (attachmentFieldEditorInfo != null
                && attachmentFieldEditorInfo.getEditor() instanceof AttachmentFieldEditor) {
            AttachmentFieldEditor attachmentFieldEditor = (AttachmentFieldEditor) attachmentFieldEditorInfo.getEditor();

            File selectedFile = attachmentFieldEditor.getSelectedFile();
            if (selectedFile != null) {
                new MyAddIssueAttachmentTask(myProject, myCreatedIssue.getKey(), selectedFile).queue();
            }
        }

    }

    @Override
    public void onSuccess() {
        showNotification("Jira", "Issue " + myCreatedIssue.getKey() + " has been successfully created");
        myProject.getMessageBus().syncPublisher(IssueCreatedListener.TOPIC).onCreated(myCreatedIssue);
    }

    private class MyAddIssueAttachmentTask extends AddIssueAttachmentTask {

        public MyAddIssueAttachmentTask(@NotNull Project project, @NotNull String issueKey, @NotNull File attachment) {
            super(project, issueKey, attachment);
        }

        @Override
        public void onSuccess() {
            // Do nothing
        }
    }

}
