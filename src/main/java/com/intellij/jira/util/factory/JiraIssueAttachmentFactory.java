package com.intellij.jira.util.factory;

import com.intellij.jira.rest.model.JiraIssueAttachment;
import com.intellij.openapi.util.Factory;

@FunctionalInterface
public interface JiraIssueAttachmentFactory extends Factory<JiraIssueAttachment> {

}
