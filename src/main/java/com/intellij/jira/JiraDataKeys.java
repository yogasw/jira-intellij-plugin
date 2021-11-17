package com.intellij.jira;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.JiraIssueAttachment;
import com.intellij.jira.rest.model.JiraIssueComment;
import com.intellij.jira.rest.model.JiraIssueLink;
import com.intellij.jira.rest.model.JiraIssueTimeTracking;
import com.intellij.jira.rest.model.JiraIssueWorklog;
import com.intellij.openapi.actionSystem.DataKey;

public class JiraDataKeys {

    private JiraDataKeys() { }

    public static final DataKey<JiraIssue> ISSUE = DataKey.create("issue");

    public static final DataKey<String> ISSUE_KEY = DataKey.create("issue.key");

    public static final DataKey<String> PROJECT_KEY = DataKey.create("project.key");

    public static final DataKey<JiraIssueComment> ISSUE_COMMENT = DataKey.create("issue.comment");

    public static final DataKey<JiraIssueAttachment> ISSUE_ATTACHMENT = DataKey.create("issue.attachment");

    public static final DataKey<JiraIssueLink> ISSUE_LINK = DataKey.create("issue.link");

    public static final DataKey<JiraIssueWorklog> ISSUE_WORKLOG = DataKey.create("issue.worklog");

    public static final DataKey<JiraIssueTimeTracking> ISSUE_TIME_TRACKING = DataKey.create("issue.timetracking");

}
