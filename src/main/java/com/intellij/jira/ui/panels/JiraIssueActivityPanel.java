package com.intellij.jira.ui.panels;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.ui.JiraTabbedPane;
import javax.swing.JTabbedPane;
import java.util.HashMap;
import java.util.Map;

public class JiraIssueActivityPanel extends JiraTabbedPane {

    private final Map<String, Integer> data = new HashMap<>();

    JiraIssueActivityPanel(JiraIssue issue) {
        super(JTabbedPane.BOTTOM);

        addTab(JiraIssueDetailsPanel.TAB_ATTACHMENTS + appendTotal(issue.getAttachments().size()), new JiraIssueAttachmentsPanel(issue));
        addTab(JiraIssueDetailsPanel.TAB_LINKS + appendTotal(issue.getIssueLinks().size()), new JiraIssueLinksPanel(issue));
        addTab(JiraIssueDetailsPanel.TAB_SUB_TASKS + appendTotal(issue.getSubtasks().size()), new JiraIssueSubtasksPanel(issue));
        addTab(JiraIssueDetailsPanel.TAB_COMMENTS + appendTotal(issue.getRenderedComments().getTotal()), new JiraIssueCommentsPanel(issue));
        addTab(JiraIssueDetailsPanel.TAB_WORK_LOG + appendTotal(issue.getWorklogs().size()), new JiraIssueWorkLogsPanel(issue));

        addChangeListener(e -> data.put(JiraIssueDetailsPanel.TAB_KEY, getSelectedIndex()));
        setSelectedIndex(data.getOrDefault(JiraIssueDetailsPanel.TAB_KEY, 0));
    }

    private String appendTotal(int total) {
        return total > 0 ? " (" + total + ") " : " ";
    }

}
