package com.intellij.jira.ui.panels;

import com.intellij.jira.JiraDataKeys;
import com.intellij.jira.actions.*;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.ui.JiraTextPane;
import com.intellij.jira.util.JiraIssueUtil;
import com.intellij.jira.util.JiraLabelUtil;
import com.intellij.jira.util.JiraPanelUtil;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.JBColor;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static com.intellij.jira.util.JiraLabelUtil.*;
import static com.intellij.jira.util.JiraPanelUtil.MARGIN_BOTTOM;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.PAGE_START;
import static javax.swing.BoxLayout.X_AXIS;
import static javax.swing.BoxLayout.Y_AXIS;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

class JiraIssuePreviewPanel extends AbstractJiraToolWindowPanel {

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
        group.add(new TransitIssueDialogAction());
        group.add(new JiraIssueAssigneePopupAction());
        group.add(new JiraIssuePrioritiesPopupAction());
        group.add(new ChangelistActionGroup());
        group.addSeparator();
        group.add(new OpenNewJiraTabAction());

        return group;
    }

    @Override
    public @Nullable Object getData(@NotNull @NonNls String dataId) {
        if (JiraDataKeys.ISSUE.is(dataId)) {
            return issue;
        }

        return super.getData(dataId);
    }

    private void initContent() {
        JPanel previewPanel = JiraPanelUtil.createWhiteBorderPanel()
                .withBorder(JBUI.Borders.empty(1, 5, 1, 0));

        JPanel issueDetails = JiraPanelUtil.createWhiteBorderPanel();
        issueDetails.setLayout(new BoxLayout(issueDetails, Y_AXIS));

        // Key and updated
        JPanel keyAndUpdatedPanel = JiraPanelUtil.createWhiteGridPanel(1, 2).withBorder(MARGIN_BOTTOM);
        JPanel issueAndProjectKeyPanel = new JiraPanel().withBackground(JBColor.WHITE);
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
            JPanel issueSummaryPanel = JiraPanelUtil.createWhiteBorderPanel();
            JTextArea summaryArea = new JTextArea(issue.getSummary());
            summaryArea.setLineWrap(true);
            summaryArea.setWrapStyleWord(true);
            summaryArea.setEditable(false);
            summaryArea.setBackground(UIUtil.isUnderDarcula() ? DACULA_DEFAULT_COLOR : WHITE);

            issueSummaryPanel.add(summaryArea, CENTER);

            issueDetails.add(issueSummaryPanel);
        }

        // Type and Status
        JPanel typeAndStatusPanel = JiraPanelUtil.createWhiteGridPanel(1, 2);
        JPanel typePanel = JiraPanelUtil.createTypePanel(issue);
        JPanel statusPanel = JiraPanelUtil.createStatusPanel(issue);

        typeAndStatusPanel.add(typePanel);
        typeAndStatusPanel.add(statusPanel);

        issueDetails.add(typeAndStatusPanel);

        // Priority and Assignee
        JPanel priorityAndAssigneePanel = JiraPanelUtil.createWhiteGridPanel(1, 2);
        JPanel priorityPanel = JiraPanelUtil.createPriorityPanel(issue);
        JPanel assigneePanel = JiraPanelUtil.createAssigneePanel(issue);

        priorityAndAssigneePanel.add(priorityPanel);
        priorityAndAssigneePanel.add(assigneePanel);

        issueDetails.add(priorityAndAssigneePanel);

        // Watches
        JPanel watchesPanel = JiraPanelUtil.createWatchesPanel(issue, myProject);
        issueDetails.add(watchesPanel);

        // Versions
        JPanel versionsPanel = JiraPanelUtil.createVersionsPanel(issue);
        issueDetails.add(versionsPanel);

        // Components
        if(issue.hasComponents()){
            JPanel componentsPanel = JiraPanelUtil.createComponentsPanel(issue);
            issueDetails.add(componentsPanel);
        }

        // Labels
        if(issue.hasLabels()){
            JPanel labelsPanel = JiraPanelUtil.createLabelsPanel(issue);
            issueDetails.add(labelsPanel);
        }

        // Description
        if (StringUtil.isNotEmpty(issue.getRenderedDescription())) {
            JPanel issueDescriptionPanel = JiraPanelUtil.createWhiteBorderPanel();
            JBLabel descriptionLabel = JiraLabelUtil.createBoldLabel("Description: ");
            JiraTextPane descriptionTextPane = new JiraTextPane();
            descriptionTextPane.setHTMLText(issue.getRenderedDescription());

            issueDescriptionPanel.add(descriptionLabel, PAGE_START);
            issueDescriptionPanel.add(descriptionTextPane, CENTER);

            issueDetails.add(issueDescriptionPanel);
        }

        previewPanel.add(ScrollPaneFactory.createScrollPane(issueDetails, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER), CENTER);

        setContent(previewPanel);
    }

}
