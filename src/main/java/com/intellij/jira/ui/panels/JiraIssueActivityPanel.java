package com.intellij.jira.ui.panels;

import com.intellij.jira.listener.JiraIssueChangeListener;
import com.intellij.jira.listener.JiraIssuesRefreshedListener;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.ui.JiraTabbedPane;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

public class JiraIssueActivityPanel extends JiraTabbedPane {

    private final Project project;
    private JiraIssue issue;

    private Integer mySelectedTab = 0;

    JiraIssueActivityPanel(@NotNull Project project, JiraIssue issue) {
        super(JTabbedPane.BOTTOM);
        this.project = project;
        this.issue = issue;

        addTabs();
        setSelectedIndex(mySelectedTab);
        addChangeListener(e -> mySelectedTab = getSelectedIndex());
        subscribeTopic();
    }

    private String appendTotal(int total) {
        return total > 0 ? " (" + total + ") " : " ";
    }

    private void addTabs() {
        addTab(JiraIssueDetailsPanel.TAB_ATTACHMENTS + appendTotal(issue.getAttachments().size()), new JiraIssueAttachmentsPanel(issue));
        addTab(JiraIssueDetailsPanel.TAB_LINKS + appendTotal(issue.getIssueLinks().size()), new JiraIssueLinksPanel(issue));
        addTab(JiraIssueDetailsPanel.TAB_SUB_TASKS + appendTotal(issue.getSubtasks().size()), new JiraIssueSubtasksPanel(issue));
        addTab(JiraIssueDetailsPanel.TAB_COMMENTS + appendTotal(issue.getRenderedComments().getTotal()), new JiraIssueCommentsPanel(issue));
        addTab(JiraIssueDetailsPanel.TAB_WORK_LOG + appendTotal(issue.getWorklogs().size()), new JiraIssueWorkLogsPanel(issue));
    }

    private void subscribeTopic() {
        MessageBusConnection connect = project.getMessageBus().connect();
        connect.subscribe(JiraIssueChangeListener.TOPIC, issue -> {
            if (issue.getKey().equals(this.issue.getKey())) {
                this.issue = issue;
                updatePanel();
            }
        });

        connect.subscribe(JiraIssuesRefreshedListener.TOPIC, issues -> {
            int issueIndex = issues.indexOf(this.issue);
            if (issueIndex > -1) {
                this.issue = issues.get(issueIndex);
                updatePanel();
            }
        });
    }

    private void updatePanel() {
        ApplicationManager.getApplication().invokeLater(() -> {
            Integer oldSelectedTab = Integer.valueOf(mySelectedTab.intValue());
            while (getTabCount() > 0) {
                remove(0);
            }

            addTabs();
            setSelectedIndex(oldSelectedTab);
            mySelectedTab = getSelectedIndex();
        });
    }

}
