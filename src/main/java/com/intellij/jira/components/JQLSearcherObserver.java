package com.intellij.jira.components;

import com.intellij.jira.events.JQLSearcherEventListener;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.jira.ui.panels.JiraJQLSearcherPanel;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class JQLSearcherObserver implements Updater<JQLSearcher>, Disposable {


    private List<JQLSearcherEventListener> myListeners;

    public JQLSearcherObserver() {
        this.myListeners = new ArrayList<>();
    }

    public void addListener(JiraJQLSearcherPanel jqlSearcherPanel) {
        myListeners.add(jqlSearcherPanel);
    }

    @Override
    public void update(List<JQLSearcher> jqlSearchers) {
        myListeners.forEach(l -> l.update(jqlSearchers));
    }

    @Override
    public void update(JQLSearcher jqlSearcher) {
        myListeners.forEach(l -> l.update(jqlSearcher));
    }

    public static JQLSearcherObserver getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, JQLSearcherObserver.class);
    }

    @Override
    public void dispose() {
        myListeners.clear();
    }
}
