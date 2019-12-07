package com.intellij.jira.util;

import com.intellij.jira.rest.model.JiraIssueWorklog;
import com.intellij.openapi.util.Factory;

@FunctionalInterface
public interface JiraIssueWorklogFactory extends Factory<JiraIssueWorklog> {

}
