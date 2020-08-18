package com.intellij.jira.activity;

import com.intellij.conversion.CannotConvertException;
import com.intellij.conversion.ConversionProcessor;
import com.intellij.conversion.WorkspaceSettings;
import com.intellij.jira.converter.JiraProjectConverter;
import com.intellij.jira.converter.JiraWorkspaceSettings;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class UpdateWorkspaceActivity implements StartupActivity, DumbAware {

    @Override
    public void runActivity(@NotNull Project project) {
        JiraProjectConverter jiraProjectConverter = ServiceManager.getService(project, JiraProjectConverter.class);
        if (Objects.isNull(jiraProjectConverter)) return;

        ConversionProcessor<WorkspaceSettings> workspaceFileConverter = jiraProjectConverter.createWorkspaceFileConverter();
        try {
            WorkspaceSettings workspaceSettings = JiraWorkspaceSettings.of(project);
            if (workspaceFileConverter.isConversionNeeded(workspaceSettings)) {
               workspaceFileConverter.process(workspaceSettings);
            }

        } catch (CannotConvertException e) {
            e.printStackTrace();
        }

    }
}
