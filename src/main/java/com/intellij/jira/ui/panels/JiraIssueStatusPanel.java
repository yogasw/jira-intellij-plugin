package com.intellij.jira.ui.panels;

import com.intellij.jira.actions.JiraIssueActionGroup;
import com.intellij.jira.actions.JiraIssueAssigneePopupAction;
import com.intellij.jira.actions.JiraIssuePrioritiesPopupAction;
import com.intellij.jira.actions.TransitIssueDialogAction;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.JiraProjectVersion;
import com.intellij.jira.tasks.ToggleWatchIssueTask;
import com.intellij.jira.util.JiraLabelUtil;
import com.intellij.jira.util.JiraPanelUtil;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.stream.Collectors;

import static com.intellij.jira.util.JiraLabelUtil.DARCULA_TEXT_COLOR;
import static com.intellij.jira.util.JiraLabelUtil.DEFAULT_SELECTED_ISSUE_COLOR;
import static com.intellij.jira.util.JiraLabelUtil.EMPTY_TEXT;
import static com.intellij.jira.util.JiraLabelUtil.HAND_CURSOR;
import static com.intellij.jira.util.JiraPanelUtil.MARGIN_BOTTOM;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.LINE_START;
import static javax.swing.BoxLayout.Y_AXIS;

public class JiraIssueStatusPanel extends AbstractJiraPanel {

    private final JiraIssue issue;

    JiraIssueStatusPanel(Project myProject, JiraIssue issue) {
        super(issue);
        this.issue = issue;
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
        JPanel watchesPanel = JiraPanelUtil.createWhitePanel(new FlowLayout(FlowLayout.LEFT, 0, 0)).withBorder(MARGIN_BOTTOM);
        JBLabel watchesLabel = JiraLabelUtil.createBoldLabel("Watchers: ");
        JBLabel watchesValueLabel = JiraLabelUtil.createLabel(issue.getWatches().getWatchCount() + " ");
        boolean isWatching = issue.getWatches().isWatching();
        JBLabel watchLabel = JiraLabelUtil.createLabel((isWatching ? "Stop " : "Start ") + "watching this issue");
        watchLabel.setBackground(UIUtil.isUnderDarcula() ? DEFAULT_SELECTED_ISSUE_COLOR : DARCULA_TEXT_COLOR);
        watchLabel.setBorder(JBUI.Borders.empty(2, 2, 2, 3));
        watchLabel.setOpaque(true);
        watchLabel.setCursor(HAND_CURSOR);
        watchLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SwingUtilities.invokeLater(() -> new ToggleWatchIssueTask(myProject, issue.getKey(), isWatching).queue());
            }
        });

        watchesPanel.add(watchesLabel);
        watchesPanel.add(watchesValueLabel);
        watchesPanel.add(watchLabel);
        mainPanel.add(watchesPanel);

        // Versions
        JPanel versionsPanel = JiraPanelUtil.createVersionsPanel(issue);
        mainPanel.add(versionsPanel);

        setContent(mainPanel);
    }

    @Override
    public ActionGroup getActionGroup() {
        JiraIssueActionGroup group = new JiraIssueActionGroup(this);
        group.add(new TransitIssueDialogAction(() -> issue));
        group.add(new JiraIssueAssigneePopupAction(() -> issue));
        group.add(new JiraIssuePrioritiesPopupAction(() -> issue));

        return group;
    }

    private String getVersionsNames(List<JiraProjectVersion> versions){
        if(versions.isEmpty()){
            return "None";
        }

        return versions.stream()
                .map(JiraProjectVersion::getName)
                .collect(Collectors.joining(", "));
    }

}
