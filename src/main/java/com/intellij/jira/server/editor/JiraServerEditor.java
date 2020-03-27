package com.intellij.jira.server.editor;

import com.intellij.jira.server.JiraServer;
import com.intellij.jira.server.auth.AuthType;
import com.intellij.jira.ui.JiraTabbedPane;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class JiraServerEditor {

    private final Project myProject;
    private final JiraServer myServer;
    private final boolean mySelectedServer;

    private BiConsumer<JiraServer, Boolean> myChangeListener;
    private Consumer<JiraServer> myChangeUrlListener;

    private JiraTabbedPane myTabbedPane;

    public JiraServerEditor(Project project, JiraServer server, boolean selected, BiConsumer<JiraServer, Boolean> changeListener, Consumer<JiraServer> changeUrlListener) {
        this.myProject = project;
        this.myServer = server;
        this.mySelectedServer = selected;
        this.myChangeListener = changeListener;
        this.myChangeUrlListener = changeUrlListener;
    }

    public JPanel getPanel(){
        JiraServerAuthEditor userAndPassAuthEditor = new JiraServerUserAndPassAuthEditor(myProject, myServer, mySelectedServer, myChangeListener, myChangeUrlListener);
        JiraServerAuthEditor apiTokenAuthEditor = new JiraServerAPITokenAuthEditor(myProject, myServer, mySelectedServer, myChangeListener, myChangeUrlListener);

        this.myTabbedPane = new JiraTabbedPane(JTabbedPane.NORTH);
        this.myTabbedPane.addTab("User And Pass", userAndPassAuthEditor.getPanel());
        this.myTabbedPane.addTab("API Token", apiTokenAuthEditor.getPanel());

        if (AuthType.API_TOKEN == myServer.getType()) {
            this.myTabbedPane.setSelectedIndex(1);
        }

        return FormBuilder.createFormBuilder()
                .addComponent(this.myTabbedPane)
                .getPanel();
    }

}
