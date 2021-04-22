package com.intellij.jira.ui.panels;

import com.intellij.jira.actions.JiraIssueActionGroup;
import com.intellij.jira.actions.JiraIssueAssigneePopupAction;
import com.intellij.jira.actions.JiraIssuePrioritiesPopupAction;
import com.intellij.jira.actions.TransitIssueDialogAction;
import com.intellij.jira.components.JiraIssueUpdater;
import com.intellij.jira.events.JiraIssueEventListener;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.util.JiraLabelUtil;
import com.intellij.jira.util.JiraPanelUtil;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import java.util.List;

import static com.intellij.jira.util.JiraLabelUtil.EMPTY_TEXT;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.LINE_START;
import static javax.swing.BoxLayout.Y_AXIS;

public class JiraIssueStatusPanel extends AbstractJiraPanel implements JiraIssueEventListener, Disposable {

    private final Project project;
    private JiraIssue issue;

    public JiraIssueStatusPanel(Project project, JiraIssue issue) {
        super(issue);
        this.project = project;
        this.issue = issue;

        init();
        addListeners();
    }

    @Override
    public ActionGroup getActionGroup() {
        JiraIssueActionGroup group = new JiraIssueActionGroup(this);
        group.add(new TransitIssueDialogAction(() -> issue));
        group.add(new JiraIssueAssigneePopupAction(() -> issue));
        group.add(new JiraIssuePrioritiesPopupAction(() -> issue));

        return group;
    }

    @Override
    public void update(List<JiraIssue> issues) {
        // Do nothing
    }

    @Override
    public void update(JiraIssue issue) {
        System.out.println("Updating JiraIssueStatusPanel");
        this.issue = issue;
        init();
    }

    @Override
    public void dispose() {
        System.out.println("Removing listener JiraIssueStatusPanel");
        JiraIssueUpdater.getInstance(project).removeIssueListener(issueKey, this);
    }

    private void init() {
        setBorder(JBUI.Borders.customLine(JBColor.border(),0, 0, 0, 1));

        JPanel mainPanel = JiraPanelUtil.createWhiteBorderPanel().withBorder(JBUI.Borders.empty(5));
        mainPanel.setLayout(new BoxLayout(mainPanel, Y_AXIS));

        // Status
        JPanel statusPanel = JiraPanelUtil.createStatusPanel(issue);
        mainPanel.add(statusPanel);

        // Priority
        JPanel priorityPanel = JiraPanelUtil.createPriorityPanel(issue);
        mainPanel.add(priorityPanel);

        // Reporter
        JPanel reporterPanel = JiraPanelUtil.createWhiteBorderPanel();
        JBLabel reporterLabel = JiraLabelUtil.createBoldLabel("Reporter: ");
        JBLabel reporterValueLabel = JiraLabelUtil.createLabel(issue.getAssignee() != null ? issue.getReporter().getDisplayName() : EMPTY_TEXT);

        reporterPanel.add(reporterLabel, LINE_START);
        reporterPanel.add(reporterValueLabel, CENTER);
        mainPanel.add(reporterPanel);

        // Assignee
        JPanel assigneePanel = JiraPanelUtil.createAssigneePanel(issue);
        mainPanel.add(assigneePanel);

        // Watches
        JPanel watchesPanel = JiraPanelUtil.createWatchesPanel(issue, project);
        mainPanel.add(watchesPanel);

        // Versions
        JPanel versionsPanel = JiraPanelUtil.createVersionsPanel(issue);
        mainPanel.add(versionsPanel);

        setContent(mainPanel);
    }

    private void addListeners() {
        JiraIssueUpdater.getInstance(project).addIssueListener(issueKey, this);
    }
}
