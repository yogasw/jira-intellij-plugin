package com.intellij.jira.ui.table.column;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.ui.renders.JiraIssueStringCellRenderer;
import com.intellij.jira.util.JiraIssueUtil;

import javax.swing.table.TableCellRenderer;

public class Assignee extends StringColumn<JiraIssue> {

    public static final Assignee INSTANCE = new Assignee();

    private Assignee() {
        super("Default.Assignee", "Assignee");
    }

    @Override
    public String valueOf(JiraIssue issue) {
        return JiraIssueUtil.getAssignee(issue);
    }

    @Override
    public TableCellRenderer getRenderer(JiraIssue issue) {
        return new JiraIssueStringCellRenderer(issue);
    }
}
