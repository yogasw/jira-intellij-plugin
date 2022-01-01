package com.intellij.jira.ui.editors;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

import java.awt.Dimension;
import java.util.Objects;

import static com.intellij.jira.util.JiraGsonUtil.createPrimitive;
import static com.intellij.openapi.util.text.StringUtil.isEmpty;
import static com.intellij.openapi.util.text.StringUtil.trim;

public class TextFieldEditor extends AbstractFieldEditor<String> {

    protected JBTextField myTextField;

    public TextFieldEditor(String fieldName, Object fieldValue, boolean required) {
        super(fieldName, fieldValue, required);
    }

    @Override
    public String getFieldValue() {
        return Objects.nonNull(myFieldValue) ? (String) myFieldValue : "";
    }

    @Override
    public JComponent createPanel() {
        myTextField = new JBTextField();
        myTextField.setPreferredSize(getFieldSize());
        myTextField.setText(getFieldValue());

        return FormBuilder.createFormBuilder()
                .addLabeledComponent(myLabel, myTextField)
                .getPanel();
    }

    public Dimension getFieldSize() {
        return JBUI.size(250, 24);
    }

    @Override
    public JsonElement getJsonValue() {
        if(isEmpty(trim(myTextField.getText()))){
            return JsonNull.INSTANCE;
        }

        return createPrimitive(myTextField.getText());
    }

    @Nullable
    @Override
    public ValidationInfo validate() {
        if(isRequired() && isEmpty(trim(myTextField.getText()))){
            return new ValidationInfo(myLabel.getMyLabelText() + " is required");
        }

        return null;
    }

    public JBTextField getMyTextField() {
        return myTextField;
    }
}
