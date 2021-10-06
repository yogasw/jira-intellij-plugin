package com.intellij.jira.ui.labels;

import com.intellij.jira.rest.model.JiraIssueStatus;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import static com.intellij.jira.util.JiraLabelUtil.IN_PROGRESS_TEXT_COLOR;

public class JiraStatusDarkLabel extends JiraStatusLightLabel {

    public JiraStatusDarkLabel(@NotNull JiraIssueStatus status) {
        super(status);
        setBorder(JBUI.Borders.empty(4, 5));
        setBackground(status.getCategoryColor());
        setForeground(status.isInProgressCategory() ? IN_PROGRESS_TEXT_COLOR : JBColor.WHITE);
    }
}
