package com.intellij.jira.ui.editors;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.intellij.jira.rest.model.JiraProject;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.intellij.jira.util.JiraGsonUtil.createArrayObject;
import static com.intellij.jira.util.JiraGsonUtil.createObject;
import static com.intellij.openapi.util.text.StringUtil.isEmpty;
import static com.intellij.openapi.util.text.StringUtil.trim;
import static com.intellij.tasks.jira.JiraRepository.GSON;
import static com.intellij.util.containers.ContainerUtil.getFirstItem;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class ProjectSelectFieldEditor extends DataSelectFieldEditor<JiraProject> {

    public ProjectSelectFieldEditor(String issueKey, String fieldName, Object fieldValue, boolean required, boolean isMultiSelect, List<JiraProject> items) {
        super(fieldName, issueKey, fieldValue, required, isMultiSelect, items);
        myButtonAction = new ProjectPickerDialogAction();
    }

    @Override
    protected void initSelectedItems() {
        JsonElement element = GSON.toJsonTree(fieldValue);
        if (element.isJsonArray()) {
            selectedItems = Arrays.asList(GSON.fromJson(element, JiraProject[].class));
        } else if (element.isJsonObject()) {
            selectedItems = Collections.singletonList(GSON.fromJson(element, JiraProject.class));
        }
    }

    @Override
    public JComponent createPanel() {
        if (Objects.nonNull(selectedItems)) {
            myTextField.setText(selectedItems.stream().map(JiraProject::getKey).collect(joining(", ")));
        }

        return super.createPanel();
    }

    @Override
    public JsonElement getJsonValue() {
        if(isEmpty(trim(myTextField.getText()))){
            return JsonNull.INSTANCE;
        }

        List<String> values = selectedItems.stream().map(JiraProject::getKey).collect(toList());
        if(isMultiSelect){
            return createArrayObject("key", values);
        }

        return createObject("key", getFirstItem(values));
    }

    @Override
    public JiraProject getFieldValue() {
        return null;
    }


    class ProjectPickerDialogAction extends PickerDialogAction{

        ProjectPickerDialogAction() { }

        @Override
        public void actionPerformed(AnActionEvent e) {
            Project project = e.getProject();
            if(nonNull(project)){
                new ProjectPickerDialog(project).show();
            }
        }
    }



    class ProjectPickerDialog extends PickerDialog<JiraProject>{

        ProjectPickerDialog(@Nullable Project project) {
            super(project, "Projects", myItems, selectedItems);
        }

        @Override
        protected void doOKAction() {
            selectedItems = myList.getSelectedValuesList();
            myTextField.setText(selectedItems.isEmpty() ? "" :  selectedItems.stream().map(JiraProject::getKey).collect(joining(", ")));


            super.doOKAction();
        }
    }

}
