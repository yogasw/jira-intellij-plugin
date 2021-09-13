package com.intellij.jira.ui.table;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.ui.JiraIssueUiProperties;
import com.intellij.jira.ui.table.column.JiraIssueApplicationSettings;
import com.intellij.jira.ui.table.column.JiraIssueColumnUtils;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.JBColor;
import com.intellij.ui.table.TableView;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.table.TableColumnModel;
import java.util.List;

import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

public class JiraIssueTable extends TableView<JiraIssue> {

    public JiraIssueTable(@NotNull List<JiraIssue> issues) {
        super(new JiraIssueListTableModel(issues));

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

        JiraIssueUiProperties.PropertyChangeListener myListener = new JiraIssueUiProperties.PropertyChangeListener() {
            @Override
            public <T> void onChanged(JiraIssueUiProperties.@NotNull JiraIssueUiProperty<T> property) {
                updateColumns();
            }
        };

        ApplicationManager.getApplication().getService(JiraIssueApplicationSettings.class).addChangeListener(myListener);
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

    public void updateModel(List<JiraIssue> issues) {
        getModel().initializeColumnsWidth(issues);
        updateColumns();
        getModel().setItems(issues);
    }

    private void updateColumns() {
        getModel().setColumnInfos(JiraIssueColumnUtils.getVisibleColumns().toArray(new ColumnInfo[0]));
    }

}
