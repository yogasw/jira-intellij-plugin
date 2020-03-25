package com.intellij.jira.actions;

import com.intellij.icons.AllIcons;
import com.intellij.ide.BrowserUtil;
import com.intellij.jira.util.factory.JiraIssueAttachmentFactory;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.nonNull;

public class OpenAttachmentInBrowserAction extends AnAction {

    private JiraIssueAttachmentFactory attachmentFactory;

    public OpenAttachmentInBrowserAction(@NotNull JiraIssueAttachmentFactory factory) {
        super("Open in Browser", "", AllIcons.General.Web);
        attachmentFactory = factory;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        BrowserUtil.open(attachmentFactory.create().getContent());
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(nonNull(attachmentFactory.create()));
    }
}
