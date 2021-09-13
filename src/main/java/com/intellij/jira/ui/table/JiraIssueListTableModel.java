package com.intellij.jira.ui.table;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.ui.table.column.JiraIssueColumn;
import com.intellij.jira.ui.table.column.JiraIssueColumnManager;
import com.intellij.jira.ui.table.column.JiraIssueColumnUtils;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ListTableModel;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class JiraIssueListTableModel extends ListTableModel<JiraIssue> {

    public JiraIssueListTableModel(@NotNull List<JiraIssue> issues) {
        super();

        initializeColumnsWidth(issues);
        setColumnInfos(getIssueColumns());
        setItems(issues);
    }

    public void initializeColumnsWidth(List<JiraIssue> issues) {
        issues.forEach(issue -> {
            for (JiraIssueColumn<?, ?> column : getJiraIssueColumnManager().getCurrentColumns()) {
                String columnWidth = getJiraIssueColumnManager().getColumnWidth(column.getId());
                String valueColumnWidth = ((JiraIssueColumn<JiraIssue, String>) column).valueOf(issue);
                if (StringUtil.length(columnWidth) < StringUtil.length(valueColumnWidth)) {
                    getJiraIssueColumnManager().setColumnWidth(column.getId(), valueColumnWidth);
                }
            }
        });
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }


    private JiraIssueColumnManager getJiraIssueColumnManager() {
        return JiraIssueColumnManager.getInstance();
    }

    private ColumnInfo<?, ?>[] getIssueColumns() {
        return JiraIssueColumnUtils.getVisibleColumns().toArray(new ColumnInfo[0]);
    }

}
