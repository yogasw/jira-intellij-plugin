package com.intellij.jira.ui.panels;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.ui.JiraTabbedPane;
import com.intellij.openapi.project.Project;

import javax.swing.JTabbedPane;

public class JiraTransitionTaskPanel extends JiraTabbedPane {

    private final Project project;
    private final JiraIssue issue;

    public JiraTransitionTaskPanel(Project project, JiraIssue issue) {
        super(JTabbedPane.TOP);
        this.project = project;
        this.issue = issue;

        addTabs();
    }

    private void addTabs() {
        addTab("Changelist", new JiraTransitionChangelistTaskPanel(project, issue));
    }

}
