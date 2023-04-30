package com.intellij.jira.settings.branch.editor;

import com.intellij.ide.DataManager;
import com.intellij.jira.settings.branch.BranchSettings;
import com.intellij.jira.settings.branch.BranchSettingsState;
import com.intellij.jira.ui.editors.Editor;
import com.intellij.jira.util.JiraIssueField;
import com.intellij.jira.util.Separator;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.configuration.actions.IconWithTextAction;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.SeparatorFactory;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import java.util.List;

public class BranchSettingsEditor implements Editor {

    private final Project myProject;
    private final BranchSettings myBranchSettings;


    private JBList<String> myBranchPrefixList;
    private JRadioButton myDefaultBranchNameRadio;
    private JRadioButton myCustomBranchNameRadio;

    private JLabel myFieldSeparatorLabel;
    private ComboBox<Separator> myFieldSeparatorCombo;
    private JLabel myBranchNameLabel;
    private JBList<String> myBranchNameList;
    private JLabel myBranchNameExample;

    public BranchSettingsEditor(@Nullable Project project, BranchSettings branchSettings) {
        myProject = project;
        myBranchSettings = branchSettings;
    }

    @Override
    public JComponent createPanel() {

        // Prefix
        CollectionListModel<String> stringCollectionListModel = new CollectionListModel<>(myBranchSettings.getState().getBranchTypes());
        myBranchPrefixList = new JBList<>(stringCollectionListModel);
        myBranchPrefixList.setEmptyText("No prefixes");


        ToolbarDecorator prefixToolbarDecorator = ToolbarDecorator.createDecorator(myBranchPrefixList)
                                                                    .disableUpDownActions();

        prefixToolbarDecorator.setMinimumSize(JBUI.size(300, 40));
        prefixToolbarDecorator.setAddAction(anActionButton -> {
            new AddPrefixDialog(myProject).show();
        });

        prefixToolbarDecorator.setRemoveAction(anActionButton -> {
            String prefixToRemove = myBranchPrefixList.getSelectedValue();
            ((CollectionListModel<String>) myBranchPrefixList.getModel()).remove(prefixToRemove);
            myBranchPrefixList.doLayout();
            updateBranchNameExample();
        });

        // Branch name
        myDefaultBranchNameRadio = new JBRadioButton("Creates a branch using {issueKey} as name");
        if (!myBranchSettings.getState().isCustom()) {
            myDefaultBranchNameRadio.setSelected(true);
        }

        myCustomBranchNameRadio = new JBRadioButton("Creates a branch customizing the name");


        myFieldSeparatorLabel = new JBLabel("Separator: ");
        myFieldSeparatorCombo = new ComboBox<>(Separator.values());

        myFieldSeparatorCombo.addActionListener((event) -> {
            updateBranchNameExample();
        });

        myBranchNameLabel = new JBLabel("Name: ");
        CollectionListModel<String> availableIssueFields = new CollectionListModel<>(myBranchSettings.getState().getFieldNames());
        myBranchNameList = new JBList<>(availableIssueFields);
        myBranchNameList.setEmptyText("No fields");

        if (myBranchSettings.getState().isCustom()) {
            myCustomBranchNameRadio.setSelected(true);
            myFieldSeparatorCombo.setSelectedItem(myBranchSettings.getState().getFieldSeparator());
        }

        ToolbarDecorator branchNameToolbarDecorator = ToolbarDecorator.createDecorator(myBranchNameList)
                                                                        .disableUpDownActions();

        branchNameToolbarDecorator.setMinimumSize(JBUI.size(300, 40));
        branchNameToolbarDecorator.setAddAction(anActionButton -> {
            DefaultActionGroup group = new DefaultActionGroup();
            group.add(new AddFieldAction(JiraIssueField.KEY));
            group.add(new AddFieldAction(JiraIssueField.SUMMARY));
            group.add(new AddFieldAction("projectKey"));

            JBPopupFactory.getInstance()
                    .createActionGroupPopup("Add Field", group, DataManager.getInstance().getDataContext(anActionButton.getContextComponent()), JBPopupFactory.ActionSelectionAid.SPEEDSEARCH, true)
                    .show(anActionButton.getPreferredPopupPoint());

        });


        myBranchNameExample = new JBLabel("e.g: " + getBranchNameExample() );

        return FormBuilder.createFormBuilder()
                .addComponent(SeparatorFactory.createSeparator("Prefix", null))
                .addComponent(prefixToolbarDecorator.createPanel())
                .addComponent(SeparatorFactory.createSeparator("Name", null))
                .addComponent(myDefaultBranchNameRadio)
                .addComponent(myCustomBranchNameRadio)
                .addLabeledComponent(myFieldSeparatorLabel, myFieldSeparatorCombo)
                .addLabeledComponent(myBranchNameLabel, branchNameToolbarDecorator.createPanel())
                .addComponentToRightColumn(myBranchNameExample)
                .getPanel();
    }

