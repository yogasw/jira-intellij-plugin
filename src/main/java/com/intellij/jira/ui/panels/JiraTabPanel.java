package com.intellij.jira.ui.panels;

import com.intellij.jira.ui.JiraUi;
import com.intellij.ui.components.JBPanel;

import java.awt.BorderLayout;

import static java.awt.BorderLayout.CENTER;

public class JiraTabPanel extends JBPanel<JiraTabPanel> {

    private final JiraUi myUi;


    public JiraTabPanel(JiraUi myUi) {
        super(new BorderLayout());
        this.myUi = myUi;
        add(myUi.getMainComponent(), CENTER);
    }

    public JiraUi getUi() {
        return myUi;
    }
}
