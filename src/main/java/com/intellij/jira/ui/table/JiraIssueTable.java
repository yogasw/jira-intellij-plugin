package com.intellij.jira.ui.table;

import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.jql.JQLSearcherManager;
import com.intellij.jira.listener.IssueChangeListener;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.jira.ui.JiraIssueStyleFactory;
import com.intellij.jira.ui.highlighters.JiraIssueHighlighter;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.table.TableView;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;

import static com.intellij.jira.util.JiraLabelUtil.getBgRowColor;
import static com.intellij.jira.util.JiraLabelUtil.getFgRowColor;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

public class JiraIssueTable extends TableView<JiraIssue> implements Disposable {

    private final JiraIssuesData myIssuesData;
    private JQLSearcher mySearcher;
    private final Collection<JiraIssueHighlighter> myHighlighters = new LinkedHashSet<>();
    private final BaseStyleProvider myBaseStyleProvider;

    public JiraIssueTable(@NotNull JiraIssuesData issuesData, @NotNull JQLSearcher searcher, @NotNull Disposable parent) {
        super(new JiraIssueListTableModel(issuesData, searcher));

        Disposer.register(parent, this);

        myIssuesData = issuesData;
        mySearcher = searcher;

        myBaseStyleProvider = new BaseStyleProvider(this);

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

    public void updateSelectedSearcher() {
        mySearcher = JQLSearcherManager.getInstance().getSelectedSearcher(getProject());
    }

    public void updateModelAndColumns() {
        setModelAndUpdateColumns(new JiraIssueListTableModel(myIssuesData, mySearcher));
    }

    public void addHighlighter(JiraIssueHighlighter highlighter) {
        myHighlighters.add(highlighter);
    }

    public void removeHighlighter(JiraIssueHighlighter highlighter) {
        myHighlighters.remove(highlighter);
    }

    public SimpleTextAttributes applyHighlighter(@NotNull Component component, boolean selected, boolean hasFocus, int row, int column) {
        Collection<JiraIssueHighlighter.JiraIssueStyle> styles = new ArrayList<>();

        JiraIssue issue = getRow(row);
        myHighlighters.forEach(highlighter -> styles.add(highlighter.getStyle(issue)));

        JiraIssueHighlighter.JiraIssueStyle baseStyle = myBaseStyleProvider.getBaseStyle(row, column, hasFocus, selected);
        styles.add(baseStyle);

        JiraIssueHighlighter.JiraIssueStyle combinedStyle = JiraIssueStyleFactory.combine(styles);

        component.setBackground(combinedStyle.getBackground());
        component.setForeground(combinedStyle.getForeground());

        return getTextAttributes(combinedStyle);
    }

    @Override
    public void dispose() {
        myHighlighters.clear();
    }

    private SimpleTextAttributes getTextAttributes(JiraIssueHighlighter.JiraIssueStyle style) {
        if (JiraIssueHighlighter.TextStyle.BOLD.equals(style.getTextStyle())) {
            return SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES;
        }

        return SimpleTextAttributes.REGULAR_ATTRIBUTES;
    }

    private void subscribeTopics() {
        MessageBusConnection connection = myIssuesData.getProject().getMessageBus().connect();

        connection.subscribe(IssueChangeListener.TOPIC, new OnIssueChanged());
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


    private class OnIssueChanged implements IssueChangeListener {

        @Override
        public void onChange(@NotNull JiraIssue issue) {
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
