package com.intellij.jira.tasks;

import com.intellij.jira.exceptions.InvalidResultException;
import com.intellij.jira.helper.TransitionFieldHelper;
import com.intellij.jira.listener.IssueCreatedListener;
import com.intellij.jira.rest.model.JiraCreatedIssue;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.util.result.Result;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

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
    }

    @Override
    public void onSuccess() {
        showNotification("Jira", "Issue " + myCreatedIssue.getKey() + " has been successfully created");
        myProject.getMessageBus().syncPublisher(IssueCreatedListener.TOPIC).onCreated(myCreatedIssue);
    }
}
