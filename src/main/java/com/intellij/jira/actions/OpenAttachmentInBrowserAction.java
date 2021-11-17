package com.intellij.jira.actions;

import com.intellij.icons.AllIcons;
import com.intellij.ide.BrowserUtil;
import com.intellij.jira.JiraDataKeys;
import com.intellij.jira.rest.model.JiraIssueAttachment;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.nonNull;

public class OpenAttachmentInBrowserAction extends AnAction {

    public OpenAttachmentInBrowserAction() {
        super("Open in Browser", "", AllIcons.General.Web);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        JiraIssueAttachment issueAttachment = e.getRequiredData(JiraDataKeys.ISSUE_ATTACHMENT);
        BrowserUtil.open(issueAttachment.getContent());
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(nonNull(e.getData(JiraDataKeys.ISSUE_ATTACHMENT)));
    }
}
