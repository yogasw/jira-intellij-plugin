package com.intellij.jira.ui.dialog;

import com.intellij.jira.components.JQLSearcherManager;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.jira.tasks.RefreshIssuesTask;
import com.intellij.jira.util.SimpleSelectableList;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.table.TableView;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ListTableModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;

import static com.intellij.jira.util.JiraLabelUtil.getBgRowColor;
import static com.intellij.jira.util.JiraLabelUtil.getFgRowColor;
import static com.intellij.openapi.ui.Messages.CANCEL_BUTTON;
import static com.intellij.openapi.ui.Messages.OK_BUTTON;

public class ConfigureJQLSearchersDialog extends DialogWrapper {

    private final Project myProject;
    private final JQLSearcherManager myManager;

    private SimpleSelectableList<JQLSearcher> mySearchers;

    private final ColumnInfo<JQLSearcher, String> ALIAS_COLUMN = new AliasColumnInfo();
    private final ColumnInfo<JQLSearcher, String> JQL_COLUMN = new JQLColumnInfo();
    private final ColumnInfo<JQLSearcher, String> SHARED_COLUMN = new SharedrColumnInfo();


    private TableView<JQLSearcher> myTable;
    private ListTableModel<JQLSearcher> myModel;

    public ConfigureJQLSearchersDialog(@NotNull Project project) {
        super(project, false);
        this.myProject = project;
        this.myManager = JQLSearcherManager.getInstance();

        init();
    }


    @Override
    protected void init() {
        mySearchers = new SimpleSelectableList<>();

        myModel = new ListTableModel(new ColumnInfo[]{ALIAS_COLUMN, JQL_COLUMN, SHARED_COLUMN}, new ArrayList());
        for(JQLSearcher searcher : myManager.getSearchers(myProject)){
            JQLSearcher clone = searcher.clone();
            mySearchers.add(clone);
            myModel.addRow(clone);
        }

        mySearchers.selectItem(myManager.getSelectedSearcherIndex(myProject));
        myTable = new TableView<>(myModel);


        setTitle("Configure JQL Searcher");
        super.init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JBPanel myPanel = new JBPanel(new BorderLayout());
        myPanel.add(ToolbarDecorator.createDecorator(myTable)
                        .setAddAction(button -> {
                            NewJQLSearcherDialog dlg = new NewJQLSearcherDialog(myProject, false);
                            if (dlg.showAndGet()) {
                                JQLSearcher newJqlSearcher = dlg.getJqlSearcher();
                                newJqlSearcher.setShared(dlg.isSharedSearcher());
                                mySearchers.add(newJqlSearcher, dlg.isSelectedSearcher());
                                myModel.addRow(newJqlSearcher);
                                myModel.fireTableDataChanged();
                            }
                        })
                        .setEditAction(button -> {
                            int selRow = myTable.getSelectedRow();
                            boolean isDefaultSearcher = (selRow == mySearchers.getSelectedItemIndex());
                            JQLSearcher selectedSearcher = getSelectedJQLSearcher();
                            EditJQLSearcherDialog dlg = new EditJQLSearcherDialog(myProject, selectedSearcher, isDefaultSearcher, false);

                            if (dlg.showAndGet()) {
                                selectedSearcher.setShared(dlg.isSharedSearcher());
                                mySearchers.update(selRow, selectedSearcher, dlg.isSelectedSearcher());
                                myModel.fireTableDataChanged();
                            }
                        })
                        .setRemoveAction(button -> {
                            if (Messages.showOkCancelDialog(myProject, "You are going to delete this searcher, are you sure?","Delete Searcher", OK_BUTTON, CANCEL_BUTTON, Messages.getQuestionIcon()) == Messages.OK) {

                                mySearchers.remove(myTable.getSelectedRow());
                                myModel.removeRow(myTable.getSelectedRow());
                                myModel.fireTableDataChanged();
                            }
                        })
                        .disableUpDownActions().createPanel(), BorderLayout.CENTER);

        return myPanel;
    }



    private JQLSearcher getSelectedJQLSearcher(){
        return myModel.getItem(myTable.getSelectedRow());
    }

    @Override
    protected void doOKAction() {
        myManager.setSearchers(myProject, mySearchers);
        new RefreshIssuesTask(myProject).queue();

        super.doOKAction();
    }





    private class JQLSearcherTableCellRenderer extends DefaultTableCellRenderer{

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if(row == mySearchers.getSelectedItemIndex()){
                setBackground(getBgRowColor(isSelected));
                setForeground(getFgRowColor(isSelected));
            }
            else{
                setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
            }

            return this;
        }
    }


    private abstract class BaseColumnInfo extends ColumnInfo<JQLSearcher, String>{

        private final JQLSearcherTableCellRenderer JQL_SEARCHER_RENDERER = new JQLSearcherTableCellRenderer();

        public BaseColumnInfo(String name) {
            super(name);
        }

        @Nullable
        @Override
        public TableCellRenderer getRenderer(JQLSearcher jqlSearcher) {
            return JQL_SEARCHER_RENDERER;
        }
    }


    private class AliasColumnInfo extends BaseColumnInfo {

        public AliasColumnInfo() {
            super("Alias");
        }

        @Nullable
        @Override
        public String valueOf(JQLSearcher jqlSearcher) {
            return jqlSearcher.getAlias();
        }
    }

    private class JQLColumnInfo extends BaseColumnInfo {

        public JQLColumnInfo() {
            super("JQL");
        }

        @Nullable
        @Override
        public String valueOf(JQLSearcher jqlSearcher) {
            return jqlSearcher.getJql();
        }
    }

    private class SharedrColumnInfo extends BaseColumnInfo {

        public SharedrColumnInfo() {
            super("Shared");
        }

        @Nullable
        @Override
        public String valueOf(JQLSearcher jqlSearcher) {
            return jqlSearcher.isShared() ? "Yes" : "No";
        }
    }



}
