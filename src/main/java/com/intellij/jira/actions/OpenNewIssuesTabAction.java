package com.intellij.jira.actions;

import com.intellij.icons.AllIcons;
import com.intellij.jira.components.JQLSearcherManager;
import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.jira.ui.JiraIssuesUi;
import com.intellij.jira.ui.JiraIssuesUiFactory;
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

public class OpenNewIssuesTabAction extends JiraIssueAction {
    private static final ActionProperties properties = ActionProperties.of("Open New Issues Tab With Selected Jql", AllIcons.Actions.OpenNewTab);

    public OpenNewIssuesTabAction() {
        super(properties);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (Objects.nonNull(project)) {

            ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
            ToolWindow jiraToolWindow = toolWindowManager.getToolWindow(TOOL_WINDOW_ID);
            ContentManager contentManager = jiraToolWindow.getContentManager();

            JQLSearcher selectedSearcher = JQLSearcherManager.getInstance().getSelectedSearcher(project);
            String jqlAlias = selectedSearcher.getAlias();
            Content content = contentManager.findContent(jqlAlias); // Avoid creates same content
            if (Objects.isNull(content)) {
                JiraIssuesData issuesData = new JiraIssuesData(project, selectedSearcher);
                JiraIssuesUi jqlIssuesUi = JiraIssuesUiFactory.createJQLUi(jqlAlias, issuesData);

                content = ContentFactory.SERVICE.getInstance().createContent(jqlIssuesUi.getMainComponent(), jqlAlias, false);
                contentManager.addContent(content);
            }

            contentManager.setSelectedContent(content);
        }
    }
}
