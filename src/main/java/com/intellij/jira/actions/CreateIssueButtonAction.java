package com.intellij.jira.actions;

import com.intellij.ide.ui.laf.darcula.ui.DarculaButtonPainter;
import com.intellij.jira.exceptions.InvalidPermissionException;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.server.JiraServerManager;
import com.intellij.jira.ui.JiraIssueActionPlaces;
import com.intellij.jira.ui.dialog.CreateIssueDialog;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ex.CustomComponentAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.AnActionButton;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.components.BorderLayoutPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;

import static com.intellij.jira.rest.model.JiraPermissionType.BROWSE_PROJECTS;
import static com.intellij.jira.rest.model.JiraPermissionType.CREATE_ISSUES;
import static java.util.Objects.isNull;

public class CreateIssueButtonAction extends AnActionButton implements CustomComponentAction {

    public CreateIssueButtonAction() {

    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if(isNull(project)){
            return;
        }

        JiraServerManager manager = ApplicationManager.getApplication().getService(JiraServerManager.class);
        JiraRestApi jiraRestApi = manager.getJiraRestApi(project);
        if(isNull(jiraRestApi)) {
            return;
        }

        boolean hasPermission = jiraRestApi.userHasPermission(BROWSE_PROJECTS, CREATE_ISSUES);
        if(!hasPermission){
            throw new InvalidPermissionException("Jira", "You don't have permission to create issues");
        }

        CreateIssueDialog createIssueDialog = new CreateIssueDialog(project, jiraRestApi);
        createIssueDialog.show();
    }

    @Override
    public @NotNull JComponent createCustomComponent(@NotNull Presentation presentation, @NotNull String place) {
        JButton createIssueButton = new JButton("Create Issue");
        int buttonHeight = JBUI.scale(26);
        createIssueButton.setPreferredSize(new Dimension(createIssueButton.getPreferredSize().width, buttonHeight));
        createIssueButton.setBorder(new DarculaButtonPainter() {
            @Override
            public Insets getBorderInsets(Component c) {
                return JBUI.emptyInsets();
            }});


        createIssueButton.setFocusable(false);
        createIssueButton.setEnabled(true);

        createIssueButton.addActionListener(e -> {
            DataContext dataContext = ActionToolbar.getDataContextFor(createIssueButton);
            actionPerformed(AnActionEvent.createFromAnAction(this, null, JiraIssueActionPlaces.JIRA_ISSUES_TOOLBAR_PLACE, dataContext));
        });

        JPanel panel = new BorderLayoutPanel().addToCenter(createIssueButton);
        panel.setBorder(JBUI.Borders.emptyLeft(6));

        return panel;
    }
}
