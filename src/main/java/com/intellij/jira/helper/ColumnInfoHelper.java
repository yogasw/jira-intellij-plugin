package com.intellij.jira.helper;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.ui.renders.JiraIconAndTextTableCellRenderer;
import com.intellij.jira.ui.renders.JiraIssueStatusTableCellRenderer;
import com.intellij.jira.ui.renders.JiraIssueTableCellRenderer;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;

import java.util.List;

import static com.intellij.jira.util.JiraIssueUtil.*;
import static java.util.Objects.nonNull;

public class ColumnInfoHelper {

    private static final String KEY_COLUMN = "Key";
    private static final String SUMMARY_COLUMN = "Summary";
    private static final String ASSIGNEE_COLUMN = "Assignee";
    private static final String TYPE_COLUMN = "Type";
    private static final String PRIORITY_COLUMN = "Priority";
    private static final String STATUS_COLUMN = "Status";
    private static final String CREATED_COLUMN = "Created";

    private static ColumnInfoHelper helper;

    private ColumnInfoHelper(){ }

    @NotNull
    public static ColumnInfoHelper getHelper(){
        if (helper == null){
            helper = new ColumnInfoHelper();
        }

        return helper;
    }

    @NotNull
    public ColumnInfo[] generateColumnsInfo(List<JiraIssue> issues) {
        return new ColumnInfo[]{ new KeyColumnInfo(issues),
                                new SummaryColumnInfo(),
                                new AssigneeColumnInfo(),
                                new IssueTypeColumnInfo(),
                                new PriorityColumnInfo(),
                                new StatusColumnInfo(),
                                new CreatedColumnInfo()};
    }


    private abstract static class AbstractColumnInfo extends ColumnInfo<JiraIssue, String>{
        private static final TableCellRenderer ICON_AND_TEXT_RENDERER = new JiraIconAndTextTableCellRenderer();
        private String columnName;

        AbstractColumnInfo(String name) {
            super(name);
            this.columnName = name;
        }

        @Nullable
        @Override
        public String getMaxStringValue() {
            return columnName;
        }

        @Nullable
        @Override
        public TableCellRenderer getRenderer(JiraIssue issue) {
            return ICON_AND_TEXT_RENDERER;
        }


    }

    private abstract static class JiraIssueColumnInfo extends ColumnInfo<JiraIssue, String> {
        private static final JiraIssueTableCellRenderer JIRA_ISSUE_RENDERER = new JiraIssueTableCellRenderer();

        JiraIssueColumnInfo(@NotNull String name) {
            super(name);
        }

        @Nullable
        @Override
        public TableCellRenderer getRenderer(JiraIssue issue) {
            return JIRA_ISSUE_RENDERER;
        }

    }

    private static class KeyColumnInfo extends JiraIssueColumnInfo{

        private String myMaxString = "";

        KeyColumnInfo(List<JiraIssue> issues) {
            super(KEY_COLUMN);

            for (JiraIssue issue : issues) {
                if (issue.getKey().length() > myMaxString.length()) {
                    this.myMaxString = issue.getKey();
                }
            }
        }

        @Nullable
        @Override
        public String valueOf(JiraIssue issue) {
            return issue.getKey();
        }

        @Nullable
        @Override
        public String getMaxStringValue() {
            return this.myMaxString;
        }

        @Override
        public int getAdditionalWidth() {
            return UIUtil.DEFAULT_HGAP;
        }

        @Override
        public TableCellRenderer getCustomizedRenderer(JiraIssue o, TableCellRenderer renderer) {
            if(renderer instanceof JiraIssueTableCellRenderer){
                ((JiraIssueTableCellRenderer) renderer).setHorizontalAlignment(SwingUtilities.LEFT);
            }
            return renderer;
        }
    }

    private static class SummaryColumnInfo extends JiraIssueColumnInfo {

        SummaryColumnInfo() {
            super(SUMMARY_COLUMN);
        }

        @Nullable
        @Override
        public String valueOf(JiraIssue issue) {
            return issue.getSummary();
        }

        @Nullable
        @Override
        public String getMaxStringValue() {
            return "";
        }

        @Override
        public int getAdditionalWidth() {
            return 400;
        }
    }

    private static class AssigneeColumnInfo extends JiraIssueColumnInfo {

        AssigneeColumnInfo() {
            super(ASSIGNEE_COLUMN);
        }

        @Nullable
        @Override
        public String valueOf(JiraIssue issue) {
            return getAssignee(issue);
        }

        @Nullable
        @Override
        public String getMaxStringValue() {
            return "";
        }

        @Override
        public int getAdditionalWidth() {
            return 70;
        }

    }

    private static class IssueTypeColumnInfo extends JiraIssueColumnInfo{

        IssueTypeColumnInfo() {
            super(TYPE_COLUMN);
        }

        @Nullable
        @Override
        public String valueOf(JiraIssue issue) {
            return getIssueType(issue);
        }

    }

    private static class PriorityColumnInfo extends AbstractColumnInfo{

        PriorityColumnInfo() {
            super(PRIORITY_COLUMN);
        }

        @Nullable
        @Override
        public String valueOf(JiraIssue issue) {
            return getPriority(issue);
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
    }

    private static class StatusColumnInfo extends JiraIssueColumnInfo{

        StatusColumnInfo() {
            super(STATUS_COLUMN);
        }

        @Nullable
        @Override
        public String valueOf(JiraIssue issue) {
            return getStatus(issue);
        }

        @Nullable
        @Override
        public TableCellRenderer getRenderer(JiraIssue issue) {
            return new JiraIssueStatusTableCellRenderer(issue.getStatus().getName(), issue.getStatus().getCategoryColor(), issue.getStatus().isInProgressCategory());
        }

    }

    private static class CreatedColumnInfo extends JiraIssueColumnInfo{

        CreatedColumnInfo() {
            super(CREATED_COLUMN);
        }

        @Nullable
        @Override
        public String valueOf(JiraIssue issue) {
            return getCreated(issue);
        }

        @Override
        public TableCellRenderer getCustomizedRenderer(JiraIssue o, TableCellRenderer renderer) {
            if(renderer instanceof JiraIssueTableCellRenderer){
                ((JiraIssueTableCellRenderer) renderer).setHorizontalAlignment(SwingUtilities.RIGHT);
            }
            return renderer;
        }

    }

}
