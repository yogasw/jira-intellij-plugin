package com.intellij.jira.ui.labels;


import com.intellij.ide.BrowserUtil;
import com.intellij.jira.util.JiraLabelUtil;
import com.intellij.ui.components.JBLabel;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class JiraLinkLabel extends JBLabel {

    private String url;

    public JiraLinkLabel(String text, String url) {
        super(text);
        this.url = url;
        init();
    }

    private void init(){
        setHorizontalAlignment(SwingUtilities.LEFT);
        setToolTipText(this.url);
        setCursor(JiraLabelUtil.HAND_CURSOR);
        setForeground(JiraLabelUtil.LINK_COLOR);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SwingUtilities.invokeLater(() -> BrowserUtil.open(url));
            }
        });
    }

}
