package com.intellij.jira.ui.editors;

import com.google.gson.JsonElement;
import com.intellij.jira.util.JiraGsonUtil;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class RemainingEstimateFieldEditor extends AbstractFieldEditor {

    private JRadioButton autoButton;
    private JRadioButton leaveButton;
    private JRadioButton newButton;
    private JRadioButton manualButton;

    private TimeSpentEditor myNewTextFieldEditor;
    private TimeSpentEditor myManualTextFieldEditor;

    private boolean showManualField;

    public RemainingEstimateFieldEditor(String fieldName, boolean showManualField, String issueKey, boolean required) {
        super(fieldName, issueKey, required);
        this.showManualField = showManualField;
    }

    @Override
    public JComponent createPanel() {

        this.autoButton = new JRadioButton("Adjust automatically", true);
        this.leaveButton = new JRadioButton("Use existing estimate of 6 hours");
        this.newButton = new JRadioButton();
        this.manualButton = new JRadioButton();

        ButtonGroup myRadioGroup = new ButtonGroup();
        myRadioGroup.add(autoButton);
        myRadioGroup.add(leaveButton);
        myRadioGroup.add(newButton);
        if(showManualField){
            myRadioGroup.add(manualButton);
        }

        this.myNewTextFieldEditor = new TimeSpentEditor("Set to", "", this.issueKey, false);
        this.myManualTextFieldEditor = new TimeSpentEditor("Reduce by", "", this.issueKey, false);

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

}
