package com.intellij.jira.ui.panels;

import com.intellij.jira.actions.JiraIssueActionGroup;
import com.intellij.jira.actions.JiraIssueAssigneePopupAction;
import com.intellij.jira.actions.JiraIssuePrioritiesPopupAction;
import com.intellij.jira.actions.TransitIssueDialogAction;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.JiraProjectVersion;
import com.intellij.jira.tasks.ToggleWatchIssueTask;
import com.intellij.jira.util.JiraIconUtil;
import com.intellij.jira.util.JiraLabelUtil;
import com.intellij.jira.util.JiraPanelUtil;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
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
import static com.intellij.jira.util.JiraLabelUtil.createEmptyLabel;
import static com.intellij.jira.util.JiraLabelUtil.createIconLabel;
import static com.intellij.jira.util.JiraPanelUtil.MARGIN_BOTTOM;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.LINE_START;
import static java.util.Objects.nonNull;
import static javax.swing.BoxLayout.X_AXIS;
import static javax.swing.BoxLayout.Y_AXIS;

public class JiraIssueStatusPanel extends AbstractJiraPanel {

    private final JiraIssue issue;

    JiraIssueStatusPanel(Project myProject, JiraIssue issue) {
        super(issue);
        this.issue = issue;
        setBorder(JBUI.Borders.customLine(JBColor.border(),0, 0, 0, 1));

        JPanel mainPanel = new JBPanel().withBackground(JBColor.white).withBorder(JBUI.Borders.empty(5));
        mainPanel.setLayout(new BoxLayout(mainPanel, Y_AXIS));

        // Status
        JPanel statusPanel = JiraPanelUtil.createWhiteBorderPanel();
        statusPanel.setLayout(new BoxLayout( statusPanel, X_AXIS));
        JLabel statusValueLabel = JiraLabelUtil.createStatusLabel(issue.getStatus());

        statusPanel.add(statusValueLabel);
        mainPanel.add(statusPanel);

        // Priority
        JPanel priorityPanel = JiraPanelUtil.createWhiteBorderPanel();
        JBLabel priorityLabel = JiraLabelUtil.createBoldLabel("Priority: ");
        JBLabel priorityValueLabel = nonNull(issue.getPriority()) ? createIconLabel(JiraIconUtil.getIcon(issue.getPriority().getIconUrl()), issue.getPriority().getName()) : createEmptyLabel();

        priorityPanel.add(priorityLabel, LINE_START);
        priorityPanel.add(priorityValueLabel, CENTER);
        mainPanel.add(priorityPanel);

        // Reporter
        JPanel reporterPanel = JiraPanelUtil.createWhiteBorderPanel();
        JBLabel reporterLabel = JiraLabelUtil.createBoldLabel("Reporter: ");
        JBLabel reporterValueLabel = JiraLabelUtil.createLabel(issue.getAssignee() != null ? issue.getReporter().getDisplayName() : EMPTY_TEXT);

        reporterPanel.add(reporterLabel, LINE_START);
        reporterPanel.add(reporterValueLabel, CENTER);
        mainPanel.add(reporterPanel);

        // Assignee
        JPanel assigneePanel = JiraPanelUtil.createWhiteBorderPanel();
        JBLabel assigneeLabel = JiraLabelUtil.createBoldLabel("Assignee: ");
        JBLabel assigneeValueLabel = JiraLabelUtil.createLabel(issue.getAssignee() != null ? issue.getAssignee().getDisplayName() : EMPTY_TEXT);

        assigneePanel.add(assigneeLabel, LINE_START);
        assigneePanel.add(assigneeValueLabel, CENTER);
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
        JPanel versionsPanel = JiraPanelUtil.createWhiteBorderPanel().withBorder(MARGIN_BOTTOM);
        JBLabel versionsLabel = JiraLabelUtil.createBoldLabel("Versions: ");
        JBLabel versionsValueLabel = JiraLabelUtil.createLabel(getVersionsNames(issue.getVersions()));

        versionsPanel.add(versionsLabel, LINE_START);
        versionsPanel.add(versionsValueLabel, CENTER);
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
