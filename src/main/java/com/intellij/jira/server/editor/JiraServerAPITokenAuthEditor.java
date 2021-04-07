package com.intellij.jira.server.editor;

import com.intellij.jira.server.JiraServer;
import com.intellij.jira.server.auth.AuthType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UI;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.intellij.openapi.util.text.StringUtil.isEmpty;
import static com.intellij.openapi.util.text.StringUtil.trim;
import static java.lang.String.valueOf;

public class JiraServerAPITokenAuthEditor extends JiraServerAuthEditor {

    private JLabel myEmailLabel;
    private JTextField myEmailField;

    private JLabel myApiTokenLabel;
    private JPasswordField myApiTokenField;

    public JiraServerAPITokenAuthEditor(Project project, JiraServer server, boolean selected, BiConsumer<JiraServer, Boolean> changeListener, Consumer<JiraServer> changeUrlListener) {
        super(project, server, selected, changeListener, changeUrlListener);
    }

    @Override
    public JPanel getPanel() {
        this.myEmailLabel = new JBLabel("Email:", 4);
        this.myEmailField = new JBTextField();
        this.myEmailField.setText(myServer.hasUserAndPassAuth() ? "" : myServer.getUsername());
        this.myEmailField.setPreferredSize(JBUI.size(DEFAULT_WIDTH, DEFAULT_HEIGHT));

        this.myApiTokenLabel = new JBLabel("API Token:", 4);
        this.myApiTokenField = new JPasswordField();
        this.myApiTokenField.setText(myServer.hasUserAndPassAuth() ? "" : myServer.getPassword());
        this.myApiTokenField.setPreferredSize(JBUI.size(DEFAULT_WIDTH, DEFAULT_HEIGHT));

        installListeners();

        return FormBuilder.createFormBuilder()
                .addVerticalGap(10)
                .addLabeledComponent(this.myUrlLabel, this.myUrlField)
                .addLabeledComponent(this.myEmailLabel, this.myEmailField)
                .addLabeledComponent(this.myApiTokenLabel, this.myApiTokenField)
                .addComponent(this.myDefaultServerCheckbox)
                .addComponentToRightColumn(this.myTestPanel)
                .getPanel();
    }

    @Override
    public void installListeners() {
        super.installListeners();
        installListener(myEmailField);
        installListener(myApiTokenField);
    }

    @Override
    protected void apply() {
        String url = trim(myUrlField.getText());
        String useremail = trim(myEmailField.getText());
        String apiToken = trim(valueOf(myApiTokenField.getPassword()));

        this.myServer.withApiToken(url, useremail, apiToken);

        super.apply();
    }

    @Nullable
    public ValidationInfo validate() {
        ValidationInfo info = super.validate();
        if (Objects.isNull(info)) {
            if (isEmpty(trim(myEmailField.getText()))) {
                info = new ValidationInfo("Email is required.");
            }

            if (isEmpty(trim(valueOf(myApiTokenField.getPassword())))) {
                info = new ValidationInfo("API Token is required.");
            }
        }

        return info;
    }

}
