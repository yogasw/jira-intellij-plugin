package com.intellij.jira.ui.panels;

import com.intellij.jira.actions.*;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.JiraIssueComponent;
import com.intellij.jira.rest.model.JiraProjectVersion;
import com.intellij.jira.tasks.ToggleWatchIssueTask;
import com.intellij.jira.ui.JiraTextPane;
import com.intellij.jira.util.JiraIconUtil;
import com.intellij.jira.util.JiraIssueUtil;
import com.intellij.jira.util.JiraLabelUtil;
import com.intellij.jira.util.JiraPanelUtil;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vcs.changes.actions.AddChangeListAction;
import com.intellij.ui.JBColor;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.stream.Collectors;

import static com.intellij.jira.util.JiraLabelUtil.*;
import static com.intellij.jira.util.JiraPanelUtil.MARGIN_BOTTOM;
import static java.awt.BorderLayout.*;
import static java.util.Objects.nonNull;
import static javax.swing.BoxLayout.X_AXIS;
import static javax.swing.BoxLayout.Y_AXIS;
import static javax.swing.ScrollPaneConstants.*;

class JiraIssuePreviewPanel extends AbstractJiraPanel {

    private final Project myProject;
    private JiraIssue issue;

    JiraIssuePreviewPanel(@NotNull Project project, @NotNull JiraIssue issue) {
        super(true, issue);
        this.myProject = project;
        this.issue = issue;
        setBackground(JBColor.white);

        initContent();
    }

    @Override
    public ActionGroup getActionGroup() {
        JiraIssueActionGroup group = new JiraIssueActionGroup(this);
        group.add(new TransitIssueDialogAction(() -> issue));
        group.add(new JiraIssueAssigneePopupAction(() -> issue));
        group.add(new JiraIssuePrioritiesPopupAction(() -> issue));
        group.add(new ChangeListActionGroup(() -> issue));

        return group;
    }

