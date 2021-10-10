package com.intellij.jira.ui.renders;

import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.ui.JiraIssueStyleFactory;
import com.intellij.jira.ui.highlighters.JiraIssueHighlighter;
import com.intellij.jira.ui.highlighters.JiraIssueHighlighterFactory;
import com.intellij.jira.ui.highlighters.JiraIssueHighlighterProperty;
import com.intellij.jira.ui.table.JiraIssueTable;
import com.intellij.jira.ui.table.column.JiraIssueApplicationSettings;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.ColoredTableCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.TableCellState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import static com.intellij.jira.actions.HighlightersActionGroup.JIRA_ISSUE_HIGHLIGHTER_FACTORY_EP;
import static com.intellij.jira.util.JiraLabelUtil.getBgRowColor;
import static com.intellij.jira.util.JiraLabelUtil.getFgRowColor;

public class JiraIssueStringCellRenderer extends ColoredTableCellRenderer {

    private JiraIssue issue;

    public JiraIssueStringCellRenderer(JiraIssue issue) {
        this.issue = issue;
        setCellState(new BorderlessTableCellState());
    }

    @Override
    protected void customizeCellRenderer(@NotNull JTable table, @Nullable Object value, boolean selected, boolean hasFocus, int row, int column) {
        if (Objects.nonNull(value)) {

            JiraIssueTable myTable = (JiraIssueTable) table;
            // Get and apply selected styles
            Collection<JiraIssueHighlighter.JiraIssueStyle> styles = new ArrayList<>();
            for (JiraIssueHighlighterFactory factory : JIRA_ISSUE_HIGHLIGHTER_FACTORY_EP.getExtensionList()) {
                JiraIssueApplicationSettings properties = ApplicationManager.getApplication().getService(JiraIssueApplicationSettings.class);
                JiraIssueHighlighterProperty highlighterProperty = JiraIssueHighlighterProperty.get(factory.getId());
                if (properties.get(highlighterProperty)) {
                    JiraIssuesData jiraIssueData = new JiraIssuesData(myTable.getProject());
                    JiraIssueHighlighter highlighter = factory.createHighlighter(jiraIssueData);
                    styles.add(highlighter.getStyle(issue));
                }

            }

            JiraIssueHighlighter.JiraIssueStyle baseStyle = new BaseStyleProvider(table).getBaseStyle(row, column, hasFocus, selected);
            styles.add(baseStyle);

            JiraIssueHighlighter.JiraIssueStyle combinedStyle = JiraIssueStyleFactory.combine(styles);

            setBackground(combinedStyle.getBackground());
            setForeground(combinedStyle.getForeground());


            append(value.toString(), getTextAttributes(combinedStyle));
        }
    }



    private SimpleTextAttributes getTextAttributes(JiraIssueHighlighter.JiraIssueStyle style) {
        if (JiraIssueHighlighter.TextStyle.BOLD.equals(style.getTextStyle())) {
            return SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES;
        }

        return SimpleTextAttributes.REGULAR_ATTRIBUTES;
    }

    private class BaseStyleProvider {
        private JTable myTable;
        private final TableCellRenderer myDefaultCellRenderer = new DefaultTableCellRenderer();

        private BaseStyleProvider(JTable myTable) {
            this.myTable = myTable;
        }


        JiraIssueHighlighter.JiraIssueStyle getBaseStyle(int row, int column, boolean hasFocus, boolean selected) {
            Component component = myDefaultCellRenderer.getTableCellRendererComponent(myTable, "", selected, hasFocus, row, column);
            component.setBackground(getBgRowColor(selected));
            component.setForeground(getFgRowColor(selected));

            return JiraIssueStyleFactory.create(component.getForeground(), component.getBackground(), null);
        }

    }

    public static class BorderlessTableCellState extends TableCellState {
        @Override
        protected @Nullable Border getBorder(boolean isSelected, boolean hasFocus) {
            return null;
        }
    }

}
