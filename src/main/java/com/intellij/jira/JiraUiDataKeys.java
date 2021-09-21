package com.intellij.jira;

import com.intellij.jira.ui.panels.JiraIssuesPanel;
import com.intellij.openapi.actionSystem.DataKey;

public class JiraUiDataKeys {

    private JiraUiDataKeys() { }

    public static final DataKey<JiraIssuesPanel> ISSUES_PANEL = DataKey.create("issues.panel");

}
