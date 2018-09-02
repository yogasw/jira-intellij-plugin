package com.intellij.jira.actions;

import com.intellij.icons.AllIcons;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.JiraIssueTransition;
import com.intellij.jira.tasks.JiraServer;
import com.intellij.jira.tasks.JiraTaskManager;
import com.intellij.jira.ui.popup.JiraIssueTransitionsPopup;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Conditions;
import com.intellij.tools.SimpleActionGroup;
import org.apache.commons.collections.Factory;

import java.util.List;
import java.util.Optional;

import static java.util.Objects.nonNull;

public class JiraIssueTransitionPopupAction extends JiraIssueAction {

    private static final ActionProperties properties = ActionProperties.of("Transit to",  AllIcons.Actions.Forward);
    private Factory factory;


    public JiraIssueTransitionPopupAction(Factory factory) {
        super(properties);
        this.factory = factory;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if(nonNull(project)) {
            JiraTaskManager component = project.getComponent(JiraTaskManager.class);
            Optional<JiraServer> jiraServer = component.getConfiguredJiraServer();
            if(jiraServer.isPresent()){
                JiraIssue issue = (JiraIssue) factory.create();
                List<JiraIssueTransition> transitions = jiraServer.get().getTransitions(issue.getId());
                JiraIssueTransitionsPopup popup = new JiraIssueTransitionsPopup(createActionGroup(transitions, issue), project, Conditions.alwaysTrue());
                popup.showInCenterOf(getComponent());

            }
        }

    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabled(nonNull(factory.create()));
    }


    private ActionGroup createActionGroup(List<JiraIssueTransition> transitions, JiraIssue issue){
        SimpleActionGroup group = new SimpleActionGroup();
        transitions.forEach(t -> group.add(new JiraIssueTransitionExecuteAction(t.getName(), t.getId(), issue.getId())));

        return group;
    }


}