package com.intellij.jira.rest.model;

import com.intellij.jira.rest.JiraIssueCommentsWrapper;
import com.intellij.jira.rest.JiraIssueWorklogsWrapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class JiraIssue {

    public static final String REQUIRED_FIELDS = "summary,description,created,updated,duedate,resolutiondate,assignee,reporter,issuetype,status,priority,comment,issuelinks,project,versions,components,labels,worklog,timetracking,fixVersions";

    private String id;
    private String self;
    private String key;
    private JiraIssue.Fields fields;

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
        return fields.summary;
    }

    public String getDescription() {
        return fields.description;
    }

    public Date getCreated() {
        return fields.created;
    }

    public Date getUpdated() {
        return fields.updated;
    }

    public Date getResolutiondate() {
        return fields.resolutiondate;
    }

    public Date getDuedate() {
        return fields.duedate;
    }

    public JiraIssueType getIssuetype() {
        return fields.issuetype;
    }

    public JiraIssueStatus getStatus() {
        return fields.status;
    }

    public JiraIssuePriority getPriority() {
        return fields.priority;
    }

    public JiraIssueUser getAssignee() {
        return fields.assignee;
    }

    public JiraIssueUser getCreator() {
        return fields.creator;
    }

    public JiraIssueUser getReporter() {
        return fields.reporter;
    }

    public JiraIssueCommentsWrapper getComments(){
        return fields.comment;
    }

    public List<JiraIssueWorklog> getWorklogs(){
        return Objects.nonNull(fields.worklog) ? fields.worklog.getWorklogs() : new ArrayList<>();
    }

    public JiraIssueTimeTracking getTimetracking(){
        return fields.timetracking;
    }

    public List<JiraIssueLink> getIssueLinks(){
        return fields.issuelinks;
    }

    public JiraProject getProject(){
        return fields.project;
    }

    public List<JiraProjectVersion> getVersions(){
        return fields.versions;
    }

    public List<JiraProjectVersion> getFixVersions(){
        return fields.fixVersions;
    }

    public List<JiraIssueComponent> getComponents(){
        return fields.components;
    }

    public List<String> getLabels(){
        return fields.labels;
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

    public Object getValueForField(JiraIssueFieldProperties property) {
        String field = property.getSchema().getSystem();

        if (field.equals("summary")) {
            return getSummary();
        }
        if (field.equals("description")) {
            return getDescription();
        }
        if (field.equals("created")) {
            return getCreated();
        }
        if (field.equals("updated")) {
            return getUpdated();
        }
        if (field.equals("duedate")) {
            return getDuedate();
        }
        if (field.equals("resolutiondate")) {
            return getResolutiondate();
        }
        if (field.equals("assignee")) {
            return getAssignee();
        }
        if (field.equals("reporter")) {
            return getReporter();
        }
        if (field.equals("issuetype")) {
            return getIssuetype();
        }
        if (field.equals("status")) {
            return getStatus();
        }
        if (field.equals("priority")) {
            return getPriority();
        }
        if (field.equals("comment")) {
            return getComponents();
        }
        if (field.equals("issuelinks")) {
            return getIssueLinks();
        }
        if (field.equals("project")) {
            return getProject();
        }
        if (field.equals("versions")) {
            return getVersions();
        }
        if (field.equals("components")) {
            return getComponents();
        }
        if (field.equals("labels")) {
            return getLabels();
        }
        if (field.equals("fixVersions")) {
            return getFixVersions();
        }
        return null;
    }

    public static class Fields{

        private String summary;
        private String description;
        private Date created;
        private Date updated;
        private Date resolutiondate;
        private Date duedate;
        private JiraIssueType issuetype;
        private JiraIssueStatus status;
        private JiraIssuePriority priority;
        private JiraIssueUser assignee;
        private JiraIssueUser creator;
        private JiraIssueUser reporter;
        private JiraIssueCommentsWrapper comment;
        private JiraIssueWorklogsWrapper worklog;
        private JiraIssueTimeTracking timetracking;
        private List<JiraIssueLink> issuelinks = new ArrayList<>();
        private JiraProject project;
        private List<JiraProjectVersion> versions = new ArrayList<>();
        private List<JiraIssueComponent> components = new ArrayList<>();
        private List<String> labels = new ArrayList<>();
        private List<JiraProjectVersion> fixVersions = new ArrayList<>();

        public Fields() { }

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