    @Override
    public @Nullable ValidationInfo validate() {
        if (myBranchNameList.isEmpty()) {
            return new ValidationInfo("You must select at least one field");
        }

        return null;
    }

    public BranchSettingsState getBranchSettingsState() {
        List<String> branchTypes = ((CollectionListModel<String>) myBranchPrefixList.getModel()).getItems();
        if (myDefaultBranchNameRadio.isSelected()) {
            return BranchSettingsState.getDefault(branchTypes);
        }

        Separator separator = (Separator) myFieldSeparatorCombo.getSelectedItem();
        List<String> fieldNames = ((CollectionListModel<String>) myBranchNameList.getModel()).getItems();

        return BranchSettingsState.getCustom(branchTypes, separator, fieldNames);
    }

    private String getBranchNameExample() {
        Separator separator = (Separator) myFieldSeparatorCombo.getSelectedItem();
        String prefix = myBranchPrefixList.isEmpty() ? "" : "{prefix}/";
        List<String> fieldsNames = ((CollectionListModel<String>) myBranchNameList.getModel()).getItems()
                .stream()
                .map(fieldName -> String.format("{%s}", fieldName))
                .toList();

        return prefix + StringUtil.join(fieldsNames, separator.getSeparator());
    }

    private void updateBranchNameExample() {
        myBranchNameExample.setText("e.g: " + getBranchNameExample());
    }

    private class AddFieldAction extends IconWithTextAction implements DumbAware {

        private final String field;

        private AddFieldAction(String field) {
            super(field);
            this.field = field;
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            ((CollectionListModel<String>) myBranchNameList.getModel()).add(field);
            myBranchNameList.setSelectedIndex(myBranchNameList.getModel().getSize() - 1);
            myBranchNameList.doLayout();
            updateBranchNameExample();
        }
    }


    private class AddPrefixDialog extends DialogWrapper {

        private JTextField myPrefixField;

        protected AddPrefixDialog(@Nullable Project project) {
            super(project, false);

            setTitle("Add Prefix");
            init();
        }

        @Nullable
        @Override
        protected JComponent createCenterPanel() {
            myPrefixField = new JTextField();

            return FormBuilder.createFormBuilder()
                    .addLabeledComponent("Name: ", myPrefixField)
                    .getPanel();
        }


        @Nullable
        @Override
        protected ValidationInfo doValidate() {
            if (StringUtil.trim(myPrefixField.getText()).isEmpty()) {
                return new ValidationInfo("Prefix is required");
            }

            if (myPrefixField.getText().split(" ").length > 1) {
                return new ValidationInfo("Prefix must be one word");
            }

            return null;
        }

        @Override
        protected void doOKAction() {
            String prefix = StringUtil.trim(myPrefixField.getText());
            ((CollectionListModel<String>) myBranchPrefixList.getModel()).add(prefix);
            myBranchPrefixList.doLayout();

            super.doOKAction();
        }
    }

}
