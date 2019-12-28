package com.intellij.jira.ui.editors;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.intellij.jira.rest.model.JiraIssueTimeTracking;

public class RemainingEstimateTransitionFieldEditor extends RemainingEstimateFieldEditor {

    private static final String ADJUST_ESTIMATE = "adjustEstimate";

    public RemainingEstimateTransitionFieldEditor(String issueKey, String fieldName, boolean required, JiraIssueTimeTracking timeTracking) {
        super(issueKey, fieldName, required, timeTracking, true);
    }

    @Override
    public JsonElement getJsonValue() {
        JsonObject remainingEstimate = new JsonObject();
        if (newButton.isSelected()){
            remainingEstimate.addProperty(ADJUST_ESTIMATE, "new");
            remainingEstimate.addProperty("newEstimate", myNewTextFieldEditor.getMyTextField().getText());
        } else if (manualButton.isSelected()){
            remainingEstimate.addProperty(ADJUST_ESTIMATE, "manual");
            remainingEstimate.addProperty("increaseBy", myManualTextFieldEditor.getMyTextField().getText());
        } else if (leaveButton.isSelected()){
            remainingEstimate.addProperty(ADJUST_ESTIMATE, "leave");
        } else {
            remainingEstimate.addProperty(ADJUST_ESTIMATE, "auto");
        }

        return remainingEstimate;
    }
}
