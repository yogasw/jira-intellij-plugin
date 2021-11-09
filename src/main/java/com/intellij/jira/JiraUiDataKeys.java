package com.intellij.jira;

import com.intellij.jira.ui.AbstractIssuesUi;
import com.intellij.jira.ui.JiraIssueUiProperties;
import com.intellij.jira.ui.panels.JiraIssuesPanel;
import com.intellij.openapi.actionSystem.DataKey;

public class JiraUiDataKeys {

    private JiraUiDataKeys() { }

    public static final DataKey<JiraIssueUiProperties> JIRA_UI_PROPERTIES = DataKey.create("jira.ui.properties");
    public static final DataKey<JiraIssuesPanel> ISSUES_PANEL = DataKey.create("issues.panel");
    public static final DataKey<AbstractIssuesUi> ISSUES_UI = DataKey.create("issues.ui");

}
