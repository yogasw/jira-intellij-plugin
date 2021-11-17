package com.intellij.jira.actions;

import com.intellij.openapi.actionSystem.DefaultActionGroup;

public class JiraIssueToolbarPopupActionGroup extends DefaultActionGroup {

    @Override
    public boolean isDumbAware() {
        return true;
    }
}
