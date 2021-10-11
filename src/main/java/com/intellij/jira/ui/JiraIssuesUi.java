package com.intellij.jira.ui;

import com.intellij.jira.data.JiraIssuesData;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;

/**
 * Represents a Tab in Jira Window
 */
public interface JiraIssuesUi {

    @NotNull
    String getId();

    @NotNull
    JiraIssuesData getData();

    @NotNull
    JComponent getMainComponent();

}
