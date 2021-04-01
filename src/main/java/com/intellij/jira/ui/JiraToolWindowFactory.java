package com.intellij.jira.ui;

import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.server.JiraServerManager;
import com.intellij.jira.ui.panels.JiraIssuesPanel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowType;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.SwingUtilities;

public class JiraToolWindowFactory implements ToolWindowFactory {

    public static final String TOOL_WINDOW_ID = "JIRA";
    public static final String TAB_ISSUES = "Issues";

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        createContent(project, toolWindow);

        JiraServerManager.getInstance(project).addConfigurationServerChangedListener(() -> {
            SwingUtilities.invokeLater(() -> createContent(project, toolWindow));
        });

        toolWindow.setType(ToolWindowType.DOCKED, null);
    }

    private void createContent(Project project, ToolWindow toolWindow) {
        ContentManager contentManager = toolWindow.getContentManager();
        contentManager.removeAllContents(true);

        JiraRestApi jiraRestApi = JiraServerManager.getInstance(project).getJiraRestApi();
        JiraIssuesPanel issuesPanel = new JiraIssuesPanel(jiraRestApi, project);

        Content content = ContentFactory.SERVICE.getInstance().createContent(issuesPanel, TAB_ISSUES, false);
        content.setCloseable(false);
        contentManager.addContent(content);
    }

}
