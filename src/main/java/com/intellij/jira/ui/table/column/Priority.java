package com.intellij.jira.ui.table.column;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.JiraIssuePriority;
import com.intellij.jira.ui.renders.JiraIconAndTextTableCellRenderer;
import com.intellij.jira.util.JiraIconUtil;
import com.intellij.jira.util.JiraIssueUtil;
import com.intellij.util.ui.table.IconTableCellRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;

import static com.intellij.jira.util.JiraLabelUtil.EMPTY_TEXT;
import static com.intellij.jira.util.JiraLabelUtil.getBgRowColor;
import static java.util.Objects.nonNull;

public class Priority extends StringColumn<JiraIssue> {

    public static final Priority INSTANCE = new Priority();

    private final JiraIconAndTextTableCellRenderer myPriorityRenderer = new JiraIconAndTextTableCellRenderer();

    protected Priority() {
        super("Default.IssuePriority", "Priority");
    }

    @Override
    public TableCellRenderer getRenderer(JiraIssue issue) {
        return myPriorityRenderer;
    }

    @Override
    public String valueOf(JiraIssue issue) {
        return JiraIssueUtil.getPriority(issue);
    }

    @Override
    public TableCellRenderer getCustomizedRenderer(JiraIssue issue, TableCellRenderer renderer) {
        if(renderer instanceof JiraIconAndTextTableCellRenderer && nonNull(issue.getPriority())){
            ((JiraIconAndTextTableCellRenderer) renderer).setIconUrl(issue.getPriority().getIconUrl());
            ((JiraIconAndTextTableCellRenderer) renderer).emptyText();
            ((JiraIconAndTextTableCellRenderer) renderer).setToolTipText(valueOf(issue));
        }

        return renderer;
    }

    private static class MyPriorityRender extends IconTableCellRenderer<JiraIssuePriority> {

        @Override
        protected @Nullable Icon getIcon(@NotNull JiraIssuePriority value, JTable table, int row) {
            return JiraIconUtil.getIcon(value.getIconUrl());
        }

        @Override
        protected boolean isCenterAlignment() {
            return true;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int column) {
            super.getTableCellRendererComponent(table, value, selected, false, row, column);
            setText(EMPTY_TEXT);
            //setToolTipText(valuvalue.toString());
            setBackground(getBgRowColor(selected));

            return this;
        }
    }


}
