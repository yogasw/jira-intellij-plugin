package com.intellij.jira.converter;

import com.intellij.conversion.CannotConvertException;
import com.intellij.conversion.impl.ConversionContextImpl;
import com.intellij.conversion.impl.WorkspaceSettingsImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Objects;

public class JiraWorkspaceSettings extends WorkspaceSettingsImpl {

    private JiraWorkspaceSettings(Path workspaceFile, ConversionContextImpl context) throws CannotConvertException {
        super(workspaceFile, context);
    }

    public static JiraWorkspaceSettings of(Project project) throws CannotConvertException {
        VirtualFile workspaceFile = project.getWorkspaceFile();
        if (Objects.isNull(workspaceFile)){
            throw new CannotConvertException("Cannot find workspace file!!");
        }

        Path workspacePath = FileSystems.getDefault().getPath(workspaceFile.getPath());
        return new JiraWorkspaceSettings(workspacePath, new ConversionContextImpl(workspacePath));
    }

}
