package com.intellij.jira.ui;

import org.jetbrains.annotations.NotNull;
import javax.swing.JComponent;

/**
 * Represents a Tab in Jira Window
 */
public interface JiraUi {

    @NotNull
    String getId();

    @NotNull
    JComponent getMainComponent();

}
