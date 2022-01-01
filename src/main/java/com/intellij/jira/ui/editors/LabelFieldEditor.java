package com.intellij.jira.ui.editors;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.intellij.jira.util.JiraLabelUtil;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import java.util.Objects;

public class LabelFieldEditor extends AbstractFieldEditor<String> {

    private JBLabel myLabelText;

    public LabelFieldEditor(String fieldName) {
        this(fieldName, null);
    }

    public LabelFieldEditor(String fieldName, Object fieldValue) {
        super(fieldName, fieldValue);
    }

    @Override
    public JComponent createPanel() {
        myLabelText = JiraLabelUtil.createBoldLabel(getFieldValue());

        return FormBuilder.createFormBuilder()
                .addLabeledComponent(myLabel, myLabelText, true)
                .getPanel();
    }

    @Override
    public JsonElement getJsonValue() {
        return JsonNull.INSTANCE;
    }

    @Nullable
    @Override
    public ValidationInfo validate() {
        return null;
    }

    @Override
    public String getFieldValue() {
        return Objects.nonNull(myFieldValue) ? (String) myFieldValue : "None";
    }

}
