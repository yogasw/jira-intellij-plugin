package com.intellij.jira.ui.panels;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.ui.SimpleToolWindowPanel;

import javax.swing.*;

import static com.intellij.jira.ui.JiraToolWindowFactory.TOOL_WINDOW_ID;

public abstract class AbstractJiraToolWindowPanel extends SimpleToolWindowPanel {

    protected final String issueKey;
    protected final String projectKey;

    AbstractJiraToolWindowPanel(JiraIssue issue) {
        this(false, issue);
    }

    AbstractJiraToolWindowPanel(boolean borderless, JiraIssue issue) {
        this(true, borderless, issue);
    }

    public AbstractJiraToolWindowPanel(boolean vertical, boolean borderless, JiraIssue issue) {
        super(vertical, borderless);
        this.issueKey = issue.getKey();
        this.projectKey = issue.getProject().getKey();

        initToolbar();
    }

    public void initToolbar(){
        ActionToolbar actionToolbar = getActionToolbar();
        actionToolbar.setTargetComponent(this);

        Box toolBarBox = getToolBarBox();
        toolBarBox.add(actionToolbar.getComponent());
        setToolbar(toolBarBox);
    }

    public ActionToolbar getActionToolbar(){
        return ActionManager.getInstance().createActionToolbar(TOOL_WINDOW_ID, getActionGroup(), true);
    }

    public Box getToolBarBox(){
        return Box.createHorizontalBox();
    }

    public abstract ActionGroup getActionGroup();

}
