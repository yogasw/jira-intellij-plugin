package com.intellij.jira.data;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class JiraIssuesData {

    private final Project myProject;
    private final IssuesGetter myIssuesGetter;

    public JiraIssuesData(@NotNull Project project) {
        myProject = project;
        myIssuesGetter = new IssuesGetter(project);
    }

    public Project getProject() {
        return myProject;
    }

    public List<JiraIssue> getIssues(String jql) {
        return myIssuesGetter.getIssues(jql);
    }

    @Nullable
    public JiraIssue getIssue(String issueKey) {
        return myIssuesGetter.getIssue(issueKey);
    }

}
