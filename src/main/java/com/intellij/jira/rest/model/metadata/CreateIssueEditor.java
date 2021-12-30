package com.intellij.jira.rest.model.metadata;

import com.google.gson.JsonObject;
import com.intellij.jira.rest.model.JiraIssueType;
import com.intellij.jira.rest.model.JiraProject;
import com.intellij.jira.ui.editors.ComboBoxFieldEditor;
import com.intellij.jira.ui.editors.Editor;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CreateIssueEditor implements Editor {

    private final Map<String, List<JiraIssueTypeIssueCreateMetadata>> myIssueCreateMeta;

    private ProjectComboBox myProjectCombo;
    private IssueTypeComboBox myIssueTypeCombo;
    private FieldsEditor myFieldsEditor;

    public CreateIssueEditor(JiraIssueCreateMetadata issueCreateMeta) {
        myIssueCreateMeta = issueCreateMeta.getProjects().stream()
                .collect(Collectors.toMap(JiraProject::getKey, JiraProjectIssueCreateMetadata::getIssuetypes));
    }

    @Override
    public JComponent createPanel() {
        List<String> projectKeys = new ArrayList<>(myIssueCreateMeta.keySet());
        String firstProjectKey = ContainerUtil.getFirstItem(projectKeys);
        myProjectCombo = new ProjectComboBox(firstProjectKey, projectKeys);

        List<JiraIssueTypeIssueCreateMetadata> issueTypeIssueCreateMetadata = myIssueCreateMeta.get(firstProjectKey);
        JiraIssueTypeIssueCreateMetadata firstIssueType = ContainerUtil.getFirstItem(issueTypeIssueCreateMetadata);
        List<String> issueTypes = issueTypeIssueCreateMetadata.stream().map(JiraIssueType::getName).collect(Collectors.toList());
        myIssueTypeCombo = new IssueTypeComboBox(issueTypes);

        myFieldsEditor = new FieldsEditor(firstIssueType.getFields());

        return FormBuilder.createFormBuilder()
                .addComponent(myProjectCombo.createPanel())
                .addComponent(myIssueTypeCombo.createPanel())
                .addComponent(myFieldsEditor.createPanel())
                .getPanel();
    }

    @Override
    public @Nullable ValidationInfo validate() {
        return null;
    }

    private class ProjectComboBox extends ComboBoxFieldEditor<String> {

        public ProjectComboBox(Object fieldValue, List<String> items) {
            super(null, "Project", fieldValue, true, items);
        }
    }

    private class IssueTypeComboBox extends ComboBoxFieldEditor<String> {

        public IssueTypeComboBox(List<String> items) {
            super(null, "Issue Type", null, true, items);
        }
    }

    private class FieldsEditor implements Editor {

        private final JsonObject myFields;

        private FieldsEditor(JsonObject fields) {
            myFields = fields;
        }

        @Override
        public JComponent createPanel() {
            // TODO: build panel
            return FormBuilder.createFormBuilder()
                    .getPanel();
        }

        @Override
        public @Nullable ValidationInfo validate() {
            return null;
        }
    }

}
