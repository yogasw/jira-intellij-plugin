package com.intellij.jira.ui.panels;

import com.intellij.jira.actions.AddWorklogDialogAction;
import com.intellij.jira.actions.DeleteWorklogDialogAction;
import com.intellij.jira.actions.EditWorklogDialogAction;
import com.intellij.jira.actions.JiraIssueActionGroup;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.JiraIssueTimeTracking;
import com.intellij.jira.rest.model.JiraIssueWorklog;
import com.intellij.jira.ui.JiraIssueWorklogListModel;
import com.intellij.jira.ui.renders.JiraIssueWorklogListCellRender;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;

import static com.intellij.jira.ui.JiraToolWindowFactory.TOOL_WINDOW_ID;
import static java.awt.BorderLayout.CENTER;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

public class JiraIssueWorklogsPanel extends SimpleToolWindowPanel {

    private String issueKey;
    private String projectKey;
    private List<JiraIssueWorklog> worklogs;
    private JiraIssueWorklog worklog;
    private JiraIssueTimeTracking timeTracking;

    private JBList<JiraIssueWorklog> issueWorklogList;

    public JiraIssueWorklogsPanel(@NotNull JiraIssue issue) {
        super(true);
        this.issueKey = issue.getKey();
        this.projectKey = issue.getProject().getKey();
        this.worklogs = issue.getWorklogs();
        this.timeTracking = issue.getTimetracking();

        initToolbar();
        initContent();
    }

    private void initToolbar() {
        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar(TOOL_WINDOW_ID, createActionGroup(), true);
        actionToolbar.setTargetComponent(this);

        Box toolBarBox = Box.createHorizontalBox();
        toolBarBox.add(actionToolbar.getComponent());
        setToolbar(toolBarBox);
    }

    private ActionGroup createActionGroup() {
        JiraIssueActionGroup group = new JiraIssueActionGroup(this);
        group.add(new AddWorklogDialogAction(issueKey, projectKey, () -> timeTracking));
        group.add(new EditWorklogDialogAction(issueKey, projectKey, () -> worklog, () -> timeTracking));
        group.add(new DeleteWorklogDialogAction(issueKey, () -> worklog, () -> timeTracking));

        return group;
    }

    private void initContent(){
        JBPanel panel = new JBPanel(new BorderLayout());

        issueWorklogList = new JBList<>();
        issueWorklogList.setEmptyText("No work logs");
        issueWorklogList.setModel(new JiraIssueWorklogListModel(worklogs));
        issueWorklogList.setCellRenderer(new JiraIssueWorklogListCellRender());
        issueWorklogList.setSelectionMode(SINGLE_SELECTION);
        issueWorklogList.addListSelectionListener(e -> {
            SwingUtilities.invokeLater(this::updateToolbarActions);
        });

        panel.add(ScrollPaneFactory.createScrollPane(issueWorklogList, VERTICAL_SCROLLBAR_AS_NEEDED), CENTER);

        setContent(panel);
    }

    private void updateToolbarActions() {
        JiraIssueWorklog selectedWorklog = issueWorklogList.getSelectedValue();
        if(!Objects.equals(worklog, selectedWorklog)){
            worklog = selectedWorklog;
            initToolbar();
        }
    }


}
