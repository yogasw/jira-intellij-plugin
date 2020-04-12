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
    private static final String TAB_PREVIEW = " Preview ";
    private static final String TAB_ATTACHMENTS = " Attachments";
    private static final String TAB_LINKS = " Links";
    private static final String TAB_SUB_TASKS = " Sub-Tasks";
    private static final String TAB_COMMENTS = " Comments";
    private static final String TAB_WORK_LOG = " Work Log";

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
            tabbedPane.addTab(TAB_PREVIEW, new JiraIssuePreviewPanel(myProject, issue));
            tabbedPane.addTab(TAB_ATTACHMENTS + appendTotal(issue.getAttachments().size()), new JiraIssueAttachmentsPanel(issue));
            tabbedPane.addTab(TAB_LINKS + appendTotal(issue.getIssueLinks().size()), new JiraIssueLinksPanel(issue));
            tabbedPane.addTab(TAB_SUB_TASKS + appendTotal(issue.getSubtasks().size()), new JiraIssueSubtasksPanel(issue));
            tabbedPane.addTab(TAB_COMMENTS + appendTotal(issue.getRenderedComments().getTotal()), new JiraIssueCommentsPanel(issue));
            tabbedPane.addTab(TAB_WORK_LOG + appendTotal(issue.getWorklogs().getTotal()), new JiraIssueWorkLogsPanel(issue));

            tabbedPane.addChangeListener(e -> data.put(TAB_KEY, tabbedPane.getSelectedIndex()));
            tabbedPane.setSelectedIndex(data.getOrDefault(TAB_KEY, 0));

            setContent(tabbedPane);
        }

    }

    public void setEmptyContent(){
        setContent(JiraPanelUtil.createPlaceHolderPanel("Select issue to view details"));
    }

    private String appendTotal(int total) {
        return total > 0 ? " (" + total + ") " : " ";
    }
}
