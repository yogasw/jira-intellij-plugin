package com.intellij.jira.ui.panels;

import com.intellij.jira.util.JiraBorders;
import com.intellij.openapi.roots.ui.componentsList.components.ScrollablePanel;
import com.intellij.openapi.ui.VerticalFlowLayout;

import java.awt.Color;

import static com.intellij.jira.util.JiraLabelUtil.WHITE;

public class JiraScrollablePanel extends ScrollablePanel {

    public JiraScrollablePanel() {
        super(new VerticalFlowLayout(VerticalFlowLayout.TOP, 0, 0, true, false));
        setOpaque(false);
        setBorder(JiraBorders.empty(5));
    }

    @Override
    public Color getBackground() {
        return WHITE;
    }

}
