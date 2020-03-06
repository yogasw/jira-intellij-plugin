package com.intellij.jira.util.factory;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.openapi.util.Factory;

@FunctionalInterface
public interface JiraIssueFactory extends Factory<JiraIssue> {

}
