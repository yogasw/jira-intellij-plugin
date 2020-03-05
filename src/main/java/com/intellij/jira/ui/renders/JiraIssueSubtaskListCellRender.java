package com.intellij.jira.ui.renders;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.JiraIssueStatus;
import com.intellij.jira.util.JiraLabelUtil;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBUI;

import javax.swing.JList;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import static com.intellij.jira.util.JiraLabelUtil.*;
import static com.intellij.openapi.util.text.StringUtil.toUpperCase;

public class JiraIssueSubtaskListCellRender extends DefaultJiraListCellRender {

    private JBLabel keyLabel;
    private JBLabel summaryLabel;
    private JBLabel statusLabel;

    public JiraIssueSubtaskListCellRender() {
        super();
        init();
    }

    private void init() {
        JBPanel issuePanel = new JBPanel(new BorderLayout())
                .withBorder(JBUI.Borders.empty(4, 5)).andTransparent();
        keyLabel =  JiraLabelUtil.createEmptyLabel().withFont(BOLD);
        summaryLabel =  JiraLabelUtil.createEmptyLabel().withBorder(JBUI.Borders.emptyLeft(10));
        statusLabel = JiraLabelUtil.createEmptyStatusLabel();

        issuePanel.add(keyLabel, BorderLayout.LINE_START);
        issuePanel.add(summaryLabel, BorderLayout.CENTER);
        issuePanel.add(statusLabel, BorderLayout.LINE_END);
        add(issuePanel);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        JiraIssue issue = (JiraIssue) value;

        setBorder(JBUI.Borders.emptyBottom(2));
        setBackground(DARCULA_TEXT_COLOR);

        keyLabel.setText(issue.getKey());
        keyLabel.setForeground(getFgRowColor());

        summaryLabel.setText(issue.getSummary());
        //summaryLabel.setForeground(getFgRowColor());

        JiraIssueStatus status = issue.getStatus();

        statusLabel.setText(toUpperCase(status.getName()));
        statusLabel.setBackground(status.getCategoryColor());
        statusLabel.setForeground(status.isInProgressCategory() ?  IN_PROGRESS_TEXT_COLOR : Color.WHITE);

        return this;
    }

}
