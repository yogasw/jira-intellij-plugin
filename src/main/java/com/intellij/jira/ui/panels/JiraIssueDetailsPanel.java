package com.intellij.jira.ui.panels;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.ui.JiraTabbedPane;
import com.intellij.jira.util.JiraPanelUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.isNull;

public class JiraIssueDetailsPanel extends SimpleToolWindowPanel {

    private static final String TAB_KEY = "selectedTab";

    private final Project myProject;
    private final Map<String, Integer> data = new HashMap<>();

    public JiraIssueDetailsPanel(Project project){
        super(true);
        this.myProject = project;
        setEmptyContent();
    }

    public void showIssue(@Nullable JiraIssue issue) {
        if(isNull(issue)){
            setEmptyContent();
        }else{
            JiraTabbedPane tabbedPane = new JiraTabbedPane(JTabbedPane.BOTTOM);
            tabbedPane.addTab("Preview", new JiraIssuePreviewPanel(myProject, issue));
            tabbedPane.addTab(String.format("Attachments (%d)", issue.getAttachments().size()), new JiraIssueAttachmentsPanel(issue));
            tabbedPane.addTab(String.format("Links (%d)", issue.getIssueLinks().size()), new JiraIssueLinksPanel(issue));
            tabbedPane.addTab(String.format("Sub-Tasks (%d)", issue.getSubtasks().size()), new JiraIssueSubtasksPanel(issue));
            tabbedPane.addTab(String.format("Comments (%d)", issue.getRenderedComments().getTotal()), new JiraIssueCommentsPanel(issue));
            tabbedPane.addTab(String.format("Work Log (%d)", issue.getWorklogs().getTotal()), new JiraIssueWorkLogsPanel(issue));

            tabbedPane.addChangeListener(e -> data.put(TAB_KEY, tabbedPane.getSelectedIndex()));
            tabbedPane.setSelectedIndex(data.getOrDefault(TAB_KEY, 0));

            setContent(tabbedPane);
        }

    }

    public void setEmptyContent(){
        setContent(JiraPanelUtil.createPlaceHolderPanel("Select issue to view details"));
    }

}
