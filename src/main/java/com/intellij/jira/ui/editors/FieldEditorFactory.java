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
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.containers.ContainerUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.intellij.jira.util.JiraGsonUtil.isEmpty;
import static com.intellij.tasks.jira.JiraRepository.GSON;
import static java.util.Objects.isNull;

public class FieldEditorFactory {

    private static final Set<String> TEXT_AREA_FIELDS = ContainerUtil.immutableSet("description", "environment");
    private static final Set<String> TEXT_FIELDS = ContainerUtil.immutableSet("summary");
    private static final Set<String> DATE_FIELDS = ContainerUtil.immutableSet("duedate");
    private static final Set<String> USER_PICKER_FIELDS = ContainerUtil.immutableSet("assignee", "reporter");

    public static FieldEditor create(JiraIssueFieldProperties properties, JiraIssue issue) {

        if (properties.getSchema().isCustomField()) {
            return createCustomFieldEditor(properties, issue);
        }

        String fieldName = properties.getSchema().getSystem();
        if (TEXT_FIELDS.contains(fieldName)) {
            return new TextFieldEditor(issue.getKey(), properties.getName(), issue.getAsString(fieldName), properties.isRequired());
        } else if (TEXT_AREA_FIELDS.contains(fieldName)) {
            return new TextAreaFieldEditor(issue.getKey(), properties.getName(), issue.getAsString(fieldName), properties.isRequired());
        } else if (DATE_FIELDS.contains(fieldName)) {
            return new DateFieldEditor(issue.getKey(), properties.getName(), issue.getAsDate(fieldName), properties.isRequired());
        } else if (USER_PICKER_FIELDS.contains(fieldName)) {
            return new UserSelectFieldEditor(issue.getKey(), properties.getName(), issue.getAsJiraIssueUser(fieldName), properties.isRequired());
        } else if ("timetracking".equals(fieldName)) {
            return new TimeTrackingFieldEditor(issue.getKey(), properties.isRequired());
        } else if ("issuelinks".equals(fieldName)) {
            return new LinkedIssueFieldEditor(issue.getKey(), properties.getName(), properties.isRequired(), issue.getProject().getKey());
        } else if ("issuetype".equals(fieldName)) {
            return new LabelFieldEditor(issue.getKey(), properties.getName(), issue.getIssuetype().getName());
        } else if("worklog".equals(fieldName)) {
            return new LogWorkFieldEditor(issue.getKey(), properties.getName(), issue.getTimetracking(), properties.isRequired());
        }

        return createCustomComboBoxFieldEditor(properties, issue);
    }

    public static CommentFieldEditor createCommentFieldEditor(String issueKey) {
        return new CommentFieldEditor(issueKey);
    }

    private static FieldEditor createCustomComboBoxFieldEditor(JiraIssueFieldProperties properties, JiraIssue issue) {
        JsonArray allowedValues = properties.getAllowedValues();
        if (isNull(allowedValues) || isEmpty(allowedValues)) {
            if (StringUtil.isEmpty(properties.getAutoCompleteUrl())) {
                return new LabelFieldEditor(issue.getKey(), properties.getName());
            }
        }

        List<?> items = new ArrayList<>();
        Object selectedItem = null;
        boolean isArray = properties.getSchema().isArray();
        String type = isArray ? properties.getSchema().getItems() : properties.getSchema().getType();
        String fieldName = properties.getSchema().getSystem();
        if ("priority".equals(type)) {
            items = Arrays.asList(GSON.fromJson(allowedValues, JiraIssuePriority[].class));
            selectedItem = issue.getPriority();
        } else if ("version".equals(type)) {
            items = Arrays.asList(GSON.fromJson(allowedValues, JiraProjectVersion[].class));
            if ("fixVersions".equals(fieldName)) {
                selectedItem = issue.getFixVersions();
            } else if ("versions".equals(fieldName)) {
                selectedItem = issue.getVersions();
            }
        } else if ("resolution".equals(type)) {
            items = Arrays.asList(GSON.fromJson(allowedValues, JiraIssueResolution[].class));
            selectedItem = issue.getResolution();
        } else if ("component".equals(type)) {
            items = Arrays.asList(GSON.fromJson(allowedValues, JiraProjectComponent[].class));
            selectedItem = issue.getComponents();
        }

        if (isArray) {
            return new MultiSelectFieldEditor<>(issue.getKey(), properties.getName(), items, selectedItem, properties.isRequired());
        }

        return new ComboBoxFieldEditor<>(issue.getKey(), properties.getName(), selectedItem, properties.isRequired(), items);
    }

    private static FieldEditor createCustomFieldEditor(JiraIssueFieldProperties properties, JiraIssue issue) {

        boolean isArray = properties.getSchema().isArray();
        String type = isArray ? properties.getSchema().getItems() : properties.getSchema().getType();
        String customFieldType = properties.getSchema().getCustom();

        if (!isArray) {
            if ("string".equals(type)) {
                if ("textarea".equals(customFieldType)) {
                    return new TextAreaFieldEditor(issue.getKey(), properties.getName(), null, properties.isRequired());
                }

                return new TextFieldEditor(issue.getKey(), properties.getName(), null, properties.isRequired());
            } else if ("number".equals(type)) {
                return new NumberFieldEditor(issue.getKey(), properties.getName(), null, properties.isRequired());
            } else if ("date".equals(type)) {
                return new DateFieldEditor(issue.getKey(), properties.getName(), null, properties.isRequired());
            } else if ("datetime".equals(type)) {
                return new DateTimeFieldEditor(issue.getKey(), properties.getName(), null,  properties.isRequired());
            }
        }

        // The field has not values so we have to retrieve them
        JsonArray values = properties.getAllowedValues();
        if (isNull(values) || isEmpty(values)) {
            if ("user".equals(type)) {
                return new UserSelectFieldEditor(issue.getKey(), properties.getName(), null, properties.isRequired(), isArray);
            } else if ("group".equals(type)) {
                return new GroupSelectFieldEditor(issue.getKey(), properties.getName(), null, properties.isRequired(), isArray);
            } else {
                return new LabelFieldEditor(issue.getKey(), properties.getName());
            }
        }

        // The field has values
        if ("project".equals(type)) {
            List<JiraProject> projects = Arrays.asList(GSON.fromJson(values, JiraProject[].class));
            return new ProjectSelectFieldEditor(issue.getKey(), properties.getName(), issue.getCustomfieldValue(properties.getSchema().getCustomId()), properties.isRequired(), isArray, projects);
        } else if ("version".equals(type)) {
            List<JiraProjectVersion> versions = Arrays.asList(GSON.fromJson(values, JiraProjectVersion[].class));
            return new VersionSelectFieldEditor(issue.getKey(), properties.getName(), issue.getCustomfieldValue(properties.getSchema().getCustomId()), properties.isRequired(), isArray, versions);
        }

        List<JiraCustomFieldOption> options = Arrays.asList(GSON.fromJson(values, JiraCustomFieldOption[].class));
        return new OptionSelectFieldEditor(issue.getKey(), properties.getName(), null, properties.isRequired(), isArray, options);

    }

}
