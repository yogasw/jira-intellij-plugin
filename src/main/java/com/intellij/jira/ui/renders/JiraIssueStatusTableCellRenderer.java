package com.intellij.jira.ui.renders;

import com.intellij.jira.rest.model.JiraIssueStatus;
import com.intellij.jira.ui.labels.JiraStatusLightLabel;
import com.intellij.jira.ui.panels.JiraPanel;
import com.intellij.jira.util.JiraBorders;
import com.intellij.ui.components.JBLabel;

import javax.swing.*;
import java.awt.*;

import static java.awt.BorderLayout.LINE_START;

public class JiraIssueStatusTableCellRenderer extends JiraIssueTableCellRenderer {

    private final JiraIssueStatus jiraIssueStatus;

    public JiraIssueStatusTableCellRenderer(JiraIssueStatus jiraIssueStatus) {
        super();
        this.jiraIssueStatus = jiraIssueStatus;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        JiraPanel panel = new JiraPanel(new BorderLayout()).withBackground(getBackground());
        JBLabel jiraStatusLabel = new JiraStatusLightLabel(jiraIssueStatus);

        panel.setBorder(JiraBorders.empty(4, 3));
        panel.add(jiraStatusLabel, LINE_START);

        return panel;
    }

}
