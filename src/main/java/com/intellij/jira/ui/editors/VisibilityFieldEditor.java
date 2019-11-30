package com.intellij.jira.ui.editors;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.intellij.jira.rest.model.JiraVisibility;

import java.util.List;
import java.util.Objects;

public class VisibilityFieldEditor extends ComboBoxFieldEditor<String> {

    public VisibilityFieldEditor(List<String> items, JiraVisibility selectedValue, String issueKey) {
        super("Viewable by", items, issueKey, false, false);
        if(Objects.nonNull(selectedValue) && Objects.nonNull(items)){
            int index = items.indexOf(selectedValue.getValue());
            if(index > -1){
                this.myComboBox.setSelectedIndex(index);
            }
        }
    }

    @Override
    public JsonElement getJsonValue() {
        String selectedValue = getSelectedValue();
        if("All Users".equals(selectedValue)){
            return JsonNull.INSTANCE;
        }

        JsonObject visibility = new JsonObject();
        visibility.addProperty("type", "role");
        visibility.addProperty("value", selectedValue);

        return visibility;
    }
}
