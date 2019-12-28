package com.intellij.jira.rest.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.intellij.jira.rest.JiraIssueCommentsWrapper;
import com.intellij.jira.rest.JiraIssueWorklogsWrapper;
import com.intellij.jira.util.JiraGsonUtil;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class JiraIssue {

    private String id;
    private String self;
    private String key;
    private JsonObject fields;

    public JiraIssue() { }

    public String getId() {
        return id;
    }

    public String getSelf() {
        return self;
    }

    public String getKey() {
        return key;
    }

    public String getSummary() {
        return JiraGsonUtil.getAsString(fields.get("summary"));
    }

    public String getDescription() {
        return JiraGsonUtil.getAsString(fields.get("description"));
    }

    public Date getCreated() {
        return JiraGsonUtil.getAsDate(fields.get("created"));
    }

    public Date getUpdated() {
        return JiraGsonUtil.getAsDate(fields.get("updated"));
    }

    public Date getResolutiondate() {
        return JiraGsonUtil.getAsDate(fields.get("resolutiondate"));
    }

    public Date getDuedate() {
        return JiraGsonUtil.getAsDate(fields.get("duedate"));
    }

    public JiraIssueType getIssuetype() {
        return JiraGsonUtil.getAs(fields.get("issuetype"), JiraIssueType.class);
    }

    public JiraIssueStatus getStatus() {
        return JiraGsonUtil.getAs(fields.get("status"), JiraIssueStatus.class);
    }

    public JiraIssueResolution getResolution() {
        return JiraGsonUtil.getAs(fields.get("resolution"), JiraIssueResolution.class);
    }

    public JiraIssuePriority getPriority() {
        return JiraGsonUtil.getAs(fields.get("priority"), JiraIssuePriority.class);
    }

    public JiraIssueUser getAssignee() {
        return JiraGsonUtil.getAs(fields.get("assignee"), JiraIssueUser.class);
    }

    public JiraIssueUser getCreator() {
        return JiraGsonUtil.getAs(fields.get("creator"), JiraIssueUser.class);
    }

    public JiraIssueUser getReporter() {
        return JiraGsonUtil.getAs(fields.get("reporter"), JiraIssueUser.class);
    }

    public JiraIssueCommentsWrapper getComments(){
        return JiraGsonUtil.getAs(fields.get("comment"), JiraIssueCommentsWrapper.class);
    }

    public JiraIssueWorklogsWrapper getWorklogs(){
        return JiraGsonUtil.getAs(fields.get("worklog"), JiraIssueWorklogsWrapper.class);
    }

    public JiraIssueTimeTracking getTimetracking(){
        return JiraGsonUtil.getAs(fields.get("timetracking"), JiraIssueTimeTracking.class);
    }

    public List<JiraIssueLink> getIssueLinks(){
        return JiraGsonUtil.getAsList(fields.get("issuelinks"), JiraIssueLink[].class);
    }

    public JiraProject getProject(){
        return JiraGsonUtil.getAs(fields.get("project"), JiraProject.class);
    }

    public List<JiraProjectVersion> getVersions(){
        return JiraGsonUtil.getAsList(fields.get("versions"), JiraProjectVersion[].class);
    }

    public List<JiraProjectVersion> getFixVersions(){
        return JiraGsonUtil.getAsList(fields.get("fixVersions"), JiraProjectVersion[].class);
    }

    public List<JiraIssueComponent> getComponents(){
        return JiraGsonUtil.getAsList(fields.get("components"), JiraIssueComponent[].class);
    }

    public List<String> getLabels(){
        return JiraGsonUtil.getAsList(fields.get("labels"), String[].class);
    }

    public String getAsString(String fieldName) {
        return JiraGsonUtil.getAsString(fields.get(fieldName));
    }

    public Date getAsDate(String fieldName) {
        return JiraGsonUtil.getAsDate(fields.get(fieldName));
    }

    public JiraIssueUser getAsJiraIssueUser(String fieldName) {
        return JiraGsonUtil.getAs(fields.get(fieldName), JiraIssueUser.class);
    }

    public JsonElement getCustomfieldValue(String customId) {
        return fields.get("customfield_" + customId);
    }

    public String getUrl(){
        return self.replaceFirst("(/rest([\\w/]+))", "/browse/" + getKey());
    }

    public boolean isResolved(){
        return getStatus().isDoneCategory();
    }

    public boolean hasComponents(){
        return !getComponents().isEmpty();
    }

    public boolean hasLabels(){
        return !getLabels().isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JiraIssue jiraIssue = (JiraIssue) o;
        return Objects.equals(id, jiraIssue.id) &&
                Objects.equals(key, jiraIssue.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, key);
    }
}
