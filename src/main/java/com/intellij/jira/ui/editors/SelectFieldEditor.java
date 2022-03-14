package com.intellij.jira.ui.editors;

import com.intellij.ide.DataManager;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.server.JiraServerManager;
import com.intellij.jira.ui.panels.JiraPanel;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBList;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.util.List;
import java.util.Objects;

import static java.awt.BorderLayout.CENTER;
import static java.util.Objects.nonNull;
import static javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

public abstract class SelectFieldEditor<T> extends AbstractFieldEditor<T> {

    protected JPanel myPanel;
    protected JTextField myTextField;
    protected JButton myButton;
    protected PickerDialogAction myButtonAction;
    protected boolean myIsMultiSelect;

    public SelectFieldEditor(String fieldName, Object fieldValue, boolean required, boolean isMultiSelect) {
        super(fieldName, fieldValue, required);
        myIsMultiSelect = isMultiSelect;
    }

    @Override
    public JComponent createPanel() {
        myButton.addActionListener(e -> {
            InputEvent inputEvent = e.getSource() instanceof InputEvent ? (InputEvent)e.getSource() : null;
            myButtonAction.actionPerformed(AnActionEvent.createFromAnAction(myButtonAction, inputEvent, ActionPlaces.POPUP, DataManager.getInstance().getDataContext(myTextField)));
        });

        return FormBuilder.createFormBuilder()
                .addLabeledComponent(myLabel, myPanel, true)
                .getPanel();
    }

    @Nullable
    @Override
    public ValidationInfo validate() {
        if(isRequired() && StringUtil.isEmpty(myTextField.getText())){
            return new ValidationInfo(myLabel.getText() + " is required.");
        }

        return null;
    }

    abstract class PickerDialogAction extends AnAction{

        protected JiraRestApi myJiraRestApi;
        protected Project myProject;

        public PickerDialogAction() {
            super();
        }

        @Override
        public void actionPerformed(AnActionEvent e) {
            Project project = e.getProject();
            if(nonNull(project)){
                myProject = project;
                myJiraRestApi = ApplicationManager.getApplication().getService(JiraServerManager.class).getJiraRestApi(project);
            }
        }
    }


    abstract class PickerDialog<E> extends DialogWrapper {

        protected JBList<E> myList;

        public PickerDialog(@Nullable Project project, @NotNull String title, List<E> items, List<E> selectedItems) {
            super(project, false);
            setTitle(title);
            myList = new JBList(items);
            myList.setPreferredSize(getPreferredSizeList());
            myList.setSelectionMode(myIsMultiSelect ? MULTIPLE_INTERVAL_SELECTION: SINGLE_SELECTION);
            if (Objects.nonNull(selectedItems)) {
                IntArrayList selectedIndices = new IntArrayList();
                for (E selectedItem : selectedItems) {
                    int pos = items.indexOf(selectedItem);
                    if (pos > -1) {
                        selectedIndices.add(pos);
                    }
                }

                if (!selectedIndices.isEmpty()) {
                    myList.setSelectedIndices(selectedIndices.toIntArray());
                }
            }

            init();
        }

        public Dimension getPreferredSizeList(){
            return JBUI.size(75, 250);
        }

        @Nullable
        @Override
        protected JComponent createCenterPanel() {
            JPanel panel = new JiraPanel(new BorderLayout());
            panel.setPreferredSize(JBUI.size(100, 250));
            panel.add(ScrollPaneFactory.createScrollPane(myList, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED), CENTER);

            return panel;
        }

    }

}
