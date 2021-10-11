package com.intellij.jira.data;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.intellij.jira.components.JQLSearcherManager;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.server.JiraServerManager;
import com.intellij.jira.util.result.Result;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class JiraIssuesData {

    private final Project myProject;
    private final JiraRestApi myRestApi;
    private final Cache<String, JiraIssue> myChache;
    private JQLSearcher myJqlSearcher;

    public JiraIssuesData(@NotNull Project project) {
        this(project, null);

    }

    public JiraIssuesData(@NotNull Project project, @Nullable JQLSearcher jqlSearcher) {
        this.myProject = project;
        this.myJqlSearcher = jqlSearcher;
        this.myChache = Caffeine.newBuilder().maximumSize(500).expireAfterWrite(60, TimeUnit.SECONDS).build();
        this.myRestApi = JiraServerManager.getInstance(project).getJiraRestApi();
    }

    public Project getProject() {
        return myProject;
    }

    public List<JiraIssue> getIssues() {
        JQLSearcher searcher = myJqlSearcher != null ? myJqlSearcher.clone() : null;
        if (searcher == null) {
            searcher = JQLSearcherManager.getInstance().getSelectedSearcher(myProject);
        }

        return myRestApi.getIssues(searcher.getJql());
    }

    @Nullable
    public JiraIssue getIssue(String issueKey) {
        JiraIssue issue = getIssueFromCache(issueKey);
        if (Objects.isNull(issue)) {
            issue = (JiraIssue) getIssueFromServer(issueKey).get();
        }

        return issue;
    }

    private Result getIssueFromServer(String issueKey) {
        return myRestApi.getIssue(issueKey);
    }

    private JiraIssue getIssueFromCache(String issueKey) {
        return myChache.getIfPresent(issueKey);
    }

}
