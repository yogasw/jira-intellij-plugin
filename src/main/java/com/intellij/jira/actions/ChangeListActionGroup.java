package com.intellij.jira.actions;

import com.intellij.icons.AllIcons;
import com.intellij.jira.util.factory.JiraIssueFactory;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Separator;

public class ChangeListActionGroup extends DefaultActionGroup {

    public ChangeListActionGroup(JiraIssueFactory factory) {
        super("Changelist", true);
        getTemplatePresentation().setIcon(AllIcons.Vcs.Changelist);
        add(new AddIssueChangeListAction(factory));
        add(new Separator());
        add(new ChangelistSettingsDialogAction());
    }

}
