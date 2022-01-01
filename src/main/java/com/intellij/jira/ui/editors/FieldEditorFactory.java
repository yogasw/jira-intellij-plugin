package com.intellij.jira.ui.editors;

import com.google.gson.JsonArray;
import com.intellij.jira.rest.model.JiraCustomFieldOption;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.JiraIssueFieldProperties;
import com.intellij.jira.rest.model.JiraIssuePriority;
import com.intellij.jira.rest.model.JiraIssueResolution;
import com.intellij.jira.rest.model.JiraProject;
import com.intellij.jira.rest.model.JiraProjectComponent;
import com.intellij.jira.rest.model.JiraProjectVersion;
import com.intellij.jira.util.JiraGsonUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.containers.ContainerUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.intellij.jira.util.JiraGsonUtil.isEmpty;
import static com.intellij.jira.util.JiraIssueField.ASSIGNEE;
import static com.intellij.jira.util.JiraIssueField.COMPONENT;
import static com.intellij.jira.util.JiraIssueField.DESCRIPTION;
import static com.intellij.jira.util.JiraIssueField.DUEDATE;
import static com.intellij.jira.util.JiraIssueField.ENVIRONMENT;
import static com.intellij.jira.util.JiraIssueField.FIX_VERSIONS;
import static com.intellij.jira.util.JiraIssueField.ISSUE_LINKS;
import static com.intellij.jira.util.JiraIssueField.ISSUE_TYPE;
import static com.intellij.jira.util.JiraIssueField.PRIORITY;
import static com.intellij.jira.util.JiraIssueField.PROJECT;
import static com.intellij.jira.util.JiraIssueField.REPORTER;
import static com.intellij.jira.util.JiraIssueField.RESOLUTION;
import static com.intellij.jira.util.JiraIssueField.SUMMARY;
import static com.intellij.jira.util.JiraIssueField.TIME_TRACKING;
import static com.intellij.jira.util.JiraIssueField.VERSION;
import static com.intellij.jira.util.JiraIssueField.VERSIONS;
import static com.intellij.jira.util.JiraIssueField.WORKLOG;
import static java.util.Objects.isNull;

public class FieldEditorFactory {

    private static final Set<String> TEXT_AREA_FIELDS = ContainerUtil.immutableSet(DESCRIPTION, ENVIRONMENT);
    private static final Set<String> TEXT_FIELDS = ContainerUtil.immutableSet(SUMMARY);
    private static final Set<String> DATE_FIELDS = ContainerUtil.immutableSet(DUEDATE);
    private static final Set<String> USER_PICKER_FIELDS = ContainerUtil.immutableSet(ASSIGNEE, REPORTER);

    private FieldEditorFactory() { }

    public static FieldEditor create(JiraIssueFieldProperties properties, JiraIssue issue) {

        if (properties.getSchema().isCustomField()) {
            return createCustomFieldEditor(properties, issue);
        }

        String fieldName = properties.getSchema().getSystem();
        if (TEXT_FIELDS.contains(fieldName)) {
            return new TextFieldEditor(properties.getName(), issue.getAsString(fieldName), properties.isRequired());
        } else if (TEXT_AREA_FIELDS.contains(fieldName)) {
            return new TextAreaFieldEditor(properties.getName(), issue.getAsString(fieldName), properties.isRequired());
        } else if (DATE_FIELDS.contains(fieldName)) {
            return new DateFieldEditor(properties.getName(), issue.getAsDate(fieldName), properties.isRequired());
        } else if (USER_PICKER_FIELDS.contains(fieldName)) {
            return new UserSelectFieldEditor(properties.getName(), issue.getAsJiraIssueUser(fieldName), properties.isRequired());
        } else if (TIME_TRACKING.equals(fieldName)) {
            return new TimeTrackingFieldEditor(properties.isRequired());
        } else if (ISSUE_LINKS.equals(fieldName)) {
            return new LinkedIssueFieldEditor(properties.getName(), properties.isRequired());
        } else if (ISSUE_TYPE.equals(fieldName)) {
            return new LabelFieldEditor(properties.getName(), issue.getIssuetype().getName());
        } else if(WORKLOG.equals(fieldName)) {
            return new LogWorkFieldEditor(properties.getName(), issue.getTimetracking(), properties.isRequired());
        }

        return createCustomComboBoxFieldEditor(properties, issue);
    }

