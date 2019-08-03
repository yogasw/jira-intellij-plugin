package com.intellij.jira.util;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.JiraIssueComment;
import com.intellij.jira.rest.model.JiraIssueUser;
import com.intellij.util.text.DateFormatUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.nonNull;

public class JiraIssueUtil {

    private static final Pattern BODY_NAME_PATTERN = Pattern.compile("(\\[~(\\w+)])");

    public static String getKey(@NotNull JiraIssue jiraIssue) {
        return jiraIssue.getKey();
    }

    public static String getSummary(@NotNull JiraIssue jiraIssue) {
        return jiraIssue.getSummary();
    }

    public static String getAssignee(@NotNull JiraIssue jiraIssue) {
        return nonNull(jiraIssue.getAssignee()) ? jiraIssue.getAssignee().getName() : "";
    }

    public static String getAvatarIcon(@Nullable JiraIssueUser user) {
        return nonNull(user) ? user.getAvatarIcon(): "";
    }

    public static String getIssueType(@NotNull JiraIssue jiraIssue) {
        return nonNull(jiraIssue.getIssuetype()) ? jiraIssue.getIssuetype().getName() : "";
    }

    public static String getPriority(@NotNull JiraIssue jiraIssue) {
        return nonNull(jiraIssue.getPriority()) ? jiraIssue.getPriority().getName() : "";
    }

    public static String getStatus(@NotNull JiraIssue jiraIssue) {
        return jiraIssue.getStatus().getName();
    }

    public static String getCreated(@NotNull JiraIssue jiraIssue) {
        return getPrettyDateTime(jiraIssue.getCreated());
    }

    public static String getUpdated(@NotNull JiraIssue jiraIssue) {
        return getPrettyDateTime(jiraIssue.getUpdated());
    }

    public static String getCreated(@NotNull JiraIssueComment comment) {
        return getPrettyDateTime(comment.getCreated());
    }

    public static String getPrettyBody(String body){
        Matcher m = BODY_NAME_PATTERN.matcher(body);
        if(m.find()){
            body = body.replace(m.group(1), m.group(2));
        }

        return body;
    }

    public static String escapeComment(String body){
        return body.replaceAll("\r\n", "\\\\n");
    }

    private static String getPrettyDateTime(Date date){
        return DateFormatUtil.formatPrettyDateTime(date);
    }

}
