package com.intellij.jira.server.editor;

import com.intellij.jira.server.JiraServer;
import com.intellij.jira.server.auth.AuthType;
import com.intellij.jira.ui.JiraTabbedPane;
import com.intellij.jira.ui.editors.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class JiraServerEditor implements Editor {

    private static final int API_TOKEN_TAB = 1;

    private final Project myProject;
    private final JiraServer myServer;
    private final boolean mySelectedServer;

    private BiConsumer<JiraServer, Boolean> myChangeListener;
    private Consumer<JiraServer> myChangeUrlListener;

    private JiraTabbedPane myTabbedPane;
    private JiraServerAuthEditor userAndPassAuthEditor;
    private JiraServerAuthEditor apiTokenAuthEditor;

    public JiraServerEditor(Project project, JiraServer server, boolean selected, BiConsumer<JiraServer, Boolean> changeListener, Consumer<JiraServer> changeUrlListener) {
        this.myProject = project;
        this.myServer = server;
        this.mySelectedServer = selected;
        this.myChangeListener = changeListener;
        this.myChangeUrlListener = changeUrlListener;
    }

    @Override
    public JComponent createPanel(){
        this.userAndPassAuthEditor = new JiraServerUserAndPassAuthEditor(myProject, myServer, mySelectedServer, myChangeListener, myChangeUrlListener);
        this.apiTokenAuthEditor = new JiraServerAPITokenAuthEditor(myProject, myServer, mySelectedServer, myChangeListener, myChangeUrlListener);

        this.myTabbedPane = new JiraTabbedPane(JTabbedPane.NORTH);
        this.myTabbedPane.addTab("User And Pass", userAndPassAuthEditor.createPanel());
        this.myTabbedPane.addTab("API Token", apiTokenAuthEditor.createPanel());

        if (AuthType.API_TOKEN == myServer.getType()) {
            this.myTabbedPane.setSelectedIndex(API_TOKEN_TAB);
        }

        return FormBuilder.createFormBuilder()
                .addComponent(this.myTabbedPane)
                .getPanel();
    }


    @Nullable
    public ValidationInfo validate() {
        if (isApiTokenAuthEditorSelected()) {
            return this.apiTokenAuthEditor.validate();
        }

        return this.userAndPassAuthEditor.validate();
    }

    private boolean isApiTokenAuthEditorSelected() {
        return this.myTabbedPane.getSelectedIndex() == API_TOKEN_TAB;
    }

}
