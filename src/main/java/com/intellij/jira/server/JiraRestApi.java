package com.intellij.jira.server;

import com.intellij.jira.helper.TransitionFieldHelper.FieldEditorInfo;
import com.intellij.jira.rest.JiraRestClient;
import com.intellij.jira.rest.model.*;
import com.intellij.jira.util.result.BodyResult;
import com.intellij.jira.util.result.EmptyResult;
import com.intellij.jira.util.result.Result;
import com.intellij.tasks.jira.JiraRepository;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

public class JiraRestApi {

    private static final Logger log = LoggerFactory.getLogger(JiraRestApi.class);

    private JiraRestClient jiraRestClient;

    public JiraRestApi(JiraRepository jiraRepository) {
        this.jiraRestClient = new JiraRestClient(jiraRepository);
    }


    public Result getIssue(String issueIdOrKey){
        try {
            JiraIssue issue = this.jiraRestClient.getIssue(issueIdOrKey);
            return BodyResult.ok(issue);
        } catch (Exception e) {
            log.error(String.format("Issue %s not found", issueIdOrKey));
        }

        return BodyResult.error();
    }


    public List<JiraIssue> getIssues(String searchQuery) {
        try {
            return this.jiraRestClient.findIssues(searchQuery);
        } catch (Exception e) {
            log.error("No issues found");
            return new ArrayList<>();
        }
    }

    public List<JiraIssueTransition> getTransitions(String issueId){
        try {
            return jiraRestClient.getTransitions(issueId);
        } catch (Exception e) {
            log.error(String.format("No transitions was found for issue '%s'", issueId));
            return new ArrayList<>();
        }
    }


    public Result transitIssue(String issueId, String transitionId, Map<String, FieldEditorInfo> fields){
        try {
            String response = jiraRestClient.transitIssue(issueId, transitionId, fields);
            return EmptyResult.create(response);
        } catch (Exception e) {
            log.error(String.format("Error executing transition '%s' in issue '%s'", transitionId, issueId));
            return EmptyResult.error();
        }
    }

    public List<JiraIssueUser> getAssignableUsers(String issueKey){
        try {
            return jiraRestClient.getAssignableUsers(issueKey);
        } catch (Exception e) {
            log.error("Error fetching users to assign");
            return new ArrayList<>();
        }
    }


    public Result assignUserToIssue(String accountId,  String username, String issueKey){
        try {
            String response = jiraRestClient.assignUserToIssue(accountId, username, issueKey);
            return EmptyResult.create(response);
        } catch (Exception e) {
            log.error(String.format("Error assigning user with accoundId = '%s' to issue '%s'", accountId, issueKey));
            return EmptyResult.error();
        }
    }

    @Nullable
    public JiraIssueComment getComment(String issueKey, String commentId) {
        JiraIssueComment comment = null;
        try {
            comment = jiraRestClient.getComment(issueKey, commentId);
        } catch (Exception e) {
            log.error(String.format("Comment with id = %s doesn't exists", commentId));
        }

        return comment;
    }

    public Result addIssueComment(String body, String issueKey, String viewableBy){
        try {
            JiraIssueComment comment = jiraRestClient.addCommentToIssue(body, issueKey, viewableBy);
            return BodyResult.ok(comment);
        } catch (Exception e) {
            log.error(String.format("Error creating comment in issue '%s'", issueKey));
            return BodyResult.error();
        }
    }

    public Result editIssueComment(String issueKey, String commentId, String body, String viewableBy){
        try {
            JiraIssueComment comment = jiraRestClient.editIssueComment(issueKey, commentId, body, viewableBy);
            return BodyResult.ok(comment);
        } catch (Exception e) {
            log.error(String.format("Error editing comment in issue '%s'", issueKey));
            return BodyResult.error();
        }
    }


    public Result deleteIssueComment(String issueKey, String commentId) {
        try {
            String response = jiraRestClient.deleteCommentToIssue(issueKey, commentId);
            return EmptyResult.create(response);
        } catch (Exception e) {
            log.error(String.format("Error deleting comment in issue '%s'", issueKey));
            return EmptyResult.error();
        }

    }

