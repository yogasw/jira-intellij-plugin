package com.intellij.jira.ui.panels;

import com.intellij.jira.actions.JiraIssueActionGroup;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.JiraIssueComment;
import com.intellij.jira.ui.renders.JiraIssueCommentListCellRenderer;
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

import static com.intellij.jira.ui.JiraToolWindowFactory.TOOL_WINDOW_ID;
import static java.awt.BorderLayout.CENTER;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

public class JiraIssueWorklogsPanel extends SimpleToolWindowPanel {

    private String issueKey;
    private JBList<JiraIssueComment> issueWorklogList;

    public JiraIssueWorklogsPanel(@NotNull JiraIssue issue) {
        super(true);
        this.issueKey = issue.getKey();
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
       // group.add(new AddCommentDialogAction(projectKey, issueKey));
       // group.add(new EditCommentDialogAction(projectKey, issueKey, () -> comment));
       // group.add(new DeleteCommentDialogAction(issueKey, () -> comment));

        return group;
    }


    private void initContent(){
        JBPanel panel = new JBPanel(new BorderLayout());

        issueWorklogList = new JBList<>();
        issueWorklogList.setEmptyText("No comments");
      //  issueWorklogList.setModel(new JiraIssueCommentListModel(comments.getComments()));
        issueWorklogList.setCellRenderer(new JiraIssueCommentListCellRenderer());
        issueWorklogList.setSelectionMode(SINGLE_SELECTION);
       /* issueWorklogList.addListSelectionListener(e -> {
            SwingUtilities.invokeLater(this::updateToolbarActions);
        });*/

        panel.add(ScrollPaneFactory.createScrollPane(issueWorklogList, VERTICAL_SCROLLBAR_AS_NEEDED), CENTER);

        setContent(panel);
    }

}
