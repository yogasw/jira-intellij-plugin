package com.intellij.jira.ui;

import com.intellij.jira.data.JiraIssuesData;
import com.intellij.openapi.Disposable;
import org.jetbrains.annotations.NotNull;

public interface IssuesUi extends JiraUi, Disposable {

    @NotNull
    JiraIssuesData getData();

}
