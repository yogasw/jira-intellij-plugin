package com.intellij.jira.ui.editors;

import com.intellij.jira.JiraDataKeys;
import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.ui.components.JBLabel;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractFieldEditor<T> implements FieldEditor, DataProvider {

    protected String issueKey;
    protected MyLabel myLabel;
    protected Object fieldValue;
    private boolean required;

    public AbstractFieldEditor(String issueKey, String fieldName, Object fieldValue) {
        this(issueKey, fieldName, fieldValue, false);
    }

    public AbstractFieldEditor(String issueKey, String fieldName, Object fieldValue, boolean required) {
        this.issueKey = issueKey;
        this.myLabel = new MyLabel(fieldName, required);
        this.fieldValue = fieldValue;
        this.required = required;
    }

    @Override
    public @Nullable Object getData(@NotNull @NonNls String dataId) {
        if (JiraDataKeys.ISSUE_KEY.is(dataId)) {
            return issueKey;
        }

        return null;
    }

    public abstract T getFieldValue();

    @Override
    public boolean isRequired() {
        return required;
    }

    class MyLabel extends JBLabel{
        private String myLabelText;

        public MyLabel(String myLabelText, boolean required) {
            super();
            this.myLabelText = myLabelText;
            setText(myLabelText + (required ? " *" : ""));
        }


        public String getMyLabelText() {
            return myLabelText;
        }

    }

}
