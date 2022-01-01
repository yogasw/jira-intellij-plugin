package com.intellij.jira.rest;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.intellij.jira.helper.TransitionFieldHelper.FieldEditorInfo;
import com.intellij.jira.rest.model.*;
import com.intellij.jira.rest.model.metadata.JiraIssueCreateMetadata;
import com.intellij.jira.util.JiraGsonUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.tasks.jira.JiraRepository;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.intellij.jira.rest.JiraIssueParser.*;
import static com.intellij.jira.ui.dialog.AddCommentDialog.ALL_USERS;
import static com.intellij.jira.util.JiraGsonUtil.*;
import static com.intellij.jira.util.JiraIssueField.KEY;
import static java.util.Objects.nonNull;

public class JiraRestClient {

    private static final Integer MAX_ISSUES_RESULTS = 500;
    private static final Integer MAX_USERS_RESULTS = 200;

    private static final String ISSUE = "issue";
    private static final String TRANSITIONS = "transitions";
    private static final String SEARCH = "search";
    private static final String COMMENT = "comment";
    private static final String WORKLOG = "worklog";

    private JiraRepository jiraRepository;

    public JiraRestClient(JiraRepository jiraRepository) {
        this.jiraRepository = jiraRepository;
    }

    public JiraIssue getIssue(String issueIdOrKey) throws Exception {
        GetMethod method = new GetMethod(this.jiraRepository.getRestUrl(ISSUE, issueIdOrKey));
        method.setQueryString(new NameValuePair[]{new NameValuePair("expand", "renderedFields")});
        String response = jiraRepository.executeMethod(method);
        return parseIssue(response);
    }

    public List<JiraIssue> findIssues(String searchQuery) throws Exception {
        GetMethod method = getBasicSearchMethod(searchQuery, MAX_ISSUES_RESULTS);
        method.setQueryString(method.getQueryString() + "&fields=*all&expand=renderedFields");
        String response = jiraRepository.executeMethod(method);
        return parseIssues(response);
    }

    public List<JiraIssueTransition> getTransitions(String issueId) throws Exception {
        GetMethod method = new GetMethod(this.jiraRepository.getRestUrl(ISSUE, issueId, TRANSITIONS));
        method.setQueryString(new NameValuePair[]{new NameValuePair("expand", "transitions.fields")});
        String response = jiraRepository.executeMethod(method);
        return parseIssueTransitions(response);
    }

    public String transitIssue(String issueId, String transitionId, Map<String, FieldEditorInfo> fields) throws Exception {
        String requestBody = getTransitionRequestBody(transitionId, fields);
        PostMethod method = new PostMethod(this.jiraRepository.getRestUrl(ISSUE, issueId, TRANSITIONS));
        method.setRequestEntity(createJsonEntity(requestBody));
        return jiraRepository.executeMethod(method);
    }

    public List<JiraIssueUser> getIssueAssignableUsers(String issueKey) throws Exception {
        return fetchUsers(new NameValuePair("issueKey", issueKey));
    }

    public List<JiraIssueUser> getProjectAssignableUsers(String projectKey) throws Exception {
        return fetchUsers(new NameValuePair("project", projectKey));
    }

    private List<JiraIssueUser> fetchUsers(NameValuePair issueOrProjectParam) throws Exception {
        GetMethod method = new GetMethod(this.jiraRepository.getRestUrl("user", "assignable", SEARCH));

        List<JiraIssueUser> newUsers;
        List<JiraIssueUser> jiraUsers = new ArrayList<>();

        do {
            method.setQueryString(new NameValuePair[]{
                    issueOrProjectParam,
                    new NameValuePair("startAt", String.valueOf(jiraUsers.size())),
                    new NameValuePair("maxResults", String.valueOf(MAX_USERS_RESULTS)),
            });

            String response = jiraRepository.executeMethod(method);
            newUsers = parseUsers(response);
            jiraUsers.addAll(newUsers);
        } while (newUsers.size() == MAX_USERS_RESULTS);

        return jiraUsers;
    }

