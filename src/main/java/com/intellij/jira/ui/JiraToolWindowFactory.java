package com.intellij.jira.ui;

import com.intellij.jira.JiraTabsManager;
import com.intellij.jira.server.JiraServerManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowType;
import org.jetbrains.annotations.NotNull;

public class JiraToolWindowFactory implements ToolWindowFactory {

    public static final String TOOL_WINDOW_ID = "Jira";

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        createContent(project, toolWindow);

        JiraServerManager.getInstance(project).addConfigurationServerChangedListener(() -> {
            ApplicationManager.getApplication().invokeLater(() -> {
                toolWindow.getContentManager().removeAllContents(true);
                createContent(project, toolWindow);
            });
        });

        toolWindow.setType(ToolWindowType.DOCKED, null);
    }

    private void createContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        JiraTabsManager.getInstance(project).openIssuesTab();
    }

}
