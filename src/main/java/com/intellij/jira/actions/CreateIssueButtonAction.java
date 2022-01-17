package com.intellij.jira.actions;

import com.intellij.ide.ui.laf.darcula.ui.DarculaButtonPainter;
import com.intellij.jira.exceptions.InvalidPermissionException;
import com.intellij.jira.rest.model.JiraPermissionType;
import com.intellij.jira.rest.model.metadata.JiraIssueCreateMetadata;
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

import static java.util.Objects.isNull;

public class CreateIssueButtonAction extends AnActionButton implements CustomComponentAction {

    private JButton myButton;
    private JPanel myPanel;

    public CreateIssueButtonAction() {
        myButton = new JButton("Create Issue");
        int buttonHeight = JBUI.scale(28);
        myButton.setPreferredSize(new Dimension(myButton.getPreferredSize().width, buttonHeight));
        myButton.setBorder(new DarculaButtonPainter() {
                               @Override
                               public Insets getBorderInsets(Component c) {
                                   return JBUI.emptyInsets();
                               }});


        myButton.setFocusable(false);
        myButton.setEnabled(true);

        myButton.addActionListener(e -> {
            DataContext dataContext = ActionToolbar.getDataContextFor(myButton);
            actionPerformed(AnActionEvent.createFromAnAction(this, null, JiraIssueActionPlaces.JIRA_ISSUES_TOOLBAR_PLACE, dataContext));
        });

        myPanel = new BorderLayoutPanel().addToCenter(myButton);
        myPanel.setBorder(JBUI.Borders.emptyLeft(6));
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

        boolean hasPermission = jiraRestApi.userHasPermission(JiraPermissionType.CREATE_ISSUES);
        if(!hasPermission){
            throw new InvalidPermissionException("Jira", "You don't have permission to create issue");
        }

        JiraIssueCreateMetadata issueCreateMeta = jiraRestApi.getIssueCreateMeta();

        CreateIssueDialog createIssueDialog = new CreateIssueDialog(project, issueCreateMeta);
        createIssueDialog.show();
    }


    @Override
    public @NotNull JComponent createCustomComponent(@NotNull Presentation presentation, @NotNull String place) {
        return myPanel;
    }
}