    public String assignUserToIssue(String accountId,  String username, String issueKey) throws Exception {
        String requestBody = accountId != null ? JiraGsonUtil.createObject("accountId", accountId).toString() : JiraGsonUtil.createObject("name", username).toString();
        PutMethod method = new PutMethod(this.jiraRepository.getRestUrl(ISSUE, issueKey, "assignee"));
        method.setRequestEntity(createJsonEntity(requestBody));
        return jiraRepository.executeMethod(method);
    }

    public JiraIssueComment getComment(String issueKey, String commentId) throws Exception {
        GetMethod method = new GetMethod(this.jiraRepository.getRestUrl(ISSUE, issueKey, COMMENT, commentId));
        String response = jiraRepository.executeMethod(method);
        return parseIssueComment(response);
    }

    public JiraIssueComment addCommentToIssue(String body, String issueKey, String viewableBy) throws Exception {
        String requestBody = prepareCommentBody(body, viewableBy);
        PostMethod method = new PostMethod(this.jiraRepository.getRestUrl(ISSUE, issueKey, COMMENT));
        method.setRequestEntity(createJsonEntity(requestBody));
        String response = jiraRepository.executeMethod(method);
        return parseIssueComment(response);
    }

    public JiraIssueComment editIssueComment(String issueKey, String commentId, String body, String viewableBy) throws Exception {
        String requestBody = prepareCommentBody(body, viewableBy);
        PutMethod method = new PutMethod(this.jiraRepository.getRestUrl(ISSUE, issueKey, COMMENT, commentId));
        method.setRequestEntity(createJsonEntity(requestBody));
        String response = jiraRepository.executeMethod(method);
        return parseIssueComment(response);
    }


    private GetMethod getBasicSearchMethod(String jql, int maxResults) {
        GetMethod method = new GetMethod(this.jiraRepository.getRestUrl(SEARCH));
        method.setQueryString(new NameValuePair[]{new NameValuePair("jql", jql), new NameValuePair("maxResults", String.valueOf(maxResults))});
        return method;
    }

