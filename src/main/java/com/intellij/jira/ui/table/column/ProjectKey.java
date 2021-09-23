package com.intellij.jira.ui.table.column;

import com.intellij.jira.rest.model.JiraIssue;
import org.jetbrains.annotations.Nullable;

public class ProjectKey extends StringColumn<JiraIssue> {

    public static final ProjectKey INSTANCE = new ProjectKey();

    private ProjectKey() {
        super("Dynamic.ProjectKey", "Project Key", false, true);
    }

    @Override
    public @Nullable String valueOf(JiraIssue issue) {
        return issue.getProject().getKey();
    }
}
