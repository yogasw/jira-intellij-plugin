package com.intellij.jira.actions;

import com.intellij.icons.AllIcons;
import com.intellij.jira.settings.branch.BranchSettings;
import com.intellij.jira.settings.branch.BranchSettingsState;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsActions;
import git4idea.actions.branch.GitNewBranchAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class BranchActionGroup extends DefaultActionGroup {


    public BranchActionGroup() {
        super("Branch", true);
        getTemplatePresentation().setIcon(AllIcons.Vcs.Branch);

        add(new GitNewBranchAction());

        BranchSettings branchSettings = BranchSettings.getInstance();
        BranchSettingsState state = branchSettings.getState();

        if (Objects.nonNull(state)) {
            state.getBranchTypes().forEach(type -> add(NewBranchAction.withName(type + "/LK-4050")));
        }

        //add(new Separator());
        //add(new ChangelistSettingsDialogAction());
    }


    private static class NewBranchAction extends AnAction {

        private final String myBranchName;

        private NewBranchAction(@Nullable @NlsActions.ActionText String text) {
            super(text);
            myBranchName = text;
        }

        public static NewBranchAction withName(String name) {
            return new NewBranchAction(name);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            Project project = e.getProject();
            if (Objects.isNull(project)) {
                return;
            }

            // TODO: create branch...
            // ...
        }


    }

}