    public static CommentFieldEditor createCommentFieldEditor() {
        return new CommentFieldEditor();
    }

    private static FieldEditor createCustomComboBoxFieldEditor(JiraIssueFieldProperties properties, JiraIssue issue) {
        JsonArray allowedValues = properties.getAllowedValues();
        if (isNull(allowedValues) || isEmpty(allowedValues)) {
            if (StringUtil.isEmpty(properties.getAutoCompleteUrl())) {
                return new LabelFieldEditor(properties.getName());
            }
        }

        List<?> items = new ArrayList<>();
        Object selectedItem = null;
        boolean isArray = properties.getSchema().isArray();
        String type = isArray ? properties.getSchema().getItems() : properties.getSchema().getType();
        String fieldName = properties.getSchema().getSystem();
        if (PRIORITY.equals(type)) {
            items = JiraGsonUtil.getAsList(allowedValues, JiraIssuePriority[].class);
            selectedItem = issue.getPriority();
        } else if (VERSION.equals(type)) {
            items = JiraGsonUtil.getAsList(allowedValues, JiraProjectVersion[].class);
            if (FIX_VERSIONS.equals(fieldName)) {
                selectedItem = issue.getFixVersions();
            } else if (VERSIONS.equals(fieldName)) {
                selectedItem = issue.getVersions();
            }
        } else if (RESOLUTION.equals(type)) {
            items = JiraGsonUtil.getAsList(allowedValues, JiraIssueResolution[].class);
            selectedItem = issue.getResolution();
        } else if (COMPONENT.equals(type)) {
            items = JiraGsonUtil.getAsList(allowedValues, JiraProjectComponent[].class);
            selectedItem = issue.getComponents();
        }

        if (isArray) {
            return new MultiSelectFieldEditor<>(properties.getName(), items, selectedItem, properties.isRequired());
        }

        return new ComboBoxFieldEditor<>(properties.getName(), selectedItem, properties.isRequired(), items);
    }

    private static FieldEditor createCustomFieldEditor(JiraIssueFieldProperties properties, JiraIssue issue) {

        boolean isArray = properties.getSchema().isArray();
        String type = isArray ? properties.getSchema().getItems() : properties.getSchema().getType();
        String customFieldType = properties.getSchema().getCustom();

        if (!isArray) {
            if ("string".equals(type)) {
                if ("textarea".equals(customFieldType)) {
                    return new TextAreaFieldEditor(properties.getName(), null, properties.isRequired());
                }

                return new TextFieldEditor(properties.getName(), null, properties.isRequired());
            } else if ("number".equals(type)) {
                return new NumberFieldEditor(properties.getName(), null, properties.isRequired());
            } else if ("date".equals(type)) {
                return new DateFieldEditor(properties.getName(), null, properties.isRequired());
            } else if ("datetime".equals(type)) {
                return new DateTimeFieldEditor(properties.getName(), null,  properties.isRequired());
            }
        }

        // The field has not values so we have to retrieve them
        JsonArray values = properties.getAllowedValues();
        if (isNull(values) || isEmpty(values)) {
            if ("user".equals(type)) {
                return new UserSelectFieldEditor(properties.getName(), null, properties.isRequired(), isArray);
            } else if ("group".equals(type)) {
                return new GroupSelectFieldEditor(properties.getName(), null, properties.isRequired(), isArray);
            } else {
                return new LabelFieldEditor(properties.getName());
            }
        }

        // The field has values
        if (PROJECT.equals(type)) {
            List<JiraProject> projects = JiraGsonUtil.getAsList(values, JiraProject[].class);
            return new ProjectSelectFieldEditor(properties.getName(), issue.getCustomfieldValue(properties.getSchema().getCustomId()), properties.isRequired(), isArray, projects);
        } else if (VERSION.equals(type)) {
            List<JiraProjectVersion> versions = JiraGsonUtil.getAsList(values, JiraProjectVersion[].class);
            return new VersionSelectFieldEditor(properties.getName(), issue.getCustomfieldValue(properties.getSchema().getCustomId()), properties.isRequired(), isArray, versions);
        }

        List<JiraCustomFieldOption> options = JiraGsonUtil.getAsList(values, JiraCustomFieldOption[].class);
        return new OptionSelectFieldEditor(properties.getName(), null, properties.isRequired(), isArray, options);
    }

}
