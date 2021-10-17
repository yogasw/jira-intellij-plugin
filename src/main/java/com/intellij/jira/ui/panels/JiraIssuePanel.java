package com.intellij.jira.ui.panels;

import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.listener.RefreshIssuesListener;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.util.JiraPanelUtil;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.OnePixelSplitter;
import org.jetbrains.annotations.NotNull;

import javax.swing.JPanel;
import java.awt.BorderLayout;

public class JiraIssuePanel extends JiraPanel {

    private static final JPanel ISSUE_NOT_FOUND_PANEL = JiraPanelUtil.createPlaceHolderPanel("Issue Not Found");

    private final JiraIssuesData myIssuesData;
    private final String myIssueKey;

    private JiraIssueDescriptionPanel myDescriptionPanel;
    private JiraIssueStatusActivityPanel myStatusActivityPanel;

    public JiraIssuePanel(@NotNull JiraIssuesData issuesData, String issueKey) {
        super(new BorderLayout());

        myIssuesData = issuesData;
        myIssueKey = issueKey;


        JiraIssue issue = issuesData.getIssue(issueKey);
        if (issue != null) {
            JBSplitter splitter = new OnePixelSplitter(0.5f);

            myDescriptionPanel = new JiraIssueDescriptionPanel(issuesData, issue);
            myStatusActivityPanel = new JiraIssueStatusActivityPanel(issuesData, issue);

            splitter.setFirstComponent(myDescriptionPanel);
            splitter.setSecondComponent(myStatusActivityPanel);

            add(splitter);
        } else {
            add(ISSUE_NOT_FOUND_PANEL);
        }

        issuesData.getProject().getMessageBus().connect()
                .subscribe(RefreshIssuesListener.TOPIC, new MyRefreshIssuesListener());
    }


    private class MyRefreshIssuesListener implements RefreshIssuesListener {

        @Override
        public void onRefresh() {
            JiraIssue issue = myIssuesData.getIssue(myIssueKey);
            if (issue != null) {
                myDescriptionPanel.update(issue);
                myStatusActivityPanel.update(issue);
            } else {
                removeAll();
                add(ISSUE_NOT_FOUND_PANEL);
            }
        }
    }
}
