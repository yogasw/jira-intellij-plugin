package com.intellij.jira.rest.model;

import java.util.Date;

public class JiraIssueComment {

    private String self;
    private String id;
    private JiraIssueUser author;
    private String body;
    private Date created;
    private Date updated;
    private JiraVisibility visibility;

    public JiraIssueComment() { }

    public JiraIssueUser getAuthor() {
        return author;
    }

    public String getBody() {
        return body;
    }

    public Date getCreated() {
        return created;
    }

    public String getId() {
        return id;
    }

    public JiraVisibility getVisibility() {
        return visibility;
    }


}
