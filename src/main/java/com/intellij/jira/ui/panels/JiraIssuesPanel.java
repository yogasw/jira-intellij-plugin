package com.intellij.jira.ui.panels;

import com.google.common.util.concurrent.SettableFuture;
import com.intellij.jira.JiraUiDataKeys;
import com.intellij.jira.listener.JiraIssueChangeListener;
import com.intellij.jira.listener.JiraIssuesRefreshedListener;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.ui.JiraIssueActionPlaces;
import com.intellij.jira.ui.table.JiraIssueListTableModel;
import com.intellij.jira.ui.table.JiraIssueTable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Splitter;
import com.intellij.ui.OnePixelSplitter;
import com.intellij.ui.components.panels.Wrapper;
import com.intellij.util.messages.MessageBusConnection;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Future;

import static java.util.Objects.nonNull;

public class JiraIssuesPanel extends JiraPanel implements DataProvider {

    private final JComponent myToolbar;
    private final JiraIssueTable myJiraIssueTable;
    private final JiraIssueDetailsPanel myJiraIssueDetailsPanel;

    private Splitter myIssuesBrowserSplitter;

    public JiraIssuesPanel(@NotNull Project project, @NotNull List<JiraIssue> issues) {
        super(new BorderLayout());
        myToolbar = createActionsToolbar(project);

        myJiraIssueTable = new JiraIssueTable(issues);
        myJiraIssueDetailsPanel = new JiraIssueDetailsPanel(project);

        myJiraIssueTable.getSelectionModel()
                .addListSelectionListener(e -> {
                    myJiraIssueDetailsPanel.showIssue(myJiraIssueTable.getSelectedObject());
                    myJiraIssueDetailsPanel.setToolbarHeightReferent(myToolbar);
                });

        JComponent toolbarAndTable = new JPanel(new BorderLayout());
        toolbarAndTable.add(myToolbar, BorderLayout.NORTH);
        toolbarAndTable.add(myJiraIssueTable, BorderLayout.CENTER);

        myIssuesBrowserSplitter = new OnePixelSplitter(0.6f);
        myIssuesBrowserSplitter.setFirstComponent(toolbarAndTable);
        myIssuesBrowserSplitter.setSecondComponent(myJiraIssueDetailsPanel);

        add(myIssuesBrowserSplitter);

        MessageBusConnection connection = project.getMessageBus().connect();
        connection.subscribe(JiraIssueChangeListener.TOPIC, new OnIssueChanged());
        connection.subscribe(JiraIssuesRefreshedListener.TOPIC, new OnIssuesRefreshed());

    }

    @Override
    public @Nullable Object getData(@NotNull @NonNls String dataId) {
        if (JiraUiDataKeys.ISSUES_PANEL.is(dataId)) {
            return this;
        }

        return null;
    }

    @NotNull
    private JComponent createActionsToolbar(@NotNull Project project) {
        DefaultActionGroup toolbarGroup = new DefaultActionGroup();
        toolbarGroup.copyFromGroup((DefaultActionGroup) ActionManager.getInstance().getAction(JiraIssueActionPlaces.JIRA_ISSUES_TOOLBAR));

        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar(JiraIssueActionPlaces.JIRA_ISSUES_TOOLBAR, toolbarGroup, true);
        toolbar.setTargetComponent(this);

        Wrapper jqlFilter = new Wrapper(new JiraJQLSearcherPanel(project));
        jqlFilter.setVerticalSizeReferent(toolbar.getComponent());

        JPanel panel = new JPanel(new MigLayout("ins 0, fill", "[left]0[left, fill]push[pref:pref, right]", "center"));
        //GuiUtils.installVisibilityReferent(panel, toolbar.getComponent());
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


    private class OnIssueChanged implements JiraIssueChangeListener {

        @Override
        public void issueChanged(@NotNull JiraIssue issue) {
            if(nonNull(myJiraIssueTable)){
                JiraIssueListTableModel model = myJiraIssueTable.getModel();
                int postItem = model.indexOf(issue);
                if(postItem < 0){
                    return;
                }

                model.removeRow(postItem);
                model.insertRow(postItem, issue);
                myJiraIssueTable.addSelection(issue);
            }
        }
    }

    private class OnIssuesRefreshed implements JiraIssuesRefreshedListener {

        @Override
        public void issuesRefreshed(List<JiraIssue> issues) {
            if(nonNull(myJiraIssueTable)){
                JiraIssue lastSelectedIssue = myJiraIssueTable.getSelectedObject();
                ApplicationManager.getApplication().invokeLater(() -> {

                    myJiraIssueTable.updateModel(issues);
                    int currentPosIssue = myJiraIssueTable.getModel().indexOf(lastSelectedIssue);
                    // if the last selected issue exists in the new list, we select it
                    if(currentPosIssue >= 0) {
                        JiraIssue issueToShow = myJiraIssueTable.getModel().getItem(currentPosIssue);
                        myJiraIssueTable.addSelection(issueToShow);
                    } else {
                        myJiraIssueDetailsPanel.setEmptyContent();
                    }
                });
            }
        }
    }

}
