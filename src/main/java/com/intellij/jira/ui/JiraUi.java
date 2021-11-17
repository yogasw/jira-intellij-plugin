package com.intellij.jira.ui;

import com.intellij.openapi.Disposable;
import org.jetbrains.annotations.NotNull;
import javax.swing.JComponent;

/**
 * Represents a Tab in Jira Window
 */
public interface JiraUi extends Disposable {

    @NotNull
    String getId();

    @NotNull
    JComponent getMainComponent();

}
