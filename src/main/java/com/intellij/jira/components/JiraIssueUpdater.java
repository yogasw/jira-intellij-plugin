package com.intellij.jira.components;

import com.intellij.jira.events.JiraIssueEventListener;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class JiraIssueUpdater implements Updater<JiraIssue>, Disposable {

    private List<JiraIssueEventListener> listeners;

    public JiraIssueUpdater() {
        this.listeners = new ArrayList<>();
    }

    public static JiraIssueUpdater getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, JiraIssueUpdater.class);
    }

    public void addListener(JiraIssueEventListener listener){
        listeners.add(listener);
    }

    @Override
    public void update(List<JiraIssue> issues) {
        this.listeners.forEach(j ->
                SwingUtilities.invokeLater(() -> j.update(issues)));
    }

    @Override
    public void update(JiraIssue issue) {
        this.listeners.forEach(j ->
            SwingUtilities.invokeLater(() -> j.update(issue)));
    }

    @Override
    public void dispose() {
        this.listeners.clear();
    }
}
