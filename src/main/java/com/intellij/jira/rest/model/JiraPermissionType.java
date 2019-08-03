package com.intellij.jira.rest.model;

public enum JiraPermissionType {
    EDIT_ISSUES("EDIT_ISSUE"),
    ADD_COMMENTS("COMMENT_ISSUE"),
    EDIT_ALL_COMMENTS("COMMENT_EDIT_ALL"),
    EDIT_OWN_COMMENTS("COMMENT_EDIT_OWN"),
    DELETE_ALL_COMMENTS("COMMENT_DELETE_ALL"),
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
