package com.intellij.jira.ui.popup;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.impl.SimpleDataContext;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Conditions;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.ui.popup.PopupFactoryImpl;
import org.jetbrains.annotations.NotNull;

public class JiraIssueAssignableUsersPopup extends PopupFactoryImpl.ActionGroupPopup {

    public JiraIssueAssignableUsersPopup(@NotNull ActionGroup actionGroup, @NotNull Project project) {
        super("Assign to", actionGroup, SimpleDataContext.builder()
                .add(CommonDataKeys.PROJECT, project)
                .add(PlatformDataKeys.CONTEXT_COMPONENT, IdeFocusManager.getInstance(project).getFocusOwner())
                .build(), false, false, false, false, null, 10, Conditions.alwaysTrue(), null);
    }

}
