package com.intellij.jira.ui.editors;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.CollectionComboBoxModel;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import java.awt.event.ActionListener;
import java.util.List;

import static com.intellij.jira.util.JiraGsonUtil.createNameObject;
import static com.intellij.openapi.util.text.StringUtil.isEmpty;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class ComboBoxFieldEditor<T> extends AbstractFieldEditor<T> {

    protected ComboBox<T> myComboBox;
    private final CollectionComboBoxModel<T> myComboBoxItems;

    public ComboBoxFieldEditor(String fieldName, Object fieldValue, boolean required, List<T> items) {
        super(fieldName, fieldValue, required);
        myComboBoxItems = new CollectionComboBoxModel<>(items);
        myComboBox = new ComboBox(myComboBoxItems, 300);

        if (nonNull(fieldValue)) {
            selectItem();
        }
    }

    @Override
    public JComponent createPanel() {
        return FormBuilder.createFormBuilder()
                .addLabeledComponent(myLabel, myComboBox, true)
                .getPanel();
    }

    @Override
    public JsonElement getJsonValue() {
        if(isNull(myComboBox.getSelectedItem())){
            return JsonNull.INSTANCE;
        }

        return createNameObject(getSelectedValue());
    }

    public String getSelectedValue(){
        return nonNull(myComboBox.getSelectedItem()) ? myComboBox.getSelectedItem().toString() : "";
    }

    public void setSelectedValue(T value) {
        myComboBox.setSelectedItem(value);
    }

    @Nullable
    @Override
    public ValidationInfo validate() {
        if(isRequired() && isEmpty(getSelectedValue())){
            return new ValidationInfo(myLabel.getText() + " is required.");
        }

        return null;
    }

    @Override
    public T getFieldValue() {
        return (T) myFieldValue;
    }

    public void addActionListener(ActionListener listener) {
        myComboBox.addActionListener(listener);
    }

    private void selectItem() {
        // TODO: 22/12/2019 mejorar
        T currentValue = getFieldValue();
        if (currentValue instanceof List) {
            for (Object value : (List) currentValue) {
                T item = findItem(myComboBoxItems.getItems(), value);
                if (item != null) {
                    myComboBoxItems.setSelectedItem(item);
                    break;
                }
            }
        } else if (currentValue != null) {
            T item = findItem(myComboBoxItems.getItems(), currentValue);
            if (item != null) {
                myComboBoxItems.setSelectedItem(item);
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

}
