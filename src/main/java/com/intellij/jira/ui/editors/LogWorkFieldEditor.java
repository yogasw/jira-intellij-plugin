package com.intellij.jira.ui.editors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.intellij.jira.rest.model.JiraIssueTimeTracking;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

public class LogWorkFieldEditor extends AbstractFieldEditor<String> {

    protected TimeSpentEditor timeSpentEditor;
    protected DateTimeFieldEditor startedEditor;
    protected RemainingEstimateFieldEditor remainingEstimateEditor;

    private JiraIssueTimeTracking timeTracking;

    public LogWorkFieldEditor(String issueKey, String fieldName, JiraIssueTimeTracking timeTracking, boolean required) {
        super(issueKey, fieldName, null, required);
        this.timeTracking = timeTracking;
    }

    @Override
    public String getFieldValue() {
        return null;
    }

    @Override
    public JComponent createPanel() {
        this.timeSpentEditor = new TimeSpentEditor(this.issueKey);
        this.startedEditor = new DateTimeFieldEditor(this.issueKey,"Date Started", false);
        this.remainingEstimateEditor = new RemainingEstimateTransitionFieldEditor(this.issueKey, "Remaining Estimate", false, this.timeTracking);

        JPanel myWorkLogPanel = FormBuilder.createFormBuilder()
                .addComponent(timeSpentEditor.createPanel())
                .addComponent(startedEditor.createPanel())
                .addComponent(remainingEstimateEditor.createPanel())
                .getPanel();

        return FormBuilder.createFormBuilder()
                .addLabeledComponent(myLabel, myWorkLogPanel)
                .getPanel();
    }

    @Override
    public JsonElement getJsonValue() {
        if (StringUtil.isEmpty(StringUtil.trim(timeSpentEditor.getMyTextField().getText()))){
            return JsonNull.INSTANCE;
        }

        JsonArray array = new JsonArray();
        JsonObject addObject = new JsonObject();

        JsonObject logWork = remainingEstimateEditor.getJsonValue().getAsJsonObject();
        logWork.add("timeSpentSeconds", timeSpentEditor.getJsonValue());
        logWork.add("started", startedEditor.getJsonValue());

        addObject.add("add", logWork);
        array.add(addObject);

        return array;
    }

    @Nullable
    @Override
    public ValidationInfo validate() {
        ValidationInfo info = this.timeSpentEditor.validate();
        if(Objects.nonNull(info)) {
            return info;
        }

        info = this.startedEditor.validate();
        if(Objects.nonNull(info)) {
            return info;
        }

        return this.remainingEstimateEditor.validate();
    }
}
