package com.intellij.jira.ui.panels;

import com.intellij.jira.JiraDataKeys;
import com.intellij.jira.actions.JiraIssueActionGroup;
import com.intellij.jira.actions.JiraIssueAssigneePopupAction;
import com.intellij.jira.actions.JiraIssuePrioritiesPopupAction;
import com.intellij.jira.actions.TransitIssueDialogAction;
import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.listener.IssueChangeListener;
import com.intellij.jira.listener.RefreshIssuesListener;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.util.JiraPanelUtil;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static javax.swing.BoxLayout.Y_AXIS;

public class JiraIssueStatusPanel extends AbstractJiraToolWindowPanel {

    private final JiraIssuesData myIssuesData;
    private JiraIssue myIssue;

    public JiraIssueStatusPanel(@NotNull JiraIssuesData issuesData, JiraIssue issue) {
        super(issue);
        myIssuesData = issuesData;
        this.myIssue = issue;

        init();
        subscribeTopic();
    }

    @Override
    public ActionGroup getActionGroup() {
        JiraIssueActionGroup group = new JiraIssueActionGroup(this);
        group.add(new TransitIssueDialogAction());
        group.add(new JiraIssueAssigneePopupAction());
        group.add(new JiraIssuePrioritiesPopupAction());

        return group;
    }

    @Override
    public @Nullable Object getData(@NotNull @NonNls String dataId) {
        if (JiraDataKeys.ISSUE.is(dataId)) {
            return myIssue;
        }

        return super.getData(dataId);
    }

    private void init() {
        JPanel mainPanel = JiraPanelUtil.createWhiteBorderPanel().withBorder(JBUI.Borders.empty(5));
        mainPanel.setLayout(new BoxLayout(mainPanel, Y_AXIS));

        FormBuilder formBuilder = FormBuilder.createFormBuilder();

        // Status
        JPanel statusPanel = JiraPanelUtil.createStatusPanel(myIssue);
        formBuilder.addComponent(statusPanel);

        // Priority
        JPanel priorityPanel = JiraPanelUtil.createPriorityPanel(myIssue);
        formBuilder.addComponent(priorityPanel,0);

        // Reporter
        JPanel reporterPanel = JiraPanelUtil.createReporterPanel(myIssue);
        formBuilder.addComponent(reporterPanel,0);

        // Assignee
        JPanel assigneePanel = JiraPanelUtil.createAssigneePanel(myIssue);
        formBuilder.addComponent(assigneePanel,0);

        // Watches
        JPanel watchesPanel = JiraPanelUtil.createWatchesPanel(myIssue, myIssuesData.getProject());
        formBuilder.addComponent(watchesPanel,0);

        // Versions
        JPanel versionsPanel = JiraPanelUtil.createVersionsPanel(myIssue);
        formBuilder.addComponentFillVertically(versionsPanel, 0);

        mainPanel.add(formBuilder.getPanel());
        setContent(mainPanel);
    }

    private void subscribeTopic() {
        MessageBusConnection connect = myIssuesData.getProject().getMessageBus().connect();

        connect.subscribe(IssueChangeListener.TOPIC, issueKey -> {
            if (issueKey.equals(this.myIssue.getKey())) {
                this.myIssue = myIssuesData.getIssue(issueKey);

                init();
            }
        });

        connect.subscribe(RefreshIssuesListener.TOPIC, () -> {
            this.myIssue = myIssuesData.getIssue(issueKey);

            init();
        });

    }

}
