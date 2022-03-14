package com.intellij.jira.ui.editors.factory;

import com.intellij.jira.rest.model.JiraIssueFieldProperties;
import com.intellij.jira.ui.editors.FieldEditor;
import com.intellij.openapi.project.Project;

public interface FieldEditorFactory {

   FieldEditor create(Project project, JiraIssueFieldProperties properties);

}
