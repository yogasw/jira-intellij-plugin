package com.intellij.jira.actions;

import com.intellij.icons.AllIcons;
import com.intellij.jira.JiraTabsManager;
import com.intellij.jira.JiraUiDataKeys;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.jira.ui.tree.SearcherTreeNode;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.nonNull;

public class OpenNewIssuesTabAction extends JiraIssueSearcherAction {
    private static final ActionProperties properties = ActionProperties.of("Open New Filtered Issues Tab", AllIcons.Actions.OpenNewTab);

    public OpenNewIssuesTabAction() {
        super(properties);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (nonNull(project)) {
            JQLSearcher searcher = getSearcher(e);

            JiraTabsManager.getInstance(project).openFilteredIssuesTab(searcher);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        SearcherTreeNode node = e.getData(JiraUiDataKeys.SEARCHER_TREE_NODE);
        e.getPresentation().setEnabled(nonNull(node) && nonNull(node.getSearcher()));
    }
}
