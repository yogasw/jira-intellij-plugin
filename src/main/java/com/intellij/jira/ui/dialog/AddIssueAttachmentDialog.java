package com.intellij.jira.ui.dialog;

import com.intellij.jira.tasks.AddIssueAttachmentTask;
import com.intellij.jira.ui.editors.FileChooserFieldEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static java.util.Objects.nonNull;

public class AddIssueAttachmentDialog extends DialogWrapper {

    private FileChooserFieldEditor myFileChooserFieldEditor;
    private Project myProject;
    private String issueKey;

    public AddIssueAttachmentDialog(@NotNull Project project, @NotNull String issueKey) {
        super(project, false);
        this.myProject = project;
        this.issueKey = issueKey;

        setTitle(String.format("Add Attachment to %s", issueKey));
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        this.myFileChooserFieldEditor = new FileChooserFieldEditor(issueKey);

        return FormBuilder.createFormBuilder()
                .addComponent(this.myFileChooserFieldEditor.createPanel())
                .addVerticalGap(5)
                .getPanel();
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        return myFileChooserFieldEditor.validate();
    }

    @Override
    protected void doOKAction() {
        if(nonNull(myProject)){
            new AddIssueAttachmentTask(myProject, issueKey, myFileChooserFieldEditor.getSelectedFile()).queue();
        }

        close(0);
    }
}