    private void initContent() {
        JPanel previewPanel = new JBPanel(new BorderLayout())
                .withBackground(JBColor.WHITE)
                .withBorder(JBUI.Borders.empty(1, 5, 1, 0));

        JPanel issueDetails = new JBPanel().withBackground(JBColor.WHITE);
        issueDetails.setLayout(new BoxLayout(issueDetails, Y_AXIS));

        // Key and updated
        JPanel keyAndUpdatedPanel = JiraPanelUtil.createWhitePanel(new GridLayout(1, 2)).withBorder(MARGIN_BOTTOM);
        JPanel issueAndProjectKeyPanel = new JBPanel().withBackground(JBColor.WHITE);
        issueAndProjectKeyPanel.setLayout(new BoxLayout(issueAndProjectKeyPanel, X_AXIS));
        JBLabel projectKeyLabel = JiraLabelUtil.createLinkLabel(issue.getProject().getName(), issue.getProject().getUrl());
        JBLabel separatorLabel = JiraLabelUtil.createLabel(" / ");
        JBLabel issueKeyLabel = JiraLabelUtil.createLinkLabel(issue.getKey(), issue.getUrl());

        issueAndProjectKeyPanel.add(projectKeyLabel);
        issueAndProjectKeyPanel.add(separatorLabel);
        issueAndProjectKeyPanel.add(issueKeyLabel);

        JBLabel updatedLabel = JiraLabelUtil.createLabel(JiraIssueUtil.getUpdated(issue), SwingConstants.RIGHT).withFont(ITALIC);
        updatedLabel.setForeground(JBColor.darkGray);
        updatedLabel.setToolTipText("Updated");

        keyAndUpdatedPanel.add(issueAndProjectKeyPanel);
        keyAndUpdatedPanel.add(updatedLabel);

        issueDetails.add(keyAndUpdatedPanel);

        // Summary
        if (StringUtil.isNotEmpty(issue.getSummary())) {
            JPanel issueSummaryPanel = JiraPanelUtil.createWhitePanel(new BorderLayout()).withBorder(MARGIN_BOTTOM);
            JTextArea summaryArea = new JTextArea(issue.getSummary());
            summaryArea.setLineWrap(true);
            summaryArea.setWrapStyleWord(true);
            summaryArea.setEditable(false);
            summaryArea. setBackground(UIUtil.isUnderDarcula() ? DACULA_DEFAULT_COLOR : WHITE);

            issueSummaryPanel.add(summaryArea, CENTER);

            issueDetails.add(issueSummaryPanel);
        }

        // Type and Status
        JPanel typeAndStatusPanel = JiraPanelUtil.createWhitePanel(new GridLayout(1, 2)).withBorder(MARGIN_BOTTOM);
        JPanel typePanel = JiraPanelUtil.createWhitePanel(new BorderLayout());
        JBLabel typeLabel = JiraLabelUtil.createLabel("Type: ").withFont(BOLD);
        JBLabel typeValueLabel = JiraLabelUtil.createLabel(issue.getIssuetype().getName());

        typePanel.add(typeLabel, LINE_START);
        typePanel.add(typeValueLabel, CENTER);

        JPanel statusPanel = new JBPanel().withBackground(JBColor.white);
        statusPanel.setLayout(new BoxLayout( statusPanel, X_AXIS));

        JBLabel statusLabel = JiraLabelUtil.createLabel("Status: ").withFont(BOLD);
        JLabel statusValueLabel = JiraLabelUtil.createStatusLabel(issue.getStatus());

        statusPanel.add(statusLabel);
        statusPanel.add(statusValueLabel);

        typeAndStatusPanel.add(typePanel);
        typeAndStatusPanel.add(statusPanel);

        issueDetails.add(typeAndStatusPanel);

        // Priority and Assignee
        JPanel priorityAndAssigneePanel = JiraPanelUtil.createWhitePanel(new GridLayout(1, 2)).withBorder(MARGIN_BOTTOM);
        JPanel priorityPanel = JiraPanelUtil.createWhitePanel(new BorderLayout());
        JBLabel priorityLabel = JiraLabelUtil.createLabel("Priority: ").withFont(BOLD);
        JBLabel priorityValueLabel = nonNull(issue.getPriority()) ? createIconLabel(JiraIconUtil.getIcon(issue.getPriority().getIconUrl()), issue.getPriority().getName()) : createEmptyLabel();

        priorityPanel.add(priorityLabel, LINE_START);
        priorityPanel.add(priorityValueLabel, CENTER);

        JPanel assigneePanel = JiraPanelUtil.createWhitePanel(new BorderLayout());
        JBLabel assigneeLabel = JiraLabelUtil.createLabel("Assigne: ").withFont(BOLD);
        JBLabel assigneeValueLabel = JiraLabelUtil.createLabel(issue.getAssignee() != null ? issue.getAssignee().getDisplayName() : EMPTY_TEXT);

        assigneePanel.add(assigneeLabel, LINE_START);
        assigneePanel.add(assigneeValueLabel, CENTER);

        priorityAndAssigneePanel.add(priorityPanel);
        priorityAndAssigneePanel.add(assigneePanel);

        issueDetails.add(priorityAndAssigneePanel);

        // Watches
        JPanel watchesPanel = JiraPanelUtil.createWhitePanel(new FlowLayout(FlowLayout.LEFT, 0, 0)).withBorder(MARGIN_BOTTOM);
        JBLabel watchesLabel = JiraLabelUtil.createLabel("Watchers: ").withFont(BOLD);
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

        issueDetails.add(watchesPanel);

        // Versions
        JPanel versionsPanel = JiraPanelUtil.createWhitePanel(new BorderLayout()).withBorder(MARGIN_BOTTOM);
        JBLabel versionsLabel = JiraLabelUtil.createLabel("Versions: ").withFont(BOLD);
        JBLabel versionsValueLabel = JiraLabelUtil.createLabel(getVersionsNames(issue.getVersions()));

        versionsPanel.add(versionsLabel, LINE_START);
        versionsPanel.add(versionsValueLabel, CENTER);

        issueDetails.add(versionsPanel);

        // Components
        if(issue.hasComponents()){
            JPanel componentsPanel = JiraPanelUtil.createWhitePanel(new BorderLayout()).withBorder(MARGIN_BOTTOM);
            JBLabel componentsLabel = JiraLabelUtil.createLabel("Components: ").withFont(BOLD);
            JBLabel componentsValueLabel = JiraLabelUtil.createLabel(getComponentNames(issue.getComponents()));

            componentsPanel.add(componentsLabel, LINE_START);
            componentsPanel.add(componentsValueLabel, CENTER);

            issueDetails.add(componentsPanel);
        }

        // Labels
        if(issue.hasLabels()){
            JPanel labelsPanel = JiraPanelUtil.createWhitePanel(new BorderLayout()).withBorder(MARGIN_BOTTOM);

            labelsPanel.add(JiraLabelUtil.createLabel("Labels: ").withFont(BOLD), LINE_START);
            labelsPanel.add(JiraLabelUtil.createLabel(String.join(", ", issue.getLabels())), CENTER);

            issueDetails.add(labelsPanel);
        }

        // Description
        if (StringUtil.isNotEmpty(issue.getRenderedDescription())) {
            JPanel issueDescriptionPanel = JiraPanelUtil.createWhitePanel(new BorderLayout());
            JBLabel descriptionLabel = JiraLabelUtil.createLabel("Description: ").withFont(BOLD);
            JiraTextPane descriptionTextPane = new JiraTextPane();
            descriptionTextPane.setHTMLText(issue.getRenderedDescription());

            issueDescriptionPanel.add(descriptionLabel, PAGE_START);
            issueDescriptionPanel.add(descriptionTextPane, CENTER);

            issueDetails.add(issueDescriptionPanel);
        }

        previewPanel.add(ScrollPaneFactory.createScrollPane(issueDetails, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER), CENTER);

        setContent(previewPanel);
    }

    private String getVersionsNames(List<JiraProjectVersion> versions){
        if(versions.isEmpty()){
            return "None";
        }

        return versions.stream()
                .map(JiraProjectVersion::getName)
                .collect(Collectors.joining(", "));
    }

    private String getComponentNames(List<JiraIssueComponent> components){
        return components.stream()
                .map(JiraIssueComponent::getName)
                .collect(Collectors.joining(", "));
    }

}
