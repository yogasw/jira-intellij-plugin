package com.intellij.jira.util;

import com.intellij.jira.rest.model.JiraIssueTimeTracking;
import com.intellij.openapi.util.Factory;

@FunctionalInterface
public interface JiraIssuTimeTrackingFactory extends Factory<JiraIssueTimeTracking> {

}
