package com.intellij.jira.actions;

import com.intellij.jira.server.JiraServerManager;
import com.intellij.jira.ui.table.column.JiraIssueColumn;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Separator;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.intellij.jira.ui.table.column.JiraIssueColumnUtils.*;
import static java.util.Objects.isNull;


public class ToggleIssueColumnActionGroup extends DefaultActionGroup {

    public ToggleIssueColumnActionGroup() {
        super();
    }

    @Override
    public AnAction @NotNull [] getChildren(@Nullable AnActionEvent e) {
        List<AnAction> actions = new ArrayList<>();
        if (e != null) {
            actions.add(Separator.create("Show Columns"));
        }

        getHideableColumns().forEach(column ->
            actions.add(new ToggleIssueColumnActionGroup.ToggleColumnAction(column))
        );

        return actions.toArray(AnAction.EMPTY_ARRAY);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (isNull(project)|| !project.isInitialized() || project.isDisposed()) {
            e.getPresentation().setVisible(false);
        } else {
            JiraServerManager manager = JiraServerManager.getInstance(project);
            e.getPresentation().setVisible(manager.hasJiraServerConfigured());
        }
    }

    private static final class ToggleColumnAction extends ToggleAction {
        private final JiraIssueColumn<?, ?> myColumn;

        private ToggleColumnAction(@NotNull JiraIssueColumn<?, ?> column) {
            super(() -> column.getName());
            myColumn = column;
        }

        @Override
        public boolean isSelected(@NotNull AnActionEvent e) {
            return isVisible(myColumn);
        }

        @Override
        public void setSelected(@NotNull AnActionEvent e, boolean state) {
            if (state) {
                addColumn(myColumn);
            } else {
                removeColumn(myColumn);
            }
        }

        @Override
        public void update(@NotNull AnActionEvent e) {
            super.update(e);
            e.getPresentation().setEnabledAndVisible(true);
        }

    }

}
