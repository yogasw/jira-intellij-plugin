package com.intellij.jira.actions;

import com.intellij.icons.AllIcons;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.ui.panels.JiraIssuePanel;
import com.intellij.jira.util.factory.JiraIssueFactory;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.intellij.jira.ui.JiraToolWindowFactory.TOOL_WINDOW_ID;
import static java.util.Objects.nonNull;

public class OpenNewJiraTabAction extends JiraIssueAction {

    private static final ActionProperties properties = ActionProperties.of("Open Issue in Tab", AllIcons.Actions.OpenNewTab);
    private final JiraIssueFactory issue;

    public OpenNewJiraTabAction(JiraIssueFactory factory) {
        super(properties);
        this.issue = factory;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (Objects.nonNull(project)) {

            ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
            ToolWindow jiraToolWindow = toolWindowManager.getToolWindow(TOOL_WINDOW_ID);
            ContentManager contentManager = jiraToolWindow.getContentManager();

            JiraIssue issue = this.issue.create();
            Content content = contentManager.findContent(issue.getKey()); // Avoid creates same content
            if (Objects.isNull(content)) {
                JiraIssuePanel jiraIssuePanel = new JiraIssuePanel(project, issue);

                content = ContentFactory.SERVICE.getInstance().createContent(jiraIssuePanel, issue.getKey(), false);
                contentManager.addContent(content);
            }

            contentManager.setSelectedContent(content);
        }

    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabled(nonNull(issue.create()));
    }
}
