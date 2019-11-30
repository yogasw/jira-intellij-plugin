package com.intellij.jira.ui.panels;

import com.intellij.jira.actions.JiraIssueActionGroup;
import com.intellij.jira.actions.JiraIssueAssigneePopupAction;
import com.intellij.jira.actions.JiraIssuePrioritiesPopupAction;
import com.intellij.jira.actions.TransitIssueDialogAction;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.JiraIssueComponent;
import com.intellij.jira.rest.model.JiraProjectVersion;
import com.intellij.jira.util.JiraIconUtil;
import com.intellij.jira.util.JiraIssueUtil;
import com.intellij.jira.util.JiraLabelUtil;
import com.intellij.jira.util.JiraPanelUtil;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.ui.JBColor;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.intellij.jira.util.JiraLabelUtil.*;
import static com.intellij.jira.util.JiraPanelUtil.MARGIN_BOTTOM;
import static java.awt.BorderLayout.*;
import static java.util.Objects.nonNull;
import static javax.swing.BoxLayout.X_AXIS;
import static javax.swing.BoxLayout.Y_AXIS;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

class JiraIssuePreviewPanel extends AbstractJiraPanel {

    private JiraIssue issue;

    JiraIssuePreviewPanel(@NotNull JiraIssue issue) {
        super(true, issue);
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

        return group;
    }

    private void initContent() {
        JBPanel previewPanel = new JBPanel(new BorderLayout())
                .withBackground(JBColor.WHITE)
                .withBorder(JBUI.Borders.empty(1, 5, 1, 0));

        JBPanel issueDetails = new JBPanel().withBackground(JBColor.WHITE);
        issueDetails.setLayout(new BoxLayout(issueDetails, Y_AXIS));

        // Key and updated
        JBPanel keyAndUpdatedPanel = JiraPanelUtil.createWhitePanel(new GridLayout(1, 2)).withBorder(MARGIN_BOTTOM);
        JBPanel issueAndProjectKeyPanel = new JBPanel().withBackground(JBColor.WHITE);
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
        JBPanel issueSummaryPanel = JiraPanelUtil.createWhitePanel(new BorderLayout()).withBorder(MARGIN_BOTTOM);
        JBLabel summaryLabel = JiraLabelUtil.createLabel(issue.getSummary());
        issueSummaryPanel.add(summaryLabel, LINE_START);

        issueDetails.add(issueSummaryPanel);

        // Type and Status
        JBPanel typeAndStatusPanel = JiraPanelUtil.createWhitePanel(new GridLayout(1, 2)).withBorder(MARGIN_BOTTOM);
        JBPanel typePanel = JiraPanelUtil.createWhitePanel(new BorderLayout());
        JBLabel typeLabel = JiraLabelUtil.createLabel("Type: ").withFont(BOLD);
        JBLabel typeValueLabel = JiraLabelUtil.createLabel(issue.getIssuetype().getName());

        typePanel.add(typeLabel, LINE_START);
        typePanel.add(typeValueLabel, CENTER);

        JBPanel statusPanel = new JBPanel().withBackground(JBColor.white);
        statusPanel.setLayout(new BoxLayout( statusPanel, X_AXIS));

        JBLabel statusLabel = JiraLabelUtil.createLabel("Status: ").withFont(BOLD);
        JLabel statusValueLabel = JiraLabelUtil.createStatusLabel(issue.getStatus());

        statusPanel.add(statusLabel);
        statusPanel.add(statusValueLabel);

        typeAndStatusPanel.add(typePanel);
        typeAndStatusPanel.add(statusPanel);

        issueDetails.add(typeAndStatusPanel);

        // Priority and Assignee
        JBPanel priorityAndAssigneePanel = JiraPanelUtil.createWhitePanel(new GridLayout(1, 2)).withBorder(MARGIN_BOTTOM);
        JBPanel priorityPanel = JiraPanelUtil.createWhitePanel(new BorderLayout());
        JBLabel priorityLabel = JiraLabelUtil.createLabel("Priority: ").withFont(BOLD);
        JBLabel priorityValueLabel = nonNull(issue.getPriority()) ? createIconLabel(JiraIconUtil.getSmallIcon(issue.getPriority().getIconUrl()), issue.getPriority().getName()) : createEmptyLabel();

        priorityPanel.add(priorityLabel, LINE_START);
        priorityPanel.add(priorityValueLabel, CENTER);

        JBPanel assigneePanel = JiraPanelUtil.createWhitePanel(new BorderLayout());
        JBLabel assigneeLabel = JiraLabelUtil.createLabel("Assigne: ").withFont(BOLD);
        JBLabel assigneeValueLabel = JiraLabelUtil.createLabel(issue.getAssignee() != null ? issue.getAssignee().getDisplayName() : "");

        assigneePanel.add(assigneeLabel, LINE_START);
        assigneePanel.add(assigneeValueLabel, CENTER);

        priorityAndAssigneePanel.add(priorityPanel);
        priorityAndAssigneePanel.add(assigneePanel);

        issueDetails.add(priorityAndAssigneePanel);

        // Versions
        JBPanel versionsPanel = JiraPanelUtil.createWhitePanel(new BorderLayout()).withBorder(MARGIN_BOTTOM);
        JBLabel versionsLabel = JiraLabelUtil.createLabel("Versions: ").withFont(BOLD);
        JBLabel versionsValueLabel = JiraLabelUtil.createLabel(getVersionsNames(issue.getVersions()));

        versionsPanel.add(versionsLabel, LINE_START);
        versionsPanel.add(versionsValueLabel, CENTER);

        issueDetails.add(versionsPanel);

        // Components
        if(issue.hasComponents()){
            JBPanel componentsPanel = JiraPanelUtil.createWhitePanel(new BorderLayout()).withBorder(MARGIN_BOTTOM);
            JBLabel componentsLabel = JiraLabelUtil.createLabel("Components: ").withFont(BOLD);
            JBLabel componentsValueLabel = JiraLabelUtil.createLabel(getComponentNames(issue.getComponents()));

            componentsPanel.add(componentsLabel, LINE_START);
            componentsPanel.add(componentsValueLabel, CENTER);

            issueDetails.add(componentsPanel);
        }

        // Labels
        if(issue.hasLabels()){
            JBPanel labelsPanel = JiraPanelUtil.createWhitePanel(new BorderLayout()).withBorder(MARGIN_BOTTOM);

            labelsPanel.add(JiraLabelUtil.createLabel("Labels: ").withFont(BOLD), LINE_START);
            labelsPanel.add(JiraLabelUtil.createLabel(String.join(", ", issue.getLabels())), CENTER);

            issueDetails.add(labelsPanel);
        }

        // Description
        JBPanel issueDescriptionPanel = JiraPanelUtil.createWhitePanel(new BorderLayout());
        JBLabel descriptionLabel = JiraLabelUtil.createLabel("Description: ").withFont(BOLD).withBorder(MARGIN_BOTTOM);
        JTextArea descriptionArea = new JTextArea(nonNull(issue.getDescription()) ? issue.getDescription() : "");
        descriptionArea.setLineWrap(true);
        descriptionArea.setEditable(false);

        issueDescriptionPanel.add(descriptionLabel, PAGE_START);
        issueDescriptionPanel.add(descriptionArea, CENTER);

        issueDetails.add(issueDescriptionPanel);

        previewPanel.add(ScrollPaneFactory.createScrollPane(issueDetails, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED), CENTER);

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