    private static RequestEntity createJsonEntity(String requestBody) {
        try {
            return new StringRequestEntity(requestBody, "application/json", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("UTF-8 encoding is not supported");
        }
    }

    public String deleteCommentToIssue(String issueKey, String commentId) throws Exception {
        DeleteMethod method = new DeleteMethod(this.jiraRepository.getRestUrl(ISSUE, issueKey, "comment", commentId));
        return jiraRepository.executeMethod(method);
    }

    public List<JiraIssuePriority> getIssuePriorities() throws Exception {
        GetMethod method = new GetMethod(this.jiraRepository.getRestUrl("priority"));
        String response = jiraRepository.executeMethod(method);
        return parseIssuePriorities(response);
    }

    public String changeIssuePriority(String priorityName, String issueIdOrKey) throws Exception {
        String requestBody = "{\"update\": {\"priority\": [{\"set\": {\"name\": \"" + priorityName + "\"}}]}}";
        PutMethod method = new PutMethod(this.jiraRepository.getRestUrl(ISSUE, issueIdOrKey));
        method.setRequestEntity(createJsonEntity(requestBody));
        return jiraRepository.executeMethod(method);
    }

    public LinkedHashMap<String, JiraPermission> findUserPermissionsOnIssue(String issueKey, JiraPermissionType permission) throws Exception {

        NameValuePair[] queryParams = {
                new NameValuePair("issueKey", issueKey),
                new NameValuePair("permissions", permission.toString())
        };

        return findUserPermissions(queryParams);
    }

    public LinkedHashMap<String, JiraPermission> findUserPermissions(NameValuePair... queryParams) throws Exception {
        GetMethod method = new GetMethod(this.jiraRepository.getRestUrl("mypermissions"));
        method.setQueryString(queryParams);

        String response = jiraRepository.executeMethod(method);
        return parsePermissions(response);
    }

    public List<JiraIssueLinkType> getIssueLinkTypes() throws Exception {
        GetMethod method = new GetMethod(this.jiraRepository.getRestUrl("issueLinkType"));
        String response = jiraRepository.executeMethod(method);
        return parseIssueLinkTypes(response);
    }

    public List<JiraGroup> getGroups() throws Exception {
        GetMethod method = new GetMethod(this.jiraRepository.getRestUrl("groups", "picker"));
        String response = jiraRepository.executeMethod(method);
        return parseGroups(response);
    }

    private String getTransitionRequestBody(String transitionId, Map<String, FieldEditorInfo> fields) {
        JsonObject transition = new JsonObject();
        transition.add("transition", createIdObject(transitionId));

        // Update
        JsonObject updateObject = new JsonObject();

        // Comment
        FieldEditorInfo commentField = fields.remove("comment");
        if(nonNull(commentField) && !(commentField.getJsonValue() instanceof JsonNull)){
            updateObject.add("comment", commentField.getJsonValue());
        }

        // Work Log
        FieldEditorInfo worklogField = fields.remove("worklog");
        if(nonNull(worklogField)) {
            JsonElement worklogFieldValue = worklogField.getJsonValue();
            if (!(worklogFieldValue instanceof JsonNull)) {
                updateObject.add("worklog", worklogFieldValue);
            }
        }

        // Linked Issues
        FieldEditorInfo issueLinkField = fields.remove("issuelinks");
        if(nonNull(issueLinkField) && !(issueLinkField.getJsonValue() instanceof JsonNull)){
            updateObject.add("issuelinks", issueLinkField.getJsonValue());
        }

        if(updateObject.size() > 0){
            transition.add("update", updateObject);
        }

        //Fields
        JsonObject fieldsObject = new JsonObject();
        fields.forEach((key, value) -> {
            JsonElement jsonValue = value.getJsonValue();
            if(!(jsonValue instanceof JsonNull)){
                fieldsObject.add(key, jsonValue);
            }
        });

        if(fieldsObject.size() > 0){
            transition.add("fields", fieldsObject);
        }


        return transition.toString();
    }


    public String getDefaultSearchQuery() {
        return jiraRepository.getSearchQuery();
    }

    public List<String> getProjectRoles(String projectKey) throws Exception {
        GetMethod method = new GetMethod(this.jiraRepository.getRestUrl("project", projectKey, "role"));
        String response = jiraRepository.executeMethod(method);

        return parseRoles(response);
    }

    private String prepareCommentBody(String body, String viewableBy){
        JsonObject commentBody = new JsonObject();
        commentBody.addProperty("body", body);

        if(!ALL_USERS.equals(viewableBy)){
            JsonObject visibility = new JsonObject();
            visibility.addProperty("type", "role");
            visibility.addProperty("value", viewableBy);
            commentBody.add("visibility", visibility);
        }

        return commentBody.toString();
    }

    public Integer addIssueLink(String linkType, String inIssueKey, String outIssueKey) throws Exception {
        String requestBody = prepareIssueLinkBody(linkType, inIssueKey, outIssueKey);
        PostMethod method = new PostMethod(this.jiraRepository.getRestUrl("issueLink"));
        method.setRequestEntity(createJsonEntity(requestBody));
        jiraRepository.executeMethod(method);
        return method.getStatusCode();
    }

    private String prepareIssueLinkBody(String linkType, String inIssueKey, String outIssueKey) {
        JsonObject linkObject = new JsonObject();
        linkObject.add("type", createNameObject(linkType));
        linkObject.add("inwardIssue", createObject(KEY, inIssueKey));
        linkObject.add("outwardIssue", createObject(KEY, outIssueKey));

        return linkObject.toString();
    }

    public Integer deleteIssueLink(String issueLinkId) throws Exception {
        DeleteMethod method = new DeleteMethod(this.jiraRepository.getRestUrl("issueLink", issueLinkId));
        jiraRepository.executeMethod(method);
        return method.getStatusCode();
    }

    public String getUsername(){
        return this.jiraRepository.getUsername();
    }

    public JiraIssueWorklog getWorklog(String issueKey, String worklogId) throws Exception {
        GetMethod method = new GetMethod(this.jiraRepository.getRestUrl(ISSUE, issueKey, WORKLOG, worklogId));
        String response = jiraRepository.executeMethod(method);
        
        return parseIssueWorklog(response);
    }

    public JiraIssueWorklog addIssueWorklog(String issueKey, List<FieldEditorInfo> worklogFields, String remainingEstimate) throws Exception {
        String requestBody = prepareWorklogBody(worklogFields);
        PostMethod method = new PostMethod(this.jiraRepository.getRestUrl(ISSUE, issueKey, WORKLOG));
        if(StringUtil.isNotEmpty(remainingEstimate)){
            method.setQueryString("adjustEstimate=" + remainingEstimate);
        }
        method.setRequestEntity(createJsonEntity(requestBody));
        String response = jiraRepository.executeMethod(method);
        
        return parseIssueWorklog(response);
    }
    
    public JiraIssueWorklog updateIssueWorklog(String issueKey, String workLogId, List<FieldEditorInfo> worklogFields, String remainingEstimate) throws Exception {
        String requestBody = prepareWorklogBody(worklogFields);
        PutMethod method = new PutMethod(this.jiraRepository.getRestUrl(ISSUE, issueKey, WORKLOG, workLogId));
        if(StringUtil.isNotEmpty(remainingEstimate)){
            method.setQueryString("adjustEstimate=" + remainingEstimate);
        }
        method.setRequestEntity(createJsonEntity(requestBody));
        String response = jiraRepository.executeMethod(method);

        return parseIssueWorklog(response);
    }

    private String prepareWorklogBody(List<FieldEditorInfo> worklogFields){
        JsonObject worklogObject = new JsonObject();
        for(FieldEditorInfo editorInfo : worklogFields){
            JsonElement jsonValue = editorInfo.getJsonValue();
            if(!jsonValue.isJsonNull()){
                worklogObject.add(editorInfo.getName(), jsonValue);
            }
        }

        return worklogObject.toString();
    }

    public Integer deleteIssueWorklog(String issueKey, String worklogId, String remainingEstimate) throws Exception {
        DeleteMethod method = new DeleteMethod(this.jiraRepository.getRestUrl(ISSUE, issueKey, WORKLOG, worklogId));
        if(StringUtil.isNotEmpty(remainingEstimate)){
            method.setQueryString("adjustEstimate=" + remainingEstimate);
        }

        jiraRepository.executeMethod(method);
        return method.getStatusCode();
    }

    public Integer watchIssue(String issueKey) throws Exception {
        PostMethod method = new PostMethod(this.jiraRepository.getRestUrl(ISSUE, issueKey, "watchers"));

        jiraRepository.executeMethod(method);
        return method.getStatusCode();
    }

    public Integer unwatchIssue(String issueKey, String accountId, String username) throws Exception {
        DeleteMethod method = new DeleteMethod(this.jiraRepository.getRestUrl(ISSUE, issueKey, "watchers"));
        if (accountId != null) {
            method.setQueryString(new NameValuePair[]{new NameValuePair("accountId", accountId)});
        } else {
            // For compatibility
            method.setQueryString(new NameValuePair[]{new NameValuePair("username", username)});
        }

        jiraRepository.executeMethod(method);
        return method.getStatusCode();
    }

    public JiraIssueUser getCurrentUser() throws Exception {
        GetMethod method = new GetMethod(this.jiraRepository.getRestUrl("myself"));
        String response = jiraRepository.executeMethod(method);

        return parseUser(response);
    }

    public List<JiraIssueAttachment> addIssueAttachment(String issueKey, File attachment) throws Exception {
        PostMethod method = new PostMethod(this.jiraRepository.getRestUrl(ISSUE, issueKey, "attachments"));
        method.addRequestHeader("X-Atlassian-Token", "no-check");
        Part[] parts = {new FilePart("file", attachment)};

        method.setRequestEntity(new MultipartRequestEntity(parts, method.getParams()));
        String response = jiraRepository.executeMethod(method);

        return parseIssueAttachments(response);
    }

    public Integer deleteIssueAttachment(String attachmentId) throws Exception {
        DeleteMethod method = new DeleteMethod(this.jiraRepository.getRestUrl("attachment", attachmentId));

        jiraRepository.executeMethod(method);
        return method.getStatusCode();
    }

    public JiraIssueCreateMetadata getIssueCreateMeta() throws Exception {
        GetMethod method = new GetMethod(this.jiraRepository.getRestUrl(ISSUE, "createmeta"));
        method.setQueryString(new NameValuePair[]{new NameValuePair("expand", "projects.issuetypes.fields")});

        String response = jiraRepository.executeMethod(method);

        return parseIssueCreateMeta(response);
    }

}

