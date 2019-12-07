package com.intellij.jira.ui.editors;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.intellij.jira.util.JiraGsonUtil;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.UI;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MultiSelectFieldEditor<T> extends AbstractFieldEditor {
    private final static int DEFAULT_WIDTH = 450;
    private final static int DEFAULT_HEIGHT = 24;

    private final static String VALUE_PROPERTY = "_value";

    private JButton triggerPopup;
    private List<JCheckBoxMenuItem> selectableItems;

    public MultiSelectFieldEditor(String fieldName, List<T> items, String issueKey, boolean required, Object currentValue) {
        super(fieldName, issueKey, required);
        JPopupMenu popupMenu = new JPopupMenu("");

        this.triggerPopup = new JButton("");
        this.triggerPopup.setPreferredSize(UI.size(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        this.triggerPopup.addActionListener(e -> popupMenu.show(triggerPopup, 0, 0));

        this.selectableItems = new ArrayList<>();

        JMenuItem header = new JMenuItem(this.myLabel.getMyLabelText());
        header.setEnabled(false);
        popupMenu.add(header);
        popupMenu.add(new JSeparator());
        for (T item : items) {
            JCheckBoxMenuItem selectableItem = new JCheckBoxMenuItem(item != null ? item.toString() : "");
            selectableItem.setState(isSelected(currentValue, item));
            selectableItem.putClientProperty(VALUE_PROPERTY, item);
            selectableItem.addActionListener(e -> recalcTriggerText());

            this.selectableItems.add(selectableItem);
            popupMenu.add(selectableItem);
        }
        recalcTriggerText();

        this.triggerPopup.setComponentPopupMenu(popupMenu);
    }

    private void recalcTriggerText() {
        StringBuilder sb = new StringBuilder();

        Collection<T> selectedValues = getSelectedValues();
        for (T selectedValue : selectedValues) {
            if (selectedValue == null) {
                continue;
            }

            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(selectedValue.toString());
        }

        this.triggerPopup.setText(sb.length() > 0 ? sb.toString() : "-");
    }

    private boolean isSelected(Object currentValue, T item) {
        if (currentValue instanceof List) {
            if (findItem((List) currentValue, item) != null) {
                return true;
            }
        } else if (currentValue != null) {
            return currentValue.equals(item);
        }

        return false;
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
                .addLabeledComponent(this.myLabel, this.triggerPopup)
                .getPanel();
    }


    @Override
    public JsonElement getJsonValue() {
        Collection<T> selectedValues = getSelectedValues();
        List<String> selectedValuesToString = new ArrayList<>();
        for (T selectedValue : selectedValues) {
            if (selectedValue == null) {
                continue;
            }

            selectedValuesToString.add(selectedValue.toString());
        }

        if (selectedValuesToString.isEmpty()) {
            return JsonNull.INSTANCE;
        }


        return JiraGsonUtil.createArrayNameObjects(selectedValuesToString);
    }

    private Collection<T> getSelectedValues() {
        Set<T> ret = new LinkedHashSet<>();
        for (JCheckBoxMenuItem selectableItem : selectableItems) {
            if (selectableItem.getState()) {
                ret.add((T) selectableItem.getClientProperty(VALUE_PROPERTY));
            }
        }

        return ret;
    }

    @Nullable
    @Override
    public ValidationInfo validate() {
        Collection<T> selectedValues = getSelectedValues();
        if (isRequired() && selectedValues.isEmpty()) {
            return new ValidationInfo(myLabel.getMyLabelText() + " is required.");
        }

        return null;
    }
}
