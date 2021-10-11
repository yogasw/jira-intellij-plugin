package com.intellij.jira.ui.table;

import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.listener.IssueChangeListener;
import com.intellij.jira.listener.RefreshIssuesListener;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.ui.JiraIssueUiProperties;
import com.intellij.jira.ui.table.column.JiraIssueApplicationSettings;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.ui.table.TableView;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.table.TableColumnModel;

import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

public class JiraIssueTable extends TableView<JiraIssue> {

    private final JiraIssuesData myIssuesData;

    public JiraIssueTable(@NotNull JiraIssuesData issuesData) {
        super(new JiraIssueListTableModel(issuesData));

        myIssuesData = issuesData;

        setBorder(JBUI.Borders.customLine(JBColor.border(),1, 0, 0, 0));
        setShowGrid(false);
        setSelectionMode(SINGLE_SELECTION);
        setIntercellSpacing(JBUI.emptySize());
        setRowHeight(25);
        setTableHeader(new InvisibleResizableHeader() {
            @Override
            protected boolean canMoveOrResizeColumn(int modelIndex) {
                return false;
            }
        });

        subscribeTopics();

        ApplicationManager.getApplication().getService(JiraIssueApplicationSettings.class)
                .addChangeListener(new MyPropertyChangeListener());
    }

    private void subscribeTopics() {
        MessageBusConnection connection = myIssuesData.getProject().getMessageBus().connect();

        connection.subscribe(RefreshIssuesListener.TOPIC, new OnRefreshIssues());
        connection.subscribe(IssueChangeListener.TOPIC, new OnIssueChanged());
    }

    @Override
    protected TableColumnModel createDefaultColumnModel() {
        TableColumnModel columnModel = super.createDefaultColumnModel();
        columnModel.setColumnMargin(0);

        return columnModel;
    }

    @Override
    public JiraIssueListTableModel getModel() {
        return (JiraIssueListTableModel) dataModel;
    }

    public Project getProject() {
        return myIssuesData.getProject();
    }

    private class MyPropertyChangeListener implements JiraIssueUiProperties.PropertyChangeListener {

        @Override
        public <T> void onChanged(JiraIssueUiProperties.@NotNull JiraIssueUiProperty<T> property) {
            setModelAndUpdateColumns(new JiraIssueListTableModel(myIssuesData));
        }
    }

    private class OnIssueChanged implements IssueChangeListener {

        @Override
        public void onChange(String issueKey) {
            JiraIssue issue = myIssuesData.getIssue(issueKey);
            if (issue != null) {
                JiraIssueListTableModel model = getModel();
                int postItem = model.indexOf(issue);
                if (postItem < 0) {
                    return;
                }

                model.removeRow(postItem);
                model.insertRow(postItem, issue);
                addSelection(issue);
            }
        }
    }

    private class OnRefreshIssues implements RefreshIssuesListener {

        @Override
        public void onRefresh() {
            JiraIssue lastSelectedIssue = getSelectedObject();
            ApplicationManager.getApplication().invokeLater(() -> {
                setModelAndUpdateColumns(new JiraIssueListTableModel(myIssuesData));
                int currentPosIssue = getModel().indexOf(lastSelectedIssue);
                // if the last selected issue exists in the new list, we select it
                if (currentPosIssue >= 0) {
                    JiraIssue issueToShow = getModel().getItem(currentPosIssue);
                    addSelection(issueToShow);
                }
            });

        }
    }

}
