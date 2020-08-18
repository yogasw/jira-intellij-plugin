package com.intellij.jira.server.editor;

import com.intellij.jira.server.JiraServer;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.UI;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.intellij.openapi.util.text.StringUtil.isEmpty;
import static com.intellij.openapi.util.text.StringUtil.trim;
import static java.lang.String.valueOf;

public class JiraServerUserAndPassAuthEditor extends JiraServerAuthEditor {

    private JLabel myUsernameLabel;
    private JTextField myUsernameField;

    private JLabel myPasswordLabel;
    private JPasswordField myPasswordField;

    public JiraServerUserAndPassAuthEditor(Project project, JiraServer server, boolean selected, BiConsumer<JiraServer, Boolean> changeListener, Consumer<JiraServer> changeUrlListener) {
        super(project, server, selected, changeListener, changeUrlListener);
    }

    @Override
    public JPanel getPanel() {

        this.myUsernameLabel = new JBLabel("Username:", 4);
        this.myUsernameField = new JBTextField();
        this.myUsernameField.setText(myServer.hasUserAndPassAuth() ? myServer.getUsername() : "");
        this.myUsernameField.setPreferredSize(UI.size(DEFAULT_WIDTH, DEFAULT_HEIGHT));

        this.myPasswordLabel = new JBLabel("Password:", 4);
        this.myPasswordField = new JPasswordField();
        this.myPasswordField.setText(myServer.hasUserAndPassAuth() ? myServer.getPassword() : "");
        this.myPasswordField.setPreferredSize(UI.size(DEFAULT_WIDTH, DEFAULT_HEIGHT));

        installListeners();

        return FormBuilder.createFormBuilder()
                .addVerticalGap(10)
                .addLabeledComponent(this.myUrlLabel, this.myUrlField)
                .addLabeledComponent(this.myUsernameLabel, this.myUsernameField)
                .addLabeledComponent(this.myPasswordLabel, this.myPasswordField)
                .addComponent(this.myDefaultServerCheckbox)
                .addComponentToRightColumn(this.myTestPanel)
                .getPanel();
    }

    @Override
    public void installListeners() {
        super.installListeners();
        installListener(myUsernameField);
        installListener(myPasswordField);
    }

    @Override
    protected void apply() {
        String url = trim(myUrlField.getText());
        String username = trim(myUsernameField.getText());
        String password = trim(valueOf(myPasswordField.getPassword()));

        this.myServer.withUserAndPass(url, username, password);

        super.apply();
    }

    @Nullable
    public ValidationInfo validate() {
        ValidationInfo info = super.validate();
        if (Objects.isNull(info)) {
            if (isEmpty(trim(myUsernameField.getText()))) {
                info = new ValidationInfo("Username is required.");
            }

            if (isEmpty(trim(valueOf(myPasswordField.getPassword())))) {
                info = new ValidationInfo("Password is required.");
            }
        }

        return info;
    }


}
