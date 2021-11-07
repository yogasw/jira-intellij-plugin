package com.intellij.jira.ui;

import com.intellij.jira.ui.table.JiraIssueTable;

public interface IssuesUi extends JiraUi {

    JiraIssueTable getTable();

}
