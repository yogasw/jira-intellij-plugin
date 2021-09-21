package com.intellij.jira.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Separator;

public class ChangelistActionGroup extends DefaultActionGroup {

    public ChangelistActionGroup() {
        super("Changelist", true);
        getTemplatePresentation().setIcon(AllIcons.Vcs.Changelist);
        add(new AddIssueChangeListAction());
        add(new Separator());
        add(new ChangelistSettingsDialogAction());
    }

}
