package com.intellij.jira.ui.panels;

import com.intellij.jira.ui.dialog.ConfigureJiraServersDialog;
import com.intellij.openapi.project.Project;
import com.intellij.ui.SimpleTextAttributes;
import org.jetbrains.annotations.NotNull;

public class JiraServerNotConfiguredPanel extends JiraPanelWithEmptyText {

    public JiraServerNotConfiguredPanel(@NotNull Project project) {
        super("No Jira server found");
        getEmptyText()
            .appendSecondaryText("Configure", SimpleTextAttributes.LINK_PLAIN_ATTRIBUTES, e -> new ConfigureJiraServersDialog(project).show());
    }

}
