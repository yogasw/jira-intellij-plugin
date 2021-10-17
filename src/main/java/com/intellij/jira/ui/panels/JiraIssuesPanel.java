package com.intellij.jira.ui.panels;

import com.google.common.util.concurrent.SettableFuture;
import com.intellij.jira.JiraUiDataKeys;
import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.listener.RefreshIssuesListener;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.jira.ui.JiraIssueActionPlaces;
import com.intellij.jira.ui.table.JiraIssueTable;
import com.intellij.jira.ui.table.column.JiraIssueApplicationSettings;
import com.intellij.openapi.Disposable;
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
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.vcs.log.ui.frame.ProgressStripe;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Future;

public class JiraIssuesPanel extends JiraPanel implements DataProvider, Disposable {

    private final JComponent myToolbar;
    private final JQLSearcher mySearcher;
    private final JiraIssuesData myIssuesData;
    private final JiraIssueTable myJiraIssueTable;
    private final JiraIssueDetailsPanel myJiraIssueDetailsPanel;
    private final ProgressStripe myProgressStripe;

    private Splitter myIssuesBrowserSplitter;

    public JiraIssuesPanel(@NotNull JiraIssuesData issuesData, @NotNull JQLSearcher searcher) {
        super(new BorderLayout());

        myIssuesData = issuesData;
        mySearcher = searcher;
        myToolbar = getToolbar(issuesData.getProject());

        myJiraIssueTable = getIssueTable(issuesData, searcher);
        myJiraIssueDetailsPanel = new JiraIssueDetailsPanel(issuesData);

        myJiraIssueTable.getSelectionModel().addListSelectionListener(new MyListSelectionListener());

        JComponent toolbarAndTable = new JPanel(new BorderLayout());
        toolbarAndTable.add(myToolbar, getToolbarOrientation());
        JScrollPane scrollPane = ScrollPaneFactory.createScrollPane(myJiraIssueTable, true);
        myProgressStripe = new ProgressStripe(scrollPane, this, 1);

        toolbarAndTable.add(myProgressStripe, BorderLayout.CENTER);

        myIssuesBrowserSplitter = new OnePixelSplitter(0.6f);
        myIssuesBrowserSplitter.setFirstComponent(toolbarAndTable);
        myIssuesBrowserSplitter.setSecondComponent(myJiraIssueDetailsPanel);

        MessageBusConnection connection = issuesData.getProject().getMessageBus().connect();
        connection.subscribe(RefreshIssuesListener.TOPIC, new MyRefreshIssuesListener());

        add(myIssuesBrowserSplitter);
    }

    @NotNull
    protected String getToolbarOrientation() {
        return BorderLayout.NORTH;
    }

    protected void setToolbarHeightReference() {
        myJiraIssueDetailsPanel.setToolbarHeightReferent(myToolbar);
    }

    @NotNull
    protected JiraIssueTable getIssueTable(@NotNull JiraIssuesData issuesData, @NotNull JQLSearcher searcher) {
        return new JiraIssueTable(issuesData, searcher);
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

    @Override
    public void dispose() {

    }

    private class MyListSelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            myJiraIssueDetailsPanel.showIssue(myJiraIssueTable.getSelectedObject());
            setToolbarHeightReference();
        }
    }

    private class MyRefreshIssuesListener implements RefreshIssuesListener {

        @Override
        public void onRefresh() {
            myProgressStripe.startLoading();
            ApplicationManager.getApplication().invokeLater(() -> {
                JiraIssue lastSelectedIssue = myJiraIssueTable.getSelectedObject();
                myJiraIssueTable.updateSelectedSearcher();
                myJiraIssueTable.updateModelAndColumns();
                int currentPosIssue = myJiraIssueTable.getModel().indexOf(lastSelectedIssue);
                // if the last selected issue exists in the new list, we select it
                if (currentPosIssue >= 0) {
                    JiraIssue issueToShow = myJiraIssueTable.getModel().getItem(currentPosIssue);
                    myJiraIssueTable.addSelection(issueToShow);
                }

            });
            myProgressStripe.stopLoading();

        }
    }

}
