package com.intellij.jira.actions;

import com.intellij.icons.AllIcons;
import com.intellij.jira.jql.JQLSearcherManager;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.jira.ui.tree.SearcherTreeNode;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class MakeSearcherProjectAction extends JiraIssueSearcherAction {

    private static final ActionProperties properties = ActionProperties.of("Make Project", AllIcons.ToolbarDecorator.Import);

    public MakeSearcherProjectAction() {
        super(properties);
    }
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        JQLSearcher searcher = getSearcher(e);
        searcher.setShared(false);

        JQLSearcherManager.getInstance().moveToProject(e.getProject(), searcher);
    }

    @Override
    protected boolean isEnabled(SearcherTreeNode node) {
        return super.isEnabled(node) && node.getSearcher().isShared();
    }
}
