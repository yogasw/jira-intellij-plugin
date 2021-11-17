package com.intellij.jira.data;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.server.JiraServerManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class IssuesGetter implements Getter<JiraIssue> {

    private final Project myProject;
    private final Cache<String, JiraIssue> myCache;

    public IssuesGetter(@NotNull Project project) {
        myProject = project;
        myCache = Caffeine.newBuilder().maximumSize(500).expireAfterWrite(1, TimeUnit.MINUTES).build();
    }

    @Override
    public JiraIssue getIssue(String issueKey) {
        JiraIssue issue = getIssueFromCache(issueKey);
        if (Objects.isNull(issue)) {
            issue = getIssueFromServer(issueKey);
        }

        return issue;
    }

    @Override
    public List<JiraIssue> getIssues(String jql) {
        List<JiraIssue> issues = getJiraServerManager().getJiraRestApi(myProject).getIssues(jql);
        for (JiraIssue issue : issues) {
            putInCache(issue);
        }

        return issues;
    }

    @NotNull
    private JiraServerManager getJiraServerManager() {
        return JiraServerManager.getInstance();
    }

    public JiraIssue getIssueFromServer(String issueKey) {
        JiraIssue issue = (JiraIssue) getJiraServerManager().getJiraRestApi(myProject).getIssue(issueKey).get();
        putInCache(issue);

        return issue;
    }

    @Nullable
    private JiraIssue getIssueFromCache(String issueKey) {
        return myCache.getIfPresent(issueKey);
    }

    private void putInCache(JiraIssue issue) {
        if (issue != null) {
            myCache.put(issue.getKey(), issue);
        }
    }
}
