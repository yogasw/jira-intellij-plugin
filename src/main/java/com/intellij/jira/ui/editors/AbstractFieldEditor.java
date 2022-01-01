package com.intellij.jira.ui.editors;

import com.intellij.ui.components.JBLabel;

public abstract class AbstractFieldEditor<T> implements FieldEditor {

    protected MyLabel myLabel;
    protected Object myFieldValue;
    private final boolean myIsRequired;

    public AbstractFieldEditor(String fieldName, Object fieldValue) {
        this(fieldName, fieldValue, false);
    }

    public AbstractFieldEditor(String fieldName, Object fieldValue, boolean required) {
        myLabel = new MyLabel(fieldName, required);
        myFieldValue = fieldValue;
        myIsRequired = required;
    }

    public abstract T getFieldValue();

    @Override
    public boolean isRequired() {
        return myIsRequired;
    }

    class MyLabel extends JBLabel{
        private String myLabelText;

        public MyLabel(String labelText, boolean required) {
            super();
            myLabelText = labelText;
            setText(myLabelText + (required ? " *" : ""));
        }


        public String getMyLabelText() {
            return myLabelText;
        }

    }

}
