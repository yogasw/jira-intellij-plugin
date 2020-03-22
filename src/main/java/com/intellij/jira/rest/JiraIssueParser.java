package com.intellij.jira.rest;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.intellij.jira.rest.model.*;
import com.intellij.tasks.jira.JiraRepository;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class JiraIssueParser {
    private static final Type ISSUES_WRAPPER_TYPE = (new TypeToken<JiraIssuesWrapper<JiraIssue>>(){}).getType();
    private static final Type ISSUE_TRANSITION_WRAPPER_TYPE = (new TypeToken<JiraIssueTransitionsWrapper<JiraIssueTransition>>(){}).getType();

    private JiraIssueParser() { }


    public static JiraIssue parseIssue(String response){
        return JiraRepository.GSON.fromJson(response, JiraIssue.class);
    }


    public static List<JiraIssue> parseIssues(String response){
        JiraIssuesWrapper<JiraIssue> wrapper = JiraRepository.GSON.fromJson(response, ISSUES_WRAPPER_TYPE);
        if(wrapper == null){
            return new ArrayList<>();
        }
        return wrapper.getIssues();
    }

    public static List<JiraIssueTransition> parseIssueTransitions(String response){
        JiraIssueTransitionsWrapper<JiraIssueTransition> wrapper = JiraRepository.GSON.fromJson(response, ISSUE_TRANSITION_WRAPPER_TYPE);
        if(wrapper == null){
            return new ArrayList<>();
        }
        return wrapper.getTransitions();
    }

    public static JiraIssueUser parseUser(String response) {
        return JiraRepository.GSON.fromJson(response, JiraIssueUser.class);
    }

    public static List<JiraIssueUser> parseUsers(String response) {
        return Arrays.asList(JiraRepository.GSON.fromJson(response, JiraIssueUser[].class));
    }

    public static JiraIssueComment parseIssueComment(String response){
        return JiraRepository.GSON.fromJson(response, JiraIssueComment.class);
    }

    public static List<JiraIssuePriority> parseIssuePriorities(String response){
        return Arrays.asList(JiraRepository.GSON.fromJson(response, JiraIssuePriority[].class));
    }

    public static List<JiraIssueLinkType> parseIssueLinkTypes(String response){
        JiraIssueLinkTypesWrapper wrapper = JiraRepository.GSON.fromJson(response, JiraIssueLinkTypesWrapper.class);
        if(wrapper == null){
            return new ArrayList<>();
        }

        return wrapper.getIssueLinkTypes();
    }

    public static List<JiraGroup> parseGroups(String response){
        JiraGroupsWrapper wrapper = JiraRepository.GSON.fromJson(response, JiraGroupsWrapper.class);
        if(wrapper == null){
            return new ArrayList<>();
        }

        return wrapper.getGroups();
    }

    public static List<String> parseRoles(String response){
        return new ArrayList<>(JiraRepository.GSON.fromJson(response, JsonObject.class).keySet());
    }

    public static LinkedHashMap<String, JiraPermission> parsePermissions(String response){
        return JiraRepository.GSON.fromJson(response, JiraPermissionsWrapper.class).getPermissions();
    }

    public static JiraIssueWorklog parseIssueWorklog(String response){
        return JiraRepository.GSON.fromJson(response, JiraIssueWorklog.class);
    }

    public static List<JiraIssueAttachment> parseIssueAttachments(String response){
        return Arrays.asList(JiraRepository.GSON.fromJson(response, JiraIssueAttachment[].class));
    }

}
