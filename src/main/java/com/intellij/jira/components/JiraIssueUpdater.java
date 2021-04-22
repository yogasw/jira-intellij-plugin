package com.intellij.jira.components;

import com.intellij.jira.events.JiraIssueEventListener;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JiraIssueUpdater implements Updater<JiraIssue>, Disposable {

    private Map<String, List<JiraIssueEventListener>> issueListeners;
    private List<JiraIssueEventListener> listeners;

    public JiraIssueUpdater() {
        this.issueListeners = new HashMap<>();
        this.listeners = new ArrayList<>();
    }

    public static JiraIssueUpdater getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, JiraIssueUpdater.class);
    }

    public void addIssueListener(String issueKey, JiraIssueEventListener listener){
        List<JiraIssueEventListener> issueListeners = this.issueListeners.getOrDefault(issueKey, new ArrayList<>());
        issueListeners.add(listener);

        this.issueListeners.put(issueKey, issueListeners);
    }

    public void removeIssueListener(String issueKey, JiraIssueEventListener listener){
        List<JiraIssueEventListener> issueListeners = this.issueListeners.getOrDefault(issueKey, new ArrayList<>());
        issueListeners.remove(listener);

        this.issueListeners.put(issueKey, issueListeners);
    }

    public void addListener(JiraIssueEventListener listener){
        listeners.add(listener);
    }

    public void removeListener(JiraIssueEventListener listener){
        listeners.remove(listener);
    }

    @Override
    public void update(List<JiraIssue> issues) {


        this.listeners.forEach(j ->
                SwingUtilities.invokeLater(() -> j.update(issues)));
    }

    @Override
    public void update(JiraIssue issue) {
        List<JiraIssueEventListener> issueListeners = this.issueListeners.getOrDefault(issue.getKey(), new ArrayList<>());
        issueListeners.forEach(view -> view.update(issue));

        this.listeners.forEach(j ->
            SwingUtilities.invokeLater(() -> j.update(issue)));
    }

    @Override
    public void dispose() {
        this.issueListeners.clear();
        this.listeners.clear();
    }
}
