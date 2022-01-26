package com.intellij.jira.ui.editors;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.server.JiraServerManager;
import com.intellij.jira.util.JiraGsonUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.TextFieldWithAutoCompletion;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import java.util.Arrays;
import java.util.Collection;

public class LabelsFieldEditor extends AbstractFieldEditor<String> {

    private final Project myProject;
    private final String myAutoCompleteUrl;
    private TextFieldWithAutoCompletion<String> myTextFieldWithAutoCompletion;

    public LabelsFieldEditor(Project project, String fieldName, String autoCompleteUrl) {
        super(fieldName, null);
        myProject = project;
        myAutoCompleteUrl = autoCompleteUrl;
        myTextFieldWithAutoCompletion = new TextFieldWithAutoCompletion(project, new LabelsCompletionProvider(), true, "");
    }

    @Override
    public String getFieldValue() {
        return null;
    }

    @Override
    public JComponent createPanel() {
        return FormBuilder.createFormBuilder()
                .addLabeledComponent(myLabel, myTextFieldWithAutoCompletion, true)
                .getPanel();
    }

    @Override
    public @Nullable ValidationInfo validate() {
        return null;
    }

    @Override
    public JsonElement getJsonValue() {
        String labels = myTextFieldWithAutoCompletion.getText();
        if (StringUtil.isEmpty(labels)) {
            return JsonNull.INSTANCE;
        }

        return JiraGsonUtil.createArray(Arrays.asList(labels.split(" ")));
    }


    private class LabelsCompletionProvider extends TextFieldWithAutoCompletion.StringsCompletionProvider {

        public LabelsCompletionProvider() {
            super(null, null);
        }

        @Override
        public @NotNull Collection<String> getItems(String prefix, boolean cached, CompletionParameters parameters) {
            JiraServerManager manager = ApplicationManager.getApplication().getService(JiraServerManager.class);
            JiraRestApi jiraRestApi = manager.getJiraRestApi(myProject);

            return jiraRestApi.findLabels(prefix, myAutoCompleteUrl);
        }
    }

}
