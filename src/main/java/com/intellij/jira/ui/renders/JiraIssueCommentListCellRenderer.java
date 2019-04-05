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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.intellij.jira.util.JiraLabelUtil.BOLD;
import static com.intellij.jira.util.JiraLabelUtil.ITALIC;

public class JiraIssueCommentListCellRenderer extends DefaultJiraListCellRender {

    private static final Pattern BODY_NAME_PATTERN = Pattern.compile("(\\[~(\\w+)])");

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

        subPanel.add(priorityPanel, BorderLayout.PAGE_START);
        subPanel.add(commentArea, BorderLayout.CENTER);

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



    private String getPrettyBody(String body){
        Matcher m = BODY_NAME_PATTERN.matcher(body);
        if(m.find()){
            body = body.replace(m.group(1), m.group(2));
        }

        return body;
    }


}
