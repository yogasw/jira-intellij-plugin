package com.intellij.jira.data;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class JiraIssuesData implements Disposable {

    private final Project myProject;
    private final IssuesGetter myIssuesGetter;
   // private final JiraIssuesRefresherImpl myRefresher;

    public JiraIssuesData(@NotNull Project project, @NotNull Disposable parent) {
        myProject = project;
        myIssuesGetter = new IssuesGetter(project);


        //myRefresher = new JiraIssuesRefresherImpl(project, progress);
        //Disposer.register(this, myRefresher);

        Disposer.register(parent, this);
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

    @Override
    public void dispose() {

    }
}
