package com.intellij.jira.ui;

import com.intellij.ui.components.JBTabbedPane;
import com.intellij.util.ui.JBUI;

public class JiraTabbedPane extends JBTabbedPane {

    public JiraTabbedPane(int tabPlacement) {
        super(tabPlacement);
        setTabComponentInsets(JBUI.insets(0));
    }

}
