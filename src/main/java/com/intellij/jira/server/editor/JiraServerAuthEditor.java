package com.intellij.jira.server.editor;

import com.intellij.jira.server.JiraServer;
import com.intellij.jira.tasks.TestJiraServerConnectionTask;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import java.awt.BorderLayout;
import java.net.UnknownHostException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.intellij.jira.util.JiraPanelUtil.MARGIN_BOTTOM;

public abstract class JiraServerAuthEditor {

    protected static final int DEFAULT_WIDTH = 450;
    protected static final int DEFAULT_HEIGHT = 24;

    protected final Project myProject;
    protected JiraServer myServer;
    protected final boolean mySelectedServer;

    protected BiConsumer<JiraServer, Boolean> myChangeListener;
    protected Consumer<JiraServer> myChangeUrlListener;


    protected JLabel myUrlLabel;
    protected JTextField myUrlField;

    protected JCheckBox myDefaultServerCheckbox;

    protected JPanel myTestPanel;
    protected JButton myTestButton;

    public JiraServerAuthEditor(Project project, JiraServer server, boolean selected, BiConsumer<JiraServer, Boolean> changeListener, Consumer<JiraServer> changeUrlListener) {
        this.myProject = project;
        this.myServer = server;
        this.mySelectedServer = selected;
        this.myChangeListener = changeListener;
        this.myChangeUrlListener = changeUrlListener;
        init();
    }

    public abstract JPanel getPanel();

    public void installListeners() {
        installListener(myUrlField);
        installListener(myDefaultServerCheckbox);
        installListener(myTestButton);
    }

    private void init() {
        this.myUrlLabel = new JBLabel("Server URL:", 4);
        this.myUrlField = new JBTextField();
        this.myUrlField.setText(myServer.getUrl());
        this.myUrlField.setPreferredSize(JBUI.size(DEFAULT_WIDTH, DEFAULT_HEIGHT));

        this.myDefaultServerCheckbox = new JCheckBox("Set Default");
        this.myDefaultServerCheckbox.setBorder(JBUI.Borders.emptyRight(4));
        this.myDefaultServerCheckbox.setSelected(mySelectedServer);

        this.myTestPanel = new JPanel(new BorderLayout());
        myTestPanel.setBorder(MARGIN_BOTTOM);
        this.myTestButton = new JButton("Test");
        this.myTestPanel.add(myTestButton, BorderLayout.EAST);
    }

    protected void installListener(JTextField textField) {
        textField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                ApplicationManager.getApplication().invokeLater(() -> {
                    apply();
                });

            }
        });
    }

    private void installListener(JCheckBox checkBox) {
        checkBox.addActionListener(e -> defaultServerChanged());
    }

    private void installListener(JButton button){
        button.addActionListener((event) -> {
            ApplicationManager.getApplication().invokeLater(() -> {
                TestJiraServerConnectionTask task = new TestJiraServerConnectionTask(myProject, myServer);
                ProgressManager.getInstance().run(task);
                Exception e = task.getException();
                if (e == null) {
                    Messages.showMessageDialog(myProject, "Connection is successful", "Connection", Messages.getInformationIcon());
                } else if (!(e instanceof ProcessCanceledException)) {
                    String message = e.getMessage();
                    if (e instanceof UnknownHostException) {
                        message = "Unknown host: " + message;
                    }

                    if (message == null) {
                        message = "Unknown error";
                    }

                    Messages.showErrorDialog(myProject, StringUtil.capitalize(message), "Error");
                }
            });
        });
    }

    protected void apply(){
        this.myChangeUrlListener.accept(myServer);
    }

    private void defaultServerChanged(){
        this.myChangeListener.accept(myServer, myDefaultServerCheckbox.isSelected());
    }

    @Nullable
    public ValidationInfo validate() {
        if (StringUtil.isEmpty(StringUtil.trim(myUrlField.getText()))) {
            return new ValidationInfo("Url is required.");
        }

        return null;
    }

}
