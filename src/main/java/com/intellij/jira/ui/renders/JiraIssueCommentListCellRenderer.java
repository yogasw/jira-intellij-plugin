package com.intellij.jira.ui.renders;

import com.intellij.jira.rest.model.JiraIssueComment;
import com.intellij.jira.util.JiraIssueUtil;
import com.intellij.jira.util.JiraLabelUtil;
import com.intellij.jira.util.JiraPanelUtil;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UI;

import javax.swing.*;
import java.awt.*;

import static com.intellij.jira.util.JiraIssueUtil.getPrettyBody;
import static com.intellij.jira.util.JiraLabelUtil.BOLD;
import static com.intellij.jira.util.JiraLabelUtil.ITALIC;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

public class JiraIssueCommentListCellRenderer extends DefaultJiraListCellRender {

    private JBPanel commentPanel;
    private JBLabel authorLabel;
    private JBLabel createdLabel;
    private JTextArea commentArea;

    public JiraIssueCommentListCellRenderer() {
        super();
        init();
    }

    private void init() {
        commentPanel = new JBPanel(new BorderLayout())
                .withBorder(JBUI.Borders.emptyLeft(5)).andTransparent();

        JBPanel subPanel = new JBPanel(new BorderLayout())
                .withBorder(JBUI.Borders.empty(4, 2, 4 , 5)).andTransparent();

        JBPanel priorityPanel = JiraPanelUtil.createWhitePanel(new GridLayout(1,2));
        authorLabel = JiraLabelUtil.createEmptyLabel().withFont(BOLD);
        createdLabel = JiraLabelUtil.createEmptyLabel().withFont(ITALIC);
        createdLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        priorityPanel.add(authorLabel);
        priorityPanel.add(createdLabel);

        commentArea = new JTextArea();
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        commentArea.setEditable(false);
        commentArea.setColumns(90);

        subPanel.add(priorityPanel, BorderLayout.PAGE_START);

        JScrollPane scrollPane = ScrollPaneFactory.createScrollPane(commentArea, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(UI.Borders.empty());

        subPanel.add(scrollPane, BorderLayout.CENTER);

        commentPanel.add(subPanel, BorderLayout.CENTER);

        add(commentPanel);
    }


    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, false, cellHasFocus);

        JiraIssueComment comment = (JiraIssueComment) value;

        if(isSelected){
            commentPanel.setBorder(JBUI.Borders.customLine(list.getSelectionBackground(), 0, 5, 0, 0));
        }
        else {
            commentPanel.setBorder(JBUI.Borders.emptyLeft(5));
        }

        authorLabel.setText(comment.getAuthor().getDisplayName());
        createdLabel.setText(JiraIssueUtil.getCreated(comment));
        commentArea.setText(getPrettyBody(comment.getBody()));

        return this;
    }

}
