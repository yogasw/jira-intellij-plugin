package com.intellij.jira.ui.panels;

import com.intellij.jira.actions.AddCommentDialogAction;
import com.intellij.jira.actions.DeleteCommentDialogAction;
import com.intellij.jira.actions.EditCommentDialogAction;
import com.intellij.jira.actions.JiraIssueActionGroup;
import com.intellij.jira.rest.JiraIssueCommentsWrapper;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.JiraIssueComment;
import com.intellij.jira.ui.JiraIssueCommentListModel;
import com.intellij.jira.ui.renders.JiraIssueCommentListCellRenderer;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

import static java.awt.BorderLayout.CENTER;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

class JiraIssueCommentsPanel extends AbstractJiraPanel {

    private JiraIssueComment comment;
    private JBList<JiraIssueComment> issueCommentList;

    JiraIssueCommentsPanel(@NotNull JiraIssue issue) {
        super(issue);
        initContent(issue.getRenderedComments());
    }

    @Override
    public ActionGroup getActionGroup() {
        JiraIssueActionGroup group = new JiraIssueActionGroup(this);
        group.add(new AddCommentDialogAction(projectKey, issueKey));
        group.add(new EditCommentDialogAction(projectKey, issueKey, () -> comment));
        group.add(new DeleteCommentDialogAction(issueKey, () -> comment));

        return group;
    }

    private void initContent(JiraIssueCommentsWrapper comments){
        JBPanel panel = new JBPanel(new BorderLayout());

        issueCommentList = new JBList<>();
        issueCommentList.setEmptyText("No comments");
        issueCommentList.setModel(new JiraIssueCommentListModel(comments.getComments()));
        issueCommentList.setCellRenderer(new JiraIssueCommentListCellRenderer());
        issueCommentList.setSelectionMode(SINGLE_SELECTION);
        issueCommentList.addListSelectionListener(e -> {
             SwingUtilities.invokeLater(this::updateToolbarActions);
        });

        panel.add(ScrollPaneFactory.createScrollPane(issueCommentList, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER), CENTER);

        setContent(panel);
    }

    private void updateToolbarActions() {
        JiraIssueComment selectedComment = issueCommentList.getSelectedValue();
        if(!Objects.equals(comment, selectedComment)){
            comment = selectedComment;
            initToolbar();
        }
    }

}
