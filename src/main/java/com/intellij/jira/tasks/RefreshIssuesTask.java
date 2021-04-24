package com.intellij.jira.tasks;

import com.intellij.jira.components.JQLSearcherManager;
import com.intellij.jira.components.JiraNotificationManager;
import com.intellij.jira.listener.JiraIssuesRefreshedListener;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.server.JiraServerManager;
import com.intellij.notification.Notifications;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

public class RefreshIssuesTask extends AbstractBackgroundableTask {

    public RefreshIssuesTask(@NotNull Project project) {
        super(project, "Updating Issues from Server", null);
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {

        JiraRestApi jiraRestApi = JiraServerManager.getInstance(myProject).getJiraRestApi();

        JQLSearcherManager jqlSearcherManager = JQLSearcherManager.getInstance();
        JQLSearcher searcher = jqlSearcherManager.getSelectedSearcher(myProject);

        List<JiraIssue> issues = new ArrayList<>();
        if(nonNull(searcher) && nonNull(jiraRestApi)){
            issues =  jiraRestApi.getIssues(searcher.getJql());
        }

        myProject.getMessageBus().syncPublisher(JiraIssuesRefreshedListener.TOPIC).issuesRefreshed(issues);
    }

    @Override
    public void showNotification(String title, String content) {
        Notifications.Bus.notify(JiraNotificationManager.getInstance().createSilentNotification(title, content));
    }

    @Override
    public void onSuccess() {
        showNotification("Jira", "Issues are now up to date");
    }

}
