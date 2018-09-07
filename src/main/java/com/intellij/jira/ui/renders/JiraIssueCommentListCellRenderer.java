package com.intellij.jira.ui.renders;

import com.intellij.jira.rest.model.JiraIssueComment;
import com.intellij.jira.util.JiraIssueUtil;
import com.intellij.jira.util.JiraLabelUtil;
import com.intellij.jira.util.JiraPanelUtil;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;

import static com.intellij.jira.util.JiraLabelUtil.BOLD;
import static com.intellij.jira.util.JiraLabelUtil.ITALIC;

public class JiraIssueCommentListCellRenderer extends DefaultJiraListCellRender {

    private JBLabel authorLabel;
    private JBLabel createdLabel;
    private JTextArea commentArea;

    public JiraIssueCommentListCellRenderer() {
        super();
        init();
    }

    private void init() {
        JBPanel commentPanel = new JBPanel(new BorderLayout())
                .withBorder(JBUI.Borders.empty(4, 5)).andTransparent();

        JBPanel priorityPanel = JiraPanelUtil.createWhitePanel(new GridLayout(1,2));
        authorLabel = JiraLabelUtil.createEmptyLabel().withFont(BOLD);
        createdLabel = JiraLabelUtil.createEmptyLabel().withFont(ITALIC);
        createdLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        priorityPanel.add(authorLabel);
        priorityPanel.add(createdLabel);

        commentArea = new JTextArea();
        commentArea.setLineWrap(true);
        commentArea.setEditable(false);

        commentPanel.add(priorityPanel, BorderLayout.PAGE_START);
        commentPanel.add(commentArea, BorderLayout.PAGE_END);

        add(commentPanel);
    }


    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        JiraIssueComment comment = (JiraIssueComment) value;

        authorLabel.setText(comment.getAuthor().getDisplayName());
        createdLabel.setText(JiraIssueUtil.getCreated(comment));
        commentArea.setText(comment.getBody());

        return this;
    }
}
