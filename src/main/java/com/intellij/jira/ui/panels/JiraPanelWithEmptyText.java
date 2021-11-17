package com.intellij.jira.ui.panels;

import com.intellij.ui.components.JBPanelWithEmptyText;
import org.jetbrains.annotations.NotNull;

public class JiraPanelWithEmptyText extends JBPanelWithEmptyText {

    public JiraPanelWithEmptyText(@NotNull String emptyText) {
        super();
        getEmptyText().setText(emptyText);
    }
}
