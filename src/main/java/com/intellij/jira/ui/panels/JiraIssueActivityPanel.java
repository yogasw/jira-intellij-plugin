package com.intellij.jira.ui.panels;

import com.intellij.jira.components.JiraIssueUpdater;
import com.intellij.jira.events.JiraIssueEventListener;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.ui.JiraTabbedPane;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import java.util.List;
import java.util.Objects;

public class JiraIssueActivityPanel extends JiraTabbedPane implements JiraIssueEventListener, Disposable {

    private final Project project;
    private JiraIssue issue;

    private Integer mySelectedTab = 0;

    JiraIssueActivityPanel(@NotNull Project project, JiraIssue issue) {
        super(JTabbedPane.BOTTOM);
        this.project = project;
        this.issue = issue;

        addTabs();
        setSelectedIndex(mySelectedTab);
        addListeners();
    }

    private String appendTotal(int total) {
        return total > 0 ? " (" + total + ") " : " ";
    }

    @Override
    public void update(List<JiraIssue> issues) {
        // Do nothing
    }

    @Override
    public void update(JiraIssue issue) {
        System.out.println("Updating JiraIssueActivityPanel");
        this.issue = issue;

        SwingUtilities.invokeLater(() -> {
            Integer oldSelectedTab = Integer.valueOf(mySelectedTab.intValue());
            while (getTabCount() > 0) {
                remove(0);
            }

            addTabs();
            setSelectedIndex(oldSelectedTab);
            mySelectedTab = getSelectedIndex();
        });
    }

    @Override
    public void dispose() {
        System.out.println("Removing listener JiraIssueActivityPanel");
        JiraIssueUpdater.getInstance(project).removeIssueListener(issue.getKey(), this);
    }

    private void addTabs() {
        addTab(JiraIssueDetailsPanel.TAB_ATTACHMENTS + appendTotal(issue.getAttachments().size()), new JiraIssueAttachmentsPanel(issue));
        addTab(JiraIssueDetailsPanel.TAB_LINKS + appendTotal(issue.getIssueLinks().size()), new JiraIssueLinksPanel(issue));
        addTab(JiraIssueDetailsPanel.TAB_SUB_TASKS + appendTotal(issue.getSubtasks().size()), new JiraIssueSubtasksPanel(issue));
        addTab(JiraIssueDetailsPanel.TAB_COMMENTS + appendTotal(issue.getRenderedComments().getTotal()), new JiraIssueCommentsPanel(issue));
        addTab(JiraIssueDetailsPanel.TAB_WORK_LOG + appendTotal(issue.getWorklogs().size()), new JiraIssueWorkLogsPanel(issue));
    }

    private void addListeners() {
        JiraIssueUpdater.getInstance(project).addIssueListener(issue.getKey(),this);
        addChangeListener(e -> mySelectedTab = getSelectedIndex());
    }

}
