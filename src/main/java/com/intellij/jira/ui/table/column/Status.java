package com.intellij.jira.ui.table.column;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.ui.renders.JiraIssueStatusTableCellRenderer;
import com.intellij.jira.util.JiraIssueUtil;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class Status extends StringColumn<JiraIssue> {

    public static final Status INSTANCE = new Status();

    protected Status() {
        super("Default.IssueStatus", "Status");
    }

    @Override
    public String valueOf(JiraIssue issue) {
        return JiraIssueUtil.getStatus(issue);
    }

    @Override
    public TableCellRenderer getRenderer(JiraIssue issue) {
        return new MyJiraIssueStatusTableCellRenderer(issue);
    }


    private class MyJiraIssueStatusTableCellRenderer extends JiraIssueStatusTableCellRenderer {

        public MyJiraIssueStatusTableCellRenderer(JiraIssue issue) {
            super(issue.getStatus().getName(), issue.getStatus().getCategoryColor(), issue.getStatus().isInProgressCategory());
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

}
