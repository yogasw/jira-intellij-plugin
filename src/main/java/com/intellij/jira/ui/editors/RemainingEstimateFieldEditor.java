package com.intellij.jira.ui.editors;

import com.google.gson.JsonElement;
import com.intellij.jira.rest.model.JiraIssueTimeTracking;
import com.intellij.jira.util.JiraGsonUtil;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class RemainingEstimateFieldEditor extends AbstractFieldEditor<String> {

    private JRadioButton autoButton;
    protected JRadioButton leaveButton;
    protected JRadioButton newButton;
    protected JRadioButton manualButton;

    protected TimeSpentEditor myNewTextFieldEditor;
    protected TimeSpentEditor myManualTextFieldEditor;

    private JiraIssueTimeTracking timeTracking;
    private boolean showManualField;

    public RemainingEstimateFieldEditor(String issueKey, String fieldName, boolean required, JiraIssueTimeTracking timeTracking, boolean showManualField) {
        super(issueKey, fieldName, null, required);
        this.showManualField = showManualField;
        this.timeTracking = timeTracking;
    }

    @Override
    public JComponent createPanel() {

        this.autoButton = new JRadioButton("Adjust automatically", true);
        this.leaveButton = new JRadioButton(getTimeTrackingText());
        this.newButton = new JRadioButton();
        this.manualButton = new JRadioButton();

        ButtonGroup myRadioGroup = new ButtonGroup();
        myRadioGroup.add(autoButton);
        myRadioGroup.add(leaveButton);
        myRadioGroup.add(newButton);
        if(showManualField){
            myRadioGroup.add(manualButton);
        }

        this.myNewTextFieldEditor = new TimeSpentEditor( this.issueKey, "Set to", "", false);
        this.myManualTextFieldEditor = new TimeSpentEditor( this.issueKey, "Reduce by", "", false);

        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new GridLayout(4, 1));
        radioPanel.add(autoButton);
        radioPanel.add(leaveButton);
        JPanel radioWithTexFieldPanel = new JPanel(new FlowLayout());
        radioWithTexFieldPanel.add(newButton);
        radioWithTexFieldPanel.add(myNewTextFieldEditor.createPanel());
        radioPanel.add(radioWithTexFieldPanel);

        if(showManualField){
            JPanel radioWithTexFieldPanel2 = new JPanel(new FlowLayout());
            radioWithTexFieldPanel2.add(manualButton);
            radioWithTexFieldPanel2.add(myManualTextFieldEditor.createPanel());
            radioPanel.add(radioWithTexFieldPanel2);
        }

        return FormBuilder.createFormBuilder()
                    .addLabeledComponent(myLabel, radioPanel)
                    .getPanel();
    }

    @Override
    public JsonElement getJsonValue() {
        if(newButton.isSelected()){
            return JiraGsonUtil.createPrimitive("new&newEstimate=" + myNewTextFieldEditor.getMyTextField().getText());
        }else if(manualButton.isSelected()){
            return JiraGsonUtil.createPrimitive("manual&increaseBy=" + myManualTextFieldEditor.getMyTextField().getText());
        }else if(leaveButton.isSelected()){
            return JiraGsonUtil.createPrimitive("leave");
        }

        return JiraGsonUtil.createPrimitive("auto");
    }

    @Nullable
    @Override
    public ValidationInfo validate() {
       if(newButton.isSelected()){
           return myNewTextFieldEditor.validate();
       }else if(manualButton.isSelected()){
           return myManualTextFieldEditor.validate();
       }

       return null;
    }

    private String getTimeTrackingText(){
        return Objects.isNull(this.timeTracking) ? "Leave estimate unset" : "Use existing estimate of " + this.timeTracking.getRemainingEstimate();
    }

    @Override
    public String getFieldValue() {
        return null;
    }
}
