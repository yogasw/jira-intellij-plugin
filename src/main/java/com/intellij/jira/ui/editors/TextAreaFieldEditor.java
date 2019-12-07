package com.intellij.jira.ui.editors;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static com.intellij.jira.util.JiraGsonUtil.createPrimitive;
import static com.intellij.openapi.util.text.StringUtil.isEmpty;
import static com.intellij.openapi.util.text.StringUtil.trim;

public class TextAreaFieldEditor extends AbstractFieldEditor {

    @SuppressWarnings("unused")
    private JPanel myPanel;
    @SuppressWarnings("unused")
    private JLabel myTextAreaLabel;
    protected JTextArea myTextArea;
    private String currentValue;


    public TextAreaFieldEditor(String fieldName, String issueKey, boolean required, Object currentValue) {
        super(fieldName, issueKey, required);
        if (currentValue instanceof String) {
            this.currentValue = currentValue.toString();
        }
    }

    @Override
    public JComponent createPanel() {
        this.myTextArea.setBorder(BorderFactory.createLineBorder(JBColor.border()));
        this.myTextArea.setText(currentValue);
        this.myTextAreaLabel.setText(myLabel.getText());

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

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
