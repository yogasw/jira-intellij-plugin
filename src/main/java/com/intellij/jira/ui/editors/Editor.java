package com.intellij.jira.ui.editors;

import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

public interface Editor {

    JComponent createPanel();

    @Nullable
    ValidationInfo validate();

}
