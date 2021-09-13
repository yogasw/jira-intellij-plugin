package com.intellij.jira.ui.table.column;

import com.intellij.jira.rest.model.JiraIssue;
import org.jetbrains.annotations.Nullable;

public class Key extends StringColumn<JiraIssue> {

    public static final Key INSTANCE = new Key();

    private Key() {
        super("Default.Key", "Key");
    }

    @Override
    public @Nullable String valueOf(JiraIssue issue) {
        return issue.getKey();
    }


}
