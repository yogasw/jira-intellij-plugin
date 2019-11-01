package com.intellij.jira.ui.dialog;

import com.intellij.jira.tasks.DeleteWorklogTask;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

import static java.util.Objects.nonNull;

public class DeleteWorklogDialog extends DialogWrapper {

    private Project project;
    private String issueKey;
    private String commentId;

    public DeleteWorklogDialog(Project project, String issueKey, String commentId) {
        super(project, false);
        this.project = project;
        this.issueKey = issueKey;
        this.commentId = commentId;
        init();
    }

    @Override
    protected void init() {
        setTitle(String.format("Delete Work Log: %s", issueKey));
        super.init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JBPanel panel = new JBPanel(new BorderLayout());
        JBLabel label = new JBLabel("You are going to delete this work log. This cannot be undone.");
        panel.add(label, BorderLayout.CENTER);

        return panel;
    }


    @NotNull
    @Override
    protected Action[] createActions() {
        return new Action[]{new DeleteWorklogExecuteAction(), myCancelAction};
    }


    @Override
    protected void doOKAction() {
        if(nonNull(project)){
            new DeleteWorklogTask(project, issueKey, commentId).queue();
        }
        close(0);
    }

    private class DeleteWorklogExecuteAction extends OkAction { }

}
