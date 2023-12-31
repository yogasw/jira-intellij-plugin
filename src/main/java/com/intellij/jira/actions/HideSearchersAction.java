package com.intellij.jira.actions;

import com.intellij.jira.JiraUiDataKeys;
import com.intellij.jira.ui.JiraIssueUiProperties;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import static com.intellij.jira.ui.SearcherIssuesUi.SHOW_SEARCHERS_PROPERTY;

public class HideSearchersAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        JiraIssueUiProperties properties = e.getRequiredData(JiraUiDataKeys.JIRA_UI_PROPERTIES);
        properties.set(SHOW_SEARCHERS_PROPERTY, false);
    }
}
