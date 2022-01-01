package com.intellij.jira.ui.editors;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.Nullable;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import java.util.Objects;

import static com.intellij.jira.util.JiraGsonUtil.createPrimitive;
import static com.intellij.openapi.util.text.StringUtil.isEmpty;
import static com.intellij.openapi.util.text.StringUtil.trim;

public class TextAreaFieldEditor extends AbstractFieldEditor<String> {

    private JPanel myPanel;
    private JLabel myTextAreaLabel;
    protected JTextArea myTextArea;

    public TextAreaFieldEditor(String fieldName, Object fieldValue, boolean required) {
        super(fieldName, fieldValue, required);
    }

    @Override
    public JComponent createPanel() {
        myTextArea.setBorder(BorderFactory.createLineBorder(JBColor.border()));
        myTextArea.setText(getFieldValue());
        myTextAreaLabel.setText(myLabel.getText());

        return myPanel;
    }

    @Override
    public JsonElement getJsonValue() {
        if(isEmpty(trim(myTextArea.getText()))){
            return JsonNull.INSTANCE;
        }

        return createPrimitive(myTextArea.getText());
    }

    @Nullable
    @Override
    public ValidationInfo validate() {
        if(isRequired() && isEmpty(trim(myTextArea.getText()))){
            return new ValidationInfo(myLabel.getMyLabelText() + " is required.");
        }

        return null;
    }

    @Override
    public String getFieldValue() {
        return Objects.nonNull(myFieldValue) ? (String) myFieldValue : "";
    }

}
