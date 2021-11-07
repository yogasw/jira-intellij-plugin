package com.intellij.jira.ui.renders;

import com.intellij.jira.ui.table.JiraIssueTable;
import com.intellij.ui.ColoredTableCellRenderer;
import com.intellij.ui.TableCellState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JTable;
import javax.swing.border.Border;
import java.util.Objects;

public class JiraIssueStringCellRenderer extends ColoredTableCellRenderer {

    public JiraIssueStringCellRenderer() {
        setCellState(new BorderlessTableCellState());
    }

    @Override
    protected void customizeCellRenderer(@NotNull JTable table, @Nullable Object value, boolean selected, boolean hasFocus, int row, int column) {
        if (Objects.nonNull(value) && table instanceof JiraIssueTable) {

            JiraIssueTable myTable = (JiraIssueTable) table;
            append(value.toString(), myTable.applyHighlighter(this, selected, hasFocus, row, column));

        }
    }

    public static class BorderlessTableCellState extends TableCellState {
        @Override
        protected @Nullable Border getBorder(boolean isSelected, boolean hasFocus) {
            return null;
        }
    }

}
