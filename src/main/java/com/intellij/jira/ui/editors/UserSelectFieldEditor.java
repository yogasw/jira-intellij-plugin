package com.intellij.jira.ui.editors;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.intellij.jira.rest.model.JiraIssueUser;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.intellij.jira.util.JiraGsonUtil.createArrayNameObjects;
import static com.intellij.jira.util.JiraGsonUtil.createNameObject;
import static com.intellij.openapi.util.text.StringUtil.isEmpty;
import static com.intellij.openapi.util.text.StringUtil.trim;
import static com.intellij.util.containers.ContainerUtil.getFirstItem;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

public class UserSelectFieldEditor extends SelectFieldEditor<JiraIssueUser> {

    private List<JiraIssueUser> mySelectedUsers = new ArrayList<>();

    public UserSelectFieldEditor(String issueKey, String fieldName, Object fieldValue, boolean required) {
        this(issueKey, fieldName, fieldValue, required, false);
    }

    public UserSelectFieldEditor(String issueKey, String fieldName, Object fieldValue, boolean required, boolean isMultiSelect) {
        super(issueKey, fieldName, fieldValue, required, isMultiSelect);
        myButtonAction = new UserPickerDialogAction();
        JiraIssueUser user = getFieldValue();
        if(Objects.nonNull(user)) {
            myTextField.setText(user.getDisplayName());
            mySelectedUsers.add(user);
        }
    }

    @Override
    public JsonElement getJsonValue() {
        if(isEmpty(trim(myTextField.getText()))){
            return JsonNull.INSTANCE;
        }

        List<String> selectedUserNames = getSelectedUserNames();
        if(myIsMultiSelect){
            return createArrayNameObjects(selectedUserNames);
        }

        return createNameObject(getFirstItem(selectedUserNames));
    }

    @Override
    public JiraIssueUser getFieldValue() {
        if (Objects.isNull(fieldValue)) {
            return null;
        }

        return ((JiraIssueUser) fieldValue);
    }

    private List<String> getSelectedUserNames() {
        return mySelectedUsers.stream().map(JiraIssueUser::getName).collect(toList());
    }

    private class UserPickerDialogAction extends PickerDialogAction {

        public UserPickerDialogAction() {
            super();
        }

        @Override
        public void actionPerformed(AnActionEvent e) {
            super.actionPerformed(e);
            if(nonNull(myJiraRestApi)){
                List<JiraIssueUser> users = myJiraRestApi.getAssignableUsers(issueKey);
                UserPickerDialog dialog = new UserPickerDialog(myProject, users, getFieldValue());
                dialog.show();
            }

        }
    }

    class UserPickerDialog extends PickerDialog<JiraIssueUser> {

        public UserPickerDialog(@Nullable Project project, List<JiraIssueUser> items, JiraIssueUser selectedUser) {
            super(project, "Users", items, Collections.singletonList(selectedUser));
        }

        @Override
        protected void doOKAction() {
            mySelectedUsers = myList.getSelectedValuesList();
            myTextField.setText(mySelectedUsers.isEmpty() ? "" : mySelectedUsers.stream().map(JiraIssueUser::getDisplayName).collect(Collectors.joining(", ")));

            super.doOKAction();
        }
    }

}
