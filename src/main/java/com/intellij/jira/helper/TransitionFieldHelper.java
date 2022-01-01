package com.intellij.jira.helper;

import com.google.gson.JsonElement;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.JiraIssueFieldProperties;
import com.intellij.jira.ui.editors.FieldEditor;
import com.intellij.jira.ui.editors.FieldEditorFactory;
import com.intellij.openapi.ui.ValidationInfo;

import javax.swing.*;

import static com.intellij.jira.ui.editors.FieldEditorFactory.createCommentFieldEditor;
import static com.intellij.jira.util.JiraIssueField.COMMENT;

public final class TransitionFieldHelper {

    public static FieldEditorInfo createFieldEditorInfo(JiraIssueFieldProperties properties, JiraIssue issue){
        return new FieldEditorInfo(properties, issue);
    }

    public static FieldEditorInfo createFieldEditorInfo(String jsonFieldName, FieldEditor fieldEditor){
        return new FieldEditorInfo(jsonFieldName, fieldEditor);
    }

    public static FieldEditorInfo createCommentFieldEditorInfo(){
        return new FieldEditorInfo(COMMENT, createCommentFieldEditor());
    }

    public static class FieldEditorInfo {

        private FieldEditor editor;
        private String name;

        private FieldEditorInfo(JiraIssueFieldProperties properties, JiraIssue issue) {
            this(properties.getSchema().getFieldName(), FieldEditorFactory.create(properties, issue));
        }

        private FieldEditorInfo(String jsonFieldName, FieldEditor fieldEditor) {
            this.name = jsonFieldName;
            this.editor = fieldEditor;
        }

        public JComponent getPanel(){
            return editor.createPanel();
        }

        public JsonElement getJsonValue(){
            return editor.getJsonValue();
        }

        public String getName(){
            return name;
        }

        public boolean isRequired(){
            return editor.isRequired();
        }

        public ValidationInfo validateField(){
            return editor.validate();
        }

    }

}
