package com.intellij.jira.ui;

import com.intellij.jira.components.JQLSearcherManager;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.server.JiraServerManager;
import com.intellij.jira.ui.panels.JiraIssuesPanel;
import com.intellij.jira.ui.panels.JiraServerNotConfiguredPanel;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowType;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;
import java.util.Objects;

public class JiraToolWindowFactory implements ToolWindowFactory {

    public static final String TOOL_WINDOW_ID = "Jira";
    public static final String TAB_ISSUES = "Issues";

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        createContent(project, toolWindow);

        JiraServerManager.getInstance(project).addConfigurationServerChangedListener(() -> {
            ApplicationManager.getApplication().invokeLater(() -> createContent(project, toolWindow));
        });

        toolWindow.setType(ToolWindowType.DOCKED, null);
    }

    private void createContent(Project project, ToolWindow toolWindow) {
        ContentManager contentManager = toolWindow.getContentManager();
        contentManager.removeAllContents(true);

        JiraRestApi jiraRestApi = JiraServerManager.getInstance(project).getJiraRestApi();
        JComponent issuesPanel;
        if (Objects.isNull(jiraRestApi)) {
            issuesPanel = new JiraServerNotConfiguredPanel(project);
        } else {
            String defaultJqlSearcher = JQLSearcherManager.getInstance().getSelectedSearcher(project).getJql();
            List<JiraIssue> issues = jiraRestApi.getIssues(defaultJqlSearcher);
            issuesPanel = new JiraIssuesPanel(project, issues);
        }

        Content content = ContentFactory.SERVICE.getInstance().createContent(issuesPanel, TAB_ISSUES, false);
        content.setCloseable(false);
        contentManager.addContent(content);
    }

}
