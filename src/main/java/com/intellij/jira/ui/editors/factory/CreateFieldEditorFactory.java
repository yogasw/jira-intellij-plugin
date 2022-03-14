package com.intellij.jira.ui.editors.factory;

import com.google.gson.JsonArray;
import com.intellij.jira.rest.model.JiraIssueFieldProperties;
import com.intellij.jira.rest.model.JiraIssuePriority;
import com.intellij.jira.rest.model.JiraIssueResolution;
import com.intellij.jira.rest.model.JiraProject;
import com.intellij.jira.rest.model.JiraProjectComponent;
import com.intellij.jira.rest.model.JiraProjectVersion;
import com.intellij.jira.ui.editors.ComboBoxFieldEditor;
import com.intellij.jira.ui.editors.DateFieldEditor;
import com.intellij.jira.ui.editors.FieldEditor;
import com.intellij.jira.ui.editors.LabelFieldEditor;
import com.intellij.jira.ui.editors.LabelsFieldEditor;
import com.intellij.jira.ui.editors.LogWorkFieldEditor;
import com.intellij.jira.ui.editors.MultiSelectFieldEditor;
import com.intellij.jira.ui.editors.ProjectSelectFieldEditor;
import com.intellij.jira.ui.editors.TextAreaFieldEditor;
import com.intellij.jira.ui.editors.TextFieldEditor;
import com.intellij.jira.ui.editors.UserSelectFieldEditor;
import com.intellij.jira.ui.editors.VersionSelectFieldEditor;
import com.intellij.jira.util.JiraGsonUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;

import java.util.ArrayList;
import java.util.List;

import static com.intellij.jira.util.JiraGsonUtil.isEmpty;
import static com.intellij.jira.util.JiraIssueField.COMPONENT;
import static com.intellij.jira.util.JiraIssueField.PRIORITY;
import static com.intellij.jira.util.JiraIssueField.RESOLUTION;
import static com.intellij.jira.util.JiraIssueField.VERSION;
import static java.util.Objects.isNull;

/**
 * Implementation of {@link FieldEditorFactory} to manage the creation of fields
 * for new issues
 */
public class CreateFieldEditorFactory extends UpdateFieldEditorFactory {

    private static final CreateFieldEditorFactory INSTANCE = new CreateFieldEditorFactory();

    private CreateFieldEditorFactory() {
        super(null);
    }

    public static CreateFieldEditorFactory getInstance() {
        return INSTANCE;
    }

    @Override
    protected TextFieldEditor getTextFieldEditor(JiraIssueFieldProperties properties, String fieldName) {
        return new TextFieldEditor(properties.getName(), null, properties.isRequired());
    }

    @Override
    protected TextAreaFieldEditor getTextAreaFieldEditor(JiraIssueFieldProperties properties, String fieldName) {
        return new TextAreaFieldEditor(properties.getName(), null, properties.isRequired());
    }

    @Override
    protected DateFieldEditor getDateFieldEditor(JiraIssueFieldProperties properties, String fieldName) {
        return new DateFieldEditor(properties.getName(), null, properties.isRequired());
    }

    @Override
    protected UserSelectFieldEditor getUserSelectFieldEditor(JiraIssueFieldProperties properties, String fieldName) {
        return new UserSelectFieldEditor(properties.getName(), null, properties.isRequired());
    }

    @Override
    protected LogWorkFieldEditor getLogWorkFieldEditor(JiraIssueFieldProperties properties) {
        return new LogWorkFieldEditor(properties.getName(), null, properties.isRequired());
    }

    protected FieldEditor createCustomComboBoxFieldEditor(Project project, JiraIssueFieldProperties properties) {
        JsonArray allowedValues = properties.getAllowedValues();
        if (isNull(allowedValues) || isEmpty(allowedValues)) {
            if (StringUtil.isEmpty(properties.getAutoCompleteUrl())) {
                return new LabelFieldEditor(properties.getName());
            } else {
                return new LabelsFieldEditor(project, properties.getName(), properties.getAutoCompleteUrl());
            }
        }

        List<?> items = new ArrayList<>();
        boolean isArray = properties.getSchema().isArray();
        String type = isArray ? properties.getSchema().getItems() : properties.getSchema().getType();
        if (PRIORITY.equals(type)) {
            items = JiraGsonUtil.getAsList(allowedValues, JiraIssuePriority[].class);
        } else if (VERSION.equals(type)) {
            items = JiraGsonUtil.getAsList(allowedValues, JiraProjectVersion[].class);
        } else if (RESOLUTION.equals(type)) {
            items = JiraGsonUtil.getAsList(allowedValues, JiraIssueResolution[].class);
        } else if (COMPONENT.equals(type)) {
            items = JiraGsonUtil.getAsList(allowedValues, JiraProjectComponent[].class);
        }

        if (isArray) {
            return new MultiSelectFieldEditor<>(properties.getName(), items, null, properties.isRequired());
        }

        return new ComboBoxFieldEditor<>(properties.getName(), null, properties.isRequired(), items);
    }


    @Override
    protected ProjectSelectFieldEditor getProjectSelectFieldEditor(JiraIssueFieldProperties properties, boolean isArray, List<JiraProject> projects) {
        return new ProjectSelectFieldEditor(properties.getName(), null, properties.isRequired(), isArray, projects);
    }

    @Override
    protected VersionSelectFieldEditor getVersionSelectFieldEditor(JiraIssueFieldProperties properties, boolean isArray, List<JiraProjectVersion> versions) {
        return new VersionSelectFieldEditor(properties.getName(), null, properties.isRequired(), isArray, versions);
    }

}
