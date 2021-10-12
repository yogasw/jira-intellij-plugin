package com.intellij.jira.tasks;

import com.intellij.jira.components.JiraNotificationManager;
import com.intellij.jira.listener.RefreshIssuesListener;
import com.intellij.notification.Notifications;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class RefreshIssuesTask extends AbstractBackgroundableTask {

    public RefreshIssuesTask(@NotNull Project project) {
        super(project, "Updating Issues from Server", null);
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        myProject.getMessageBus().syncPublisher(RefreshIssuesListener.TOPIC).onRefresh();
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
