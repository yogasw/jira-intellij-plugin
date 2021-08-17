package com.intellij.jira.ui.panels;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.settings.ChangelistSettings;
import com.intellij.jira.util.ChangelistUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vcs.changes.LocalChangeList;
import com.intellij.ui.CollectionComboBoxModel;
import com.intellij.util.ui.FormBuilder;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.util.List;
import java.util.stream.Collectors;

public class JiraTransitionChangelistTaskPanel extends JiraPanel {

    private JCheckBox createChangelistCheckbox;
    private JCheckBox removeChangelistCheckbox;
    private JTextField changeListNameField;
    private JComboBox<String> changelistCombo;

    public JiraTransitionChangelistTaskPanel(Project project, JiraIssue issue) {
        super();
        createChangelistCheckbox = new JCheckBox();

        ChangelistSettings changelistSettings = ChangelistSettings.getInstance();
        String fieldValue = ChangelistUtil.getChangelistName(issue, changelistSettings);

        changeListNameField = new JTextField(300);
        changeListNameField.setText(fieldValue);

        JPanel newChangelistPanel = new JPanel();
        newChangelistPanel.add(createChangelistCheckbox);
        newChangelistPanel.add(FormBuilder.createFormBuilder().addLabeledComponent("New Changelist ", changeListNameField).getPanel());

        removeChangelistCheckbox = new JCheckBox();

        ChangeListManager changeListManager = ChangeListManager.getInstance(project);
        List<String> changeLists = changeListManager.getChangeLists().stream().map(LocalChangeList::getName).collect(Collectors.toList());
        new CollectionComboBoxModel<>(changeLists);
        changelistCombo = new ComboBox<>(new CollectionComboBoxModel<>(changeLists), 300);

        JPanel removeChangelistPanel = new JPanel();
        removeChangelistPanel.add(removeChangelistCheckbox);
        removeChangelistPanel.add(FormBuilder.createFormBuilder().addLabeledComponent("Remove Changelist ", changelistCombo).getPanel());


        JPanel panel1 = FormBuilder.createFormBuilder()
                .addComponent(newChangelistPanel)
                .addComponent(removeChangelistPanel)
                .getPanel();

        add(panel1);
    }

}
