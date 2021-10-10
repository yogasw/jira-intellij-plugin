package com.intellij.jira.data;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class JiraIssuesData {

    private final Project myProject;

    public JiraIssuesData(@NotNull Project myProject) {
        this.myProject = myProject;
    }

    public Project getProject() {
        return myProject;
    }
}
