package com.intellij.jira.rest.model;

public enum JiraPermissionType {
    EDIT_ISSUES("EDIT_ISSUE"),
    ADD_COMMENTS("COMMENT_ISSUE"),
    DELETE_OWN_COMMENTS("COMMENT_DELETE_OWN"),
    LINK_ISSUES("LINK_ISSUE");

    private String oldPermission;

    JiraPermissionType(String oldPermission) {
        this.oldPermission = oldPermission;
    }

    public String getOldPermission() {
        return oldPermission;
    }

}