    public List<JiraIssuePriority> getIssuePriorities() {
        try {
            return jiraRestClient.getIssuePriorities();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Result changeIssuePriority(String priorityName, String issueIdOrKey) {
        try {
            String response = jiraRestClient.changeIssuePriority(priorityName, issueIdOrKey);
            return EmptyResult.create(response);
        } catch (Exception e) {
            e.printStackTrace();
            return EmptyResult.error();
        }

    }

    public boolean userHasPermissionOnIssue(String issueKey, JiraPermissionType permission){
        LinkedHashMap<String, JiraPermission> permissions = new LinkedHashMap<>();
        try {
            permissions = jiraRestClient.findUserPermissionsOnIssue(issueKey, permission);
        } catch (Exception e) {
            log.error("Current user has not permission to do this action");
        }

        JiraPermission jiraPermission = permissions.get(permission.toString());
        if(Objects.isNull(jiraPermission)){
            jiraPermission = permissions.get(permission.getOldPermission());
        }

        return Objects.isNull(jiraPermission) ? false : jiraPermission.isHavePermission();
    }


    public List<JiraIssueLinkType> getIssueLinkTypes(){
        try {
            return jiraRestClient.getIssueLinkTypes();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<JiraGroup> getGroups(){
        try {
            return jiraRestClient.getGroups();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public JiraIssueWorklog getWorklog(String issueKey, String worklogId){
        JiraIssueWorklog worklog = null;
        try {
            worklog = jiraRestClient.getWorklog(issueKey, worklogId);
        } catch (Exception e) {
            log.error(String.format("WorkLog with id = %s doesn't exists", worklogId));
        }

        return worklog;
    }

    public void testConnection() throws Exception {
        findCurrentUser();
    }

    public List<String> getProjectRoles(String projectKey) {
        try {
            return jiraRestClient.getProjectRoles(projectKey);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Result addIssueLink(String linkType, String inIssueKey, String outIssueKey) {
        try {
            Integer statusCode = jiraRestClient.addIssueLink(linkType, inIssueKey, outIssueKey);
            return statusCode == 201 ? BodyResult.ok(statusCode) :  BodyResult.error();
        } catch (Exception e) {
            log.error("Error creating issue link");
            return BodyResult.error();
        }
    }

    public Result deleteIssueLink(String issueLinkId) {
        try {
            Integer statusCode = jiraRestClient.deleteIssueLink(issueLinkId);
            return statusCode == 204 ? BodyResult.ok(statusCode) :  BodyResult.error();
        } catch (Exception e) {
            log.error("Error deleting issue link");
            return BodyResult.error();
        }
    }

    public String getUsername(){
        return jiraRestClient.getUsername();
    }

    public Result addIssueWorklog(String issueKey, List<FieldEditorInfo> worklogFields, String remainingEstimate) {
        try {
            JiraIssueWorklog worklog = jiraRestClient.addIssueWorklog(issueKey, worklogFields, remainingEstimate);
            return BodyResult.ok(worklog);
        } catch (Exception e) {
            log.error(String.format("Error creating worklog in issue '%s'", issueKey));
            return BodyResult.error();
        }
    }

    public Result editIssueWorklog(String issueKey, String workLogId, List<FieldEditorInfo> worklogFields, String remainingEstimate) {
        try {
            JiraIssueWorklog worklog = jiraRestClient.updateIssueWorklog(issueKey, workLogId, worklogFields, remainingEstimate);
            return BodyResult.ok(worklog);
        } catch (Exception e) {
            log.error(String.format("Error editing worklog in issue '%s'", issueKey));
            return BodyResult.error();
        }
    }

    public Result deleteIssueWorklog(String issueKey, String worklogId, String remainingEstimate) {
        try {
            Integer statusCode = jiraRestClient.deleteIssueWorklog(issueKey, worklogId, remainingEstimate);
            return statusCode == 204 ? BodyResult.ok(statusCode) :  BodyResult.error();
        } catch (Exception e) {
            log.error("Error deleting issue link");
            return BodyResult.error();
        }
    }

    public Result watchIssue(String issueKey) {
        try {
            Integer statusCode = jiraRestClient.watchIssue(issueKey);
            return statusCode == 204 ? BodyResult.ok(statusCode) :  BodyResult.error();
        } catch (Exception e) {
            log.error("Error watching issue");
            return BodyResult.error();
        }
    }

    public Result unwatchIssue(String issueKey, String accountId, String username) {
        try {
            Integer statusCode = jiraRestClient.unwatchIssue(issueKey, accountId, username);
            return statusCode == 204 ? BodyResult.ok(statusCode) :  BodyResult.error();
        } catch (Exception e) {
            log.error("Error watching issue");
            return BodyResult.error();
        }
    }

    public Result getCurrentUser() {
        try {
            return BodyResult.ok(findCurrentUser());
        } catch (Exception e) {
            log.error("Error getting current user");
            return BodyResult.error();
        }
    }

    public Result addIssueAttachment(String issueKey, File attachment) {
        try {
            return BodyResult.ok(jiraRestClient.addIssueAttachment(issueKey, attachment));
        } catch (Exception e) {
            log.error("Error attaching on issue " + issueKey);
            return BodyResult.error();
        }
    }

    public Result deleteIssueAttachment(String attachmentId) {
        try {
            Integer statusCode = jiraRestClient.deleteIssueAttachment(attachmentId);
            return statusCode == 204 ? BodyResult.ok(statusCode) : BodyResult.error();
        } catch (Exception e) {
            log.error("Error deleting attachment");
            return BodyResult.error();
        }
    }

    private JiraIssueUser findCurrentUser() throws Exception {
        return jiraRestClient.getCurrentUser();
    }

}
