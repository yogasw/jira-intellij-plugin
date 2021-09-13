package com.intellij.jira.ui.table.column;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.util.JiraIssueUtil;
import org.jetbrains.annotations.Nullable;

public class IssueType extends StringColumn<JiraIssue> {

    public static final IssueType INSTANCE = new IssueType();

    private IssueType() {
        super("Default.IssueType", "Type");
    }

    @Override
    public @Nullable String valueOf(JiraIssue issue) {
        return JiraIssueUtil.getIssueType(issue);
    }
}
