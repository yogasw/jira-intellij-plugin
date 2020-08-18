package com.intellij.jira.converter;

import com.intellij.conversion.ConversionProcessor;
import com.intellij.conversion.ProjectConverter;
import com.intellij.conversion.WorkspaceSettings;
import org.jetbrains.annotations.Nullable;

public class JiraProjectConverter extends ProjectConverter {

    private static final ConversionProcessor<WorkspaceSettings> WORKSPACE_CONVERSION = new JiraServerConversionProcessor();

    @Nullable
    @Override
    public ConversionProcessor<WorkspaceSettings> createWorkspaceFileConverter() {
        return WORKSPACE_CONVERSION;
    }
}
