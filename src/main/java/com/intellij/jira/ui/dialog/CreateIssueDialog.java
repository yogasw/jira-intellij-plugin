package com.intellij.jira.ui.dialog;

import com.intellij.jira.rest.model.metadata.CreateIssueEditor;
import com.intellij.jira.rest.model.metadata.JiraIssueCreateMetadata;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

public class CreateIssueDialog extends DialogWrapper {

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

}
