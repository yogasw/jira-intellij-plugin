package com.intellij.jira.ui.renders;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.JiraIssueLink;
import com.intellij.jira.rest.model.JiraIssueStatus;
import com.intellij.jira.ui.labels.JiraStatusLightLabel;
import com.intellij.jira.ui.panels.JiraPanel;
import com.intellij.jira.util.JiraBorders;
import com.intellij.jira.util.JiraLabelUtil;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import java.awt.*;

import static com.intellij.jira.util.JiraLabelUtil.*;
import static java.util.Objects.nonNull;

public class JiraIssueLinkListCellRenderer extends DefaultJiraListCellRender {

    private JBLabel typeLabel;
    private JBLabel issueKeyAndSummaryLabel;
    private JBLabel issueStatusLabel;

    public JiraIssueLinkListCellRenderer() {
        super();
        //init();
    }

    private void init() {
        JPanel issueLinkPanel = new JiraPanel(new BorderLayout())
                .withBorder(JiraBorders.empty(4, 5)).andTransparent();
        typeLabel =  JiraLabelUtil.createEmptyLabel().withFont(BOLD);
        issueStatusLabel = JiraLabelUtil.createEmptyStatusLabel();
        issueKeyAndSummaryLabel = JiraLabelUtil.createEmptyLabel().withBorder(JiraBorders.emptyLeft(10));
        issueLinkPanel.add(typeLabel, BorderLayout.LINE_START);
        issueLinkPanel.add(issueKeyAndSummaryLabel, BorderLayout.CENTER);
        issueLinkPanel.add(issueStatusLabel, BorderLayout.LINE_END);
        add(issueLinkPanel);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        JiraIssueLink issueLink = (JiraIssueLink) value;

        setBorder(JiraBorders.empty(4, 5));

        if (isSelected) {
            setBackground(UIUtil.isUnderDarcula() ? DARK_DARCULA_ISSUE_LINK_COLOR : DARK_ISSUE_LINK_COLOR);
        } else {
            setBackground(UIUtil.isUnderDarcula() ? DARCULA_ISSUE_LINK_COLOR : ISSUE_LINK_COLOR);
        }

        String typeText = nonNull(issueLink.getInwardIssue()) ? issueLink.getType().getInward() : issueLink.getType().getOutward();
        JLabel typeLabel = JiraLabelUtil.createBoldLabel(typeText);
        typeLabel.setForeground(getFgRowColor());

        JiraIssue issue = nonNull(issueLink.getInwardIssue()) ? issueLink.getInwardIssue() : issueLink.getOutwardIssue();
        JiraIssueStatus status = issue.getStatus();

        String issueKeyAndSummaryText = getIssueKeyAndSummary(issue);
        JLabel issueKeyAndSummaryLabel = JiraLabelUtil.createLabel(issueKeyAndSummaryText).withBorder(JiraBorders.emptyLeft(10));
        issueKeyAndSummaryLabel.setForeground(issue.isResolved() ? JBColor.border() : list.getForeground());

        JLabel issueStatusLabel = new JiraStatusLightLabel(status);

        add(typeLabel, BorderLayout.LINE_START);
        add(issueKeyAndSummaryLabel, BorderLayout.CENTER);
        add(issueStatusLabel, BorderLayout.LINE_END);

        return this;
    }


    private String getIssueKeyAndSummary(JiraIssue issue){
        return issue.getKey() + " " + issue.getSummary();
    }


}
