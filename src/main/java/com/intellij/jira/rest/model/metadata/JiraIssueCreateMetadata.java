package com.intellij.jira.rest.model.metadata;

import java.util.List;

public class JiraIssueCreateMetadata {

    private List<JiraProjectIssueCreateMetadata> projects;

    public List<JiraProjectIssueCreateMetadata> getProjects() {
        return projects;
    }
}
