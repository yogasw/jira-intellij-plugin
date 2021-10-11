package com.intellij.jira.ui.panels;

import com.google.common.util.concurrent.SettableFuture;
import com.intellij.jira.JiraUiDataKeys;
import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.ui.JiraIssueActionPlaces;
import com.intellij.jira.ui.table.JiraIssueTable;
import com.intellij.jira.ui.table.column.JiraIssueApplicationSettings;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Splitter;
import com.intellij.ui.OnePixelSplitter;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.panels.Wrapper;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Future;

public class JiraIssuesPanel extends JiraPanel implements DataProvider {

    private final JComponent myToolbar;
    private final JiraIssueTable myJiraIssueTable;
    private final JiraIssueDetailsPanel myJiraIssueDetailsPanel;

    private Splitter myIssuesBrowserSplitter;

    public JiraIssuesPanel(@NotNull JiraIssuesData issuesData) {
        super(new BorderLayout());

        myToolbar = getToolbar(issuesData.getProject());

        myJiraIssueTable = new JiraIssueTable(issuesData);
        myJiraIssueDetailsPanel = new JiraIssueDetailsPanel(issuesData);

        myJiraIssueTable.getSelectionModel().addListSelectionListener(new MyListSelectionListener());

        JComponent toolbarAndTable = new JPanel(new BorderLayout());
        toolbarAndTable.add(myToolbar, getToolbarOrientation());
        toolbarAndTable.add(ScrollPaneFactory.createScrollPane(myJiraIssueTable, true), BorderLayout.CENTER);

        myIssuesBrowserSplitter = new OnePixelSplitter(0.6f);
        myIssuesBrowserSplitter.setFirstComponent(toolbarAndTable);
        myIssuesBrowserSplitter.setSecondComponent(myJiraIssueDetailsPanel);

        add(myIssuesBrowserSplitter);
    }

    @NotNull
    protected String getToolbarOrientation() {
        return BorderLayout.NORTH;
    }

    protected void setToolbarHeightReference() {
        myJiraIssueDetailsPanel.setToolbarHeightReferent(myToolbar);
    }

    @Override
    public @Nullable Object getData(@NotNull @NonNls String dataId) {
        if (JiraUiDataKeys.ISSUES_PANEL.is(dataId)) {
            return this;
        } else if (JiraUiDataKeys.JIRA_UI_PROPERTIES.is(dataId)) {
            return ApplicationManager.getApplication().getService(JiraIssueApplicationSettings.class);
        }

        return null;
    }

    @NotNull
    protected JComponent getToolbar(@NotNull Project project) {
        DefaultActionGroup toolbarGroup = new DefaultActionGroup();
        toolbarGroup.copyFromGroup((DefaultActionGroup) ActionManager.getInstance().getAction(JiraIssueActionPlaces.JIRA_ISSUES_TOOLBAR));

        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar(JiraIssueActionPlaces.JIRA_ISSUES_TOOLBAR, toolbarGroup, true);
        toolbar.setTargetComponent(this);

        Wrapper jqlFilter = new Wrapper(new JiraJQLSearcherPanel(project));
        jqlFilter.setVerticalSizeReferent(toolbar.getComponent());

        JPanel panel = new JPanel(new MigLayout("ins 0, fill", "[left]0[left, fill]push[pref:pref, right]", "center"));
        panel.add(jqlFilter);
        panel.add(toolbar.getComponent());

        return panel;
    }

    public JiraIssueTable getJiraIssueTable() {
        return myJiraIssueTable;
    }

    public Future goToIssue(String issueKey) {
        SettableFuture<Boolean> future = SettableFuture.create();
        future.set(false);
        Optional<JiraIssue> targetIssue = myJiraIssueTable.getItems().stream().filter(issue -> Objects.equals(issue.getKey(), issueKey)).findFirst();
        if(targetIssue.isPresent()){
            future.set(true);
            myJiraIssueTable.addSelection(targetIssue.get());
            myJiraIssueTable.scrollRectToVisible(myJiraIssueTable.getCellRect(myJiraIssueTable.getSelectedRow(), myJiraIssueTable.getSelectedColumn(), true));
            myJiraIssueDetailsPanel.showIssue(targetIssue.get());
        }

        return future;
    }

    private class MyListSelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            myJiraIssueDetailsPanel.showIssue(myJiraIssueTable.getSelectedObject());
            setToolbarHeightReference();
        }
    }

}
