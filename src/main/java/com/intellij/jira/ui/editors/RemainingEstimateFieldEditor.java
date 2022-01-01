package com.intellij.jira.ui.editors;

import com.google.gson.JsonElement;
import com.intellij.jira.rest.model.JiraIssueTimeTracking;
import com.intellij.jira.util.JiraGsonUtil;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.Nullable;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Objects;

public class RemainingEstimateFieldEditor extends AbstractFieldEditor<String> {

    private JRadioButton myAutoButton;
    protected JRadioButton myLeaveButton;
    protected JRadioButton myNewButton;
    protected JRadioButton myManualButton;

    protected TimeSpentEditor myNewTextFieldEditor;
    protected TimeSpentEditor myManualTextFieldEditor;

    private JiraIssueTimeTracking myTimeTracking;
    private boolean myShowManualField;

    public RemainingEstimateFieldEditor(String fieldName, boolean required, JiraIssueTimeTracking timeTracking, boolean showManualField) {
        super(fieldName, null, required);
        myShowManualField = showManualField;
        myTimeTracking = timeTracking;
    }

    @Override
    public JComponent createPanel() {
        myAutoButton = new JRadioButton("Adjust automatically", true);
        myLeaveButton = new JRadioButton(getTimeTrackingText());
        myNewButton = new JRadioButton();
        myManualButton = new JRadioButton();

        ButtonGroup myRadioGroup = new ButtonGroup();
        myRadioGroup.add(myAutoButton);
        myRadioGroup.add(myLeaveButton);
        myRadioGroup.add(myNewButton);
        if (myShowManualField) {
            myRadioGroup.add(myManualButton);
        }

        myNewTextFieldEditor = new TimeSpentEditor("Set to", "", false);
        myManualTextFieldEditor = new TimeSpentEditor("Reduce by", "", false);

        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new GridLayout(4, 1));
        radioPanel.add(myAutoButton);
        radioPanel.add(myLeaveButton);
        JPanel radioWithTexFieldPanel = new JPanel(new FlowLayout());
        radioWithTexFieldPanel.add(myNewButton);
        radioWithTexFieldPanel.add(myNewTextFieldEditor.createPanel());
        radioPanel.add(radioWithTexFieldPanel);

        if (myShowManualField) {
            JPanel radioWithTexFieldPanel2 = new JPanel(new FlowLayout());
            radioWithTexFieldPanel2.add(myManualButton);
            radioWithTexFieldPanel2.add(myManualTextFieldEditor.createPanel());
            radioPanel.add(radioWithTexFieldPanel2);
        }

        return FormBuilder.createFormBuilder()
                    .addLabeledComponent(myLabel, radioPanel)
                    .getPanel();
    }

    @Override
    public JsonElement getJsonValue() {
        if (myNewButton.isSelected()) {
            return JiraGsonUtil.createPrimitive("new&newEstimate=" + myNewTextFieldEditor.getMyTextField().getText());
        } else if(myManualButton.isSelected()) {
            return JiraGsonUtil.createPrimitive("manual&increaseBy=" + myManualTextFieldEditor.getMyTextField().getText());
        } else if(myLeaveButton.isSelected()) {
            return JiraGsonUtil.createPrimitive("leave");
        }

        return JiraGsonUtil.createPrimitive("auto");
    }

    @Nullable
    @Override
    public ValidationInfo validate() {
       if (myNewButton.isSelected()) {
           return myNewTextFieldEditor.validate();
       } else if (myManualButton.isSelected()) {
           return myManualTextFieldEditor.validate();
       }

       return null;
    }

    private String getTimeTrackingText(){
        return Objects.isNull(myTimeTracking) ? "Leave estimate unset" : "Use existing estimate of " + myTimeTracking.getRemainingEstimate();
    }

    @Override
    public String getFieldValue() {
        return null;
    }
}
