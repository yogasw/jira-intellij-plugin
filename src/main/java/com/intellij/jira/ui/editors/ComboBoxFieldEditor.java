package com.intellij.jira.ui.editors;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.CollectionComboBoxModel;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

import static com.intellij.jira.util.JiraGsonUtil.createNameObject;
import static com.intellij.openapi.util.text.StringUtil.isEmpty;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class ComboBoxFieldEditor<T> extends AbstractFieldEditor {

    protected ComboBox<T> myComboBox;
    private CollectionComboBoxModel<T> myComboBoxItems;
    private boolean isMultiSelect;

    public ComboBoxFieldEditor(String fieldName, List<T> items, String issueKey, boolean required, boolean isMultiSelect, Object currentValue) {
        super(fieldName, issueKey, required);
        this.myComboBoxItems = new CollectionComboBoxModel<>(items);
        this.myComboBox = new ComboBox(myComboBoxItems, 300);
        this.isMultiSelect = isMultiSelect;

        if (currentValue instanceof List) {
            for (Object value : (List) currentValue) {
                T item = findItem(items, value);
                if (item != null) {
                    this.myComboBoxItems.setSelectedItem(item);
                    break;
                }
            }
        } else if (currentValue != null) {
            T item = findItem(items, currentValue);
            if (item != null) {
                this.myComboBoxItems.setSelectedItem(item);
            }
        }
    }

    private T findItem(List<T> items, Object value) {
        for (T item : items) {
            if (item != null && item.equals(value)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public JComponent createPanel() {
        return FormBuilder.createFormBuilder()
                .addLabeledComponent(this.myLabel, this.myComboBox)
                .getPanel();
    }


    @Override
    public JsonElement getJsonValue() {
        if(isNull(myComboBox.getSelectedItem())){
            return JsonNull.INSTANCE;
        }

        return createNameObject(getSelectedValue(), isMultiSelect);
    }

    protected String getSelectedValue(){
        return nonNull(this.myComboBox.getSelectedItem()) ? this.myComboBox.getSelectedItem().toString() : "";
    }


    @Nullable
    @Override
    public ValidationInfo validate() {
        if(isRequired() && isEmpty(getSelectedValue())){
            return new ValidationInfo(myLabel.getMyLabelText() + " is required.");
        }

        return null;
    }
}
