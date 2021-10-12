package com.intellij.jira.ui.panels;

import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.ui.JiraIssueActionPlaces;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;
import java.awt.*;

public class JiraJqlIssuesPanel extends JiraIssuesPanel {

    public JiraJqlIssuesPanel(@NotNull JiraIssuesData issuesData) {
        super(issuesData);
    }

    @Override
    protected @NotNull JComponent getToolbar(@NotNull Project project) {
        DefaultActionGroup toolbarGroup = new DefaultActionGroup();
        toolbarGroup.copyFromGroup((DefaultActionGroup) ActionManager.getInstance().getAction(JiraIssueActionPlaces.JIRA_ISSUES_BASE_TOOLBAR));

        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar(JiraIssueActionPlaces.JIRA_ISSUES_BASE_TOOLBAR, toolbarGroup, false);
        toolbar.setTargetComponent(this);

        return toolbar.getComponent();
    }

    @NotNull
    @Override
    protected String getToolbarOrientation() {
        return BorderLayout.WEST;
    }

    @Override
    protected void setToolbarHeightReference() {

    }
}
