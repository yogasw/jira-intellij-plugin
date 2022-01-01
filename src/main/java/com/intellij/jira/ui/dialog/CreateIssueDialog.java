package com.intellij.jira.ui.dialog;

import com.intellij.jira.JiraDataKeys;
import com.intellij.jira.rest.model.metadata.CreateIssueEditor;
import com.intellij.jira.rest.model.metadata.JiraIssueCreateMetadata;
import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

public class CreateIssueDialog extends DialogWrapper implements DataProvider {

    private final CreateIssueEditor myCreateIssueEditor;

    public CreateIssueDialog(@Nullable Project project, JiraIssueCreateMetadata issueCreateMeta) {
        super(project, false);
        myCreateIssueEditor = new CreateIssueEditor(issueCreateMeta);

        setTitle("Create Issue");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return myCreateIssueEditor.createPanel();
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {
        return myCreateIssueEditor.validate();
    }

    @Override
    protected void doOKAction() {
        // TODO: 15/1/22 implement

        close(0);
    }

    @Override
    public @Nullable Object getData(@NotNull @NonNls String dataId) {
        if (JiraDataKeys.PROJECT_KEY.is(dataId)) {
            return myCreateIssueEditor.getProjectKey();
        }

        return null;
    }
}
