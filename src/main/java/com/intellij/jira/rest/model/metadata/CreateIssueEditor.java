package com.intellij.jira.rest.model.metadata;

import com.google.gson.JsonObject;
import com.intellij.jira.helper.TransitionFieldHelper;
import com.intellij.jira.rest.model.JiraIssueFieldProperties;
import com.intellij.jira.rest.model.JiraIssueType;
import com.intellij.jira.rest.model.JiraProject;
import com.intellij.jira.ui.editors.ComboBoxFieldEditor;
import com.intellij.jira.ui.editors.Editor;
import com.intellij.jira.ui.editors.FieldEditor;
import com.intellij.jira.ui.editors.FieldEditorFactory;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.tasks.jira.JiraRepository;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.components.BorderLayoutPanel;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

public class CreateIssueEditor implements Editor {

    private final Map<String, List<JiraIssueTypeIssueCreateMetadata>> myIssueCreateMeta;

    private ProjectComboBox myProjectCombo;
    private IssueTypeComboBox myIssueTypeCombo;
    private FieldsEditor myFieldsEditor;
    private BorderLayoutPanel myFieldsPanel;

    public CreateIssueEditor(JiraIssueCreateMetadata issueCreateMeta) {
        myIssueCreateMeta = issueCreateMeta.getProjects().stream()
                .collect(Collectors.toMap(JiraProject::getKey,
                        JiraProjectIssueCreateMetadata::getIssuetypes,
                        (k, v) -> k,
                        LinkedHashMap::new));
    }

    @Override
    public JComponent createPanel() {
        myFieldsPanel = new BorderLayoutPanel();

        List<String> projectKeys = new ArrayList<>(myIssueCreateMeta.keySet());
        myProjectCombo = new ProjectComboBox(projectKeys);
        myProjectCombo.addActionListener(e -> updateIssueTypesCombo());
        myProjectCombo.setSelectedValue(ContainerUtil.getFirstItem(projectKeys));

        JPanel createIssuePanel = FormBuilder.createFormBuilder()
                .addComponent(myProjectCombo.createPanel())
                .addComponent(myIssueTypeCombo.createPanel())
                .addSeparator()
                .addComponent(myFieldsPanel)
                .getPanel();

        BorderLayoutPanel panel = new BorderLayoutPanel();
        panel.setMinimumSize(JBUI.size(300, 450));
        panel.setPreferredSize(JBUI.size(400, 500));
        panel.addToCenter(ScrollPaneFactory.createScrollPane(createIssuePanel, true));

        return panel;
    }

    public String getProjectKey() {
        return myProjectCombo.getSelectedValue();
    }

    @Override
    public @Nullable ValidationInfo validate() {
        return myFieldsEditor.validate();
    }

    private void updateIssueTypesCombo() {
        String selectedProjectKey = myProjectCombo.getSelectedValue();

        List<JiraIssueTypeIssueCreateMetadata> issueTypeIssueCreateMetadata = myIssueCreateMeta.get(selectedProjectKey);
        List<String> issueTypes = issueTypeIssueCreateMetadata.stream().map(JiraIssueType::getName).collect(Collectors.toList());
        myIssueTypeCombo = new IssueTypeComboBox(issueTypes);
        myIssueTypeCombo.addActionListener(e -> updateFieldsInForm());
        myIssueTypeCombo.setSelectedValue(ContainerUtil.getFirstItem(issueTypes));
    }

    private void updateFieldsInForm() {
        myFieldsPanel.removeAll();

        String selectedProjectKey = myProjectCombo.getSelectedValue();

        List<JiraIssueTypeIssueCreateMetadata> issueTypeIssueCreateMetadata = myIssueCreateMeta.get(selectedProjectKey);
        JiraIssueTypeIssueCreateMetadata firstIssueType = ContainerUtil.getFirstItem(issueTypeIssueCreateMetadata);

        myFieldsEditor = new FieldsEditor(firstIssueType.getFields());

        myFieldsPanel.addToCenter(myFieldsEditor.createPanel());

        myFieldsPanel.revalidate();
        myFieldsPanel.repaint();
    }


    private class ProjectComboBox extends ComboBoxFieldEditor<String> {

        public ProjectComboBox(List<String> items) {
            super("Project", null, true, items);
        }
    }

    private class IssueTypeComboBox extends ComboBoxFieldEditor<String> {

        public IssueTypeComboBox(List<String> items) {
            super("Issue Type", null, true, items);
        }
    }

    private class FieldsEditor implements Editor {

        private final JsonObject myFields;
        private final Map<String, TransitionFieldHelper.FieldEditorInfo> myCreateIssueFields = new HashMap<>();

        private FieldsEditor(JsonObject fields) {
            myFields = fields;
        }

        @Override
        public JComponent createPanel() {
            FormBuilder formBuilder = FormBuilder.createFormBuilder().setVerticalGap(10);

            List<JiraIssueFieldProperties> fields = myFields.entrySet().stream()
                    .filter(field -> !"project".equals(field.getKey()) && !"issuetype".equals(field.getKey()))
                    .map(entry -> JiraRepository.GSON.fromJson(entry.getValue(), JiraIssueFieldProperties.class))
                    .collect(Collectors.toList());

            fields.forEach(fieldProperties -> {
                FieldEditor fieldEditor = FieldEditorFactory.create(fieldProperties);
                TransitionFieldHelper.FieldEditorInfo info = TransitionFieldHelper.createFieldEditorInfo(fieldProperties.getName(), fieldEditor);
                myCreateIssueFields.put(info.getName(), info);

                formBuilder.addComponent(info.getPanel());
            });

            return formBuilder
                    .getPanel();
        }

        @Override
        public @Nullable ValidationInfo validate() {
            for(TransitionFieldHelper.FieldEditorInfo info : myCreateIssueFields.values()){
                ValidationInfo fieldValidation = info.validateField();
                if(nonNull(fieldValidation)){
                    return fieldValidation;
                }
            }

            return null;
        }
    }

}
