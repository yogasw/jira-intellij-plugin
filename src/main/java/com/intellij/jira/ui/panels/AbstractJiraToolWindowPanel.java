package com.intellij.jira.ui.panels;

import com.intellij.jira.JiraDataKeys;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.components.panels.Wrapper;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static com.intellij.jira.ui.JiraToolWindowFactory.TOOL_WINDOW_ID;

public abstract class AbstractJiraToolWindowPanel extends SimpleToolWindowPanel {

    protected final String issueKey;
    protected final String projectKey;
    private Wrapper myToolbarWrapper;

    AbstractJiraToolWindowPanel(JiraIssue issue) {
        this(false, issue);
    }

    AbstractJiraToolWindowPanel(boolean borderless, JiraIssue issue) {
        this(true, borderless, issue);
    }

    public AbstractJiraToolWindowPanel(boolean vertical, boolean borderless, JiraIssue issue) {
        this(vertical, borderless, issue.getKey(), issue.getProject().getKey());
    }

    public AbstractJiraToolWindowPanel(boolean vertical, boolean borderless, String issueKey, String projectKey) {
        super(vertical, borderless);
        this.issueKey = issueKey;
        this.projectKey = projectKey;

        initToolbar();
    }

    @Override
    public @Nullable Object getData(@NotNull @NonNls String dataId) {
        if (JiraDataKeys.ISSUE_KEY.is(dataId)) {
            return issueKey;
        } else if (JiraDataKeys.PROJECT_KEY.is(dataId)) {
            return projectKey;
        }

        return super.getData(dataId);
    }

    public void initToolbar(){
        ActionToolbar actionToolbar = getActionToolbar();
        actionToolbar.setTargetComponent(this);

        Box toolBarBox = getToolBarBox();
        toolBarBox.add(actionToolbar.getComponent());
        myToolbarWrapper = new Wrapper(actionToolbar.getComponent());
        setToolbar(myToolbarWrapper);
        //GuiUtils.installVisibilityReferent(myToolbarWrapper, actionToolbar.getComponent());
    }

    public void setToolbarHeightReferent(@NotNull JComponent referent) {
        myToolbarWrapper.setVerticalSizeReferent(referent);
    }

    public ActionToolbar getActionToolbar(){
        return ActionManager.getInstance().createActionToolbar(TOOL_WINDOW_ID, getActionGroup(), true);
    }

    public Box getToolBarBox(){
        return Box.createHorizontalBox();
    }

    public abstract ActionGroup getActionGroup();

}
