package com.intellij.jira.components;

import com.intellij.jira.rest.model.JiraProject;
import com.intellij.jira.rest.model.metadata.CreateIssueEditor;
import com.intellij.jira.rest.model.metadata.JiraIssueTypeIssueCreateMetadata;
import com.intellij.jira.rest.model.metadata.JiraProjectIssueCreateMetadata;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class CreateIssueMetaProvider {

    private final JiraRestApi myJiraRestApi;
    private Map<JiraProject, List<JiraIssueTypeIssueCreateMetadata>> myIssueCreateMeta = new LinkedHashMap<>();

    public CreateIssueMetaProvider(@NotNull JiraRestApi jiraRestApi) {
        myJiraRestApi = Objects.requireNonNull(jiraRestApi, "jiraRestApi must not be null!!");
    }

    public List<JiraIssueTypeIssueCreateMetadata> getIssueTypes(JiraProject project) {
        return myIssueCreateMeta.get(project);
    }

    public @NotNull Set<JiraProject> getCachedValues() {
        return myIssueCreateMeta.keySet();
    }

    public void updateValuesAsynchronously(CreateIssueEditor createIssueEditor) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {

            myIssueCreateMeta = myJiraRestApi.getIssueCreateMeta().getProjects().stream()
                    .collect(Collectors.toMap(k -> k,
                            JiraProjectIssueCreateMetadata::getIssuetypes,
                            (k, v) -> k,
                            LinkedHashMap::new));

            UIUtil.invokeLaterIfNeeded(() -> createIssueEditor.onUpdateValues(myIssueCreateMeta.keySet()));

        });
    }

}
