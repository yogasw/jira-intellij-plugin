package com.intellij.jira.ui.editors;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.intellij.jira.rest.model.JiraProjectVersion;
import com.intellij.jira.util.JiraGsonUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.intellij.jira.util.JiraGsonUtil.createArrayObject;
import static com.intellij.jira.util.JiraGsonUtil.createObject;
import static com.intellij.jira.util.JiraIssueField.KEY;
import static com.intellij.openapi.util.text.StringUtil.isEmpty;
import static com.intellij.openapi.util.text.StringUtil.trim;
import static com.intellij.tasks.jira.JiraRepository.GSON;
import static com.intellij.util.containers.ContainerUtil.getFirstItem;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class VersionSelectFieldEditor extends DataSelectFieldEditor<JiraProjectVersion> {

    public VersionSelectFieldEditor(String issueKey, String fieldName, Object fieldValue, boolean required, boolean isMultiSelect, List<JiraProjectVersion> items) {
        super(issueKey, fieldName, fieldValue, required, isMultiSelect, items);
        myButtonAction = new VersionPickerDialogAction();
    }

    @Override
    protected void initSelectedItems() {
        JsonElement element = GSON.toJsonTree(fieldValue);
        if (element.isJsonArray()) {
            mySelectedItems = JiraGsonUtil.getAsList(element, JiraProjectVersion[].class);
        } else if (element.isJsonObject()) {
            mySelectedItems = Collections.singletonList(GSON.fromJson(element, JiraProjectVersion.class));
        }
    }

    @Override
    public JComponent createPanel() {
        if (Objects.nonNull(mySelectedItems)) {
            myTextField.setText(mySelectedItems.stream().map(JiraProjectVersion::getName).collect(joining(", ")));
        }

        return super.createPanel();
    }

    @Override
    public JsonElement getJsonValue() {
        if(isEmpty(trim(myTextField.getText()))){
            return JsonNull.INSTANCE;
        }

        List<String> values = mySelectedItems.stream().map(JiraProjectVersion::getName).collect(toList());
        if(myIsMultiSelect){
            return createArrayObject(KEY, values);
        }

        return createObject(KEY, getFirstItem(values));
    }

    @Override
    public JiraProjectVersion getFieldValue() {
        return null;
    }


    class VersionPickerDialogAction extends PickerDialogAction{

        public VersionPickerDialogAction() { }

        @Override
        public void actionPerformed(AnActionEvent e) {
            Project project = e.getProject();
            if(nonNull(project)){
                new VersionPickerDialog(project).show();
            }
        }
    }

    class VersionPickerDialog extends PickerDialog<JiraProjectVersion>{

        VersionPickerDialog(@Nullable Project project) {
            super(project, "Versions", myItems, mySelectedItems);
        }

        @Override
        protected void doOKAction() {
            mySelectedItems = myList.getSelectedValuesList();
            myTextField.setText(mySelectedItems.isEmpty() ? "" :  mySelectedItems.stream().map(JiraProjectVersion::getName).collect(joining(", ")));

            super.doOKAction();
        }
    }

}
