package com.intellij.jira.ui.table;

import com.intellij.jira.helper.ColumnInfoHelper;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.ui.table.TableView;
import com.intellij.util.ui.JBUI;

import javax.swing.table.TableColumnModel;
import java.util.List;

import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

public class JiraIssueTableView extends TableView<JiraIssue> {

    private JiraIssueListTableModel model;

    public JiraIssueTableView(List<JiraIssue> issues) {
        super();
        model = new JiraIssueListTableModel(issues);
        setModelAndUpdateColumns(model);
        setSelectionMode(SINGLE_SELECTION);
        setIntercellSpacing(JBUI.emptySize());
        setShowGrid(false);
        setRowHeight(25);
        setTableHeader(new InvisibleResizableHeader());
    }


    @Override
    protected TableColumnModel createDefaultColumnModel() {
        TableColumnModel columnModel = super.createDefaultColumnModel();
        columnModel.setColumnMargin(0);
        return columnModel;
    }

    public void updateModel(List<JiraIssue> issues){
        model.setColumnInfos(ColumnInfoHelper.getHelper().generateColumnsInfo(issues));
        model.setItems(issues);
    }

    @Override
    public JiraIssueListTableModel getModel() {
        return model;
    }
}
