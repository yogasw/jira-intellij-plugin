package com.intellij.jira.util;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.JiraIssueComponent;
import com.intellij.jira.rest.model.JiraProjectVersion;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.util.List;
import java.util.stream.Collectors;

import static com.intellij.jira.util.JiraLabelUtil.EMPTY_TEXT;
import static java.awt.BorderLayout.LINE_START;
import static javax.swing.SwingConstants.CENTER;

public class JiraPanelUtil {

    public static final Border MARGIN_BOTTOM = JBUI.Borders.emptyBottom(10);

    public static JBPanel createWhiteGridPanel(int rows, int cols){
        return createWhitePanel(new GridLayout(rows, cols));
    }

    public static JBPanel createWhiteBorderPanel(){
        return createWhitePanel(new BorderLayout());
    }

    public static JBPanel createWhitePanel(@NotNull LayoutManager layout){
        return new JBPanel(layout)
                .withBackground(JBColor.WHITE);
    }

    public static JBPanel createPlaceHolderPanel(String text) {
        JBPanel panel = new JBPanel(new GridBagLayout());
        JBLabel messageLabel = new JBLabel(text);
        messageLabel.setHorizontalAlignment(CENTER);
        messageLabel.setVerticalAlignment(CENTER);
        panel.add(messageLabel, new GridBagConstraints());
        return panel;
    }

    public static JPanel createPanelWithVerticalLine(){
        return createPanelWithLine(false);
    }

    public static JPanel createPanelWithHorizontalLine(){
        return createPanelWithLine(true);
    }

    public static JPanel createTypePanel(@NotNull JiraIssue issue) {
        JBLabel typeLabel = JiraLabelUtil.createBoldLabel("Type: ");
        JBLabel typeValueLabel = JiraLabelUtil.createLabel(issue.getIssuetype().getName());

        return createWhiteBorderPanel(typeLabel, typeValueLabel);
    }

    public static JPanel createStatusPanel(@NotNull JiraIssue issue) {
        JBLabel statusLabel = JiraLabelUtil.createBoldLabel("Status: ");
        JLabel statusValueLabel = JiraLabelUtil.createStatusLabel(issue.getStatus());

        return createWhiteLeftFlowPanel(statusLabel, statusValueLabel);
    }

    public static JPanel createPriorityPanel(@NotNull JiraIssue issue) {
        JBLabel priorityLabel = JiraLabelUtil.createBoldLabel("Priority: ");
        JBLabel priorityValueLabel = JiraLabelUtil.createPriorityLabel(issue.getPriority());

        return createWhiteBorderPanel(priorityLabel, priorityValueLabel);
    }

    public static JPanel createAssigneePanel(@NotNull JiraIssue issue) {
        JBLabel assigneeLabel = JiraLabelUtil.createBoldLabel("Assignee: ");
        JBLabel assigneeValueLabel = JiraLabelUtil.createLabel(issue.getAssignee() != null ? issue.getAssignee().getDisplayName() : EMPTY_TEXT);

        return createWhiteBorderPanel(assigneeLabel, assigneeValueLabel);
    }

    public static JPanel createVersionsPanel(@NotNull JiraIssue issue) {
        JBLabel versionsLabel = JiraLabelUtil.createBoldLabel("Versions: ");
        JBLabel versionsValueLabel = JiraLabelUtil.createLabel(getVersionsNames(issue.getVersions()));

        return createWhiteBorderPanel(versionsLabel, versionsValueLabel);
    }

    public static JPanel createComponentsPanel(@NotNull JiraIssue issue) {
        JBLabel componentsLabel = JiraLabelUtil.createBoldLabel("Components: ");
        JBLabel componentsValueLabel = JiraLabelUtil.createLabel(getComponentNames(issue.getComponents()));

        return createWhiteBorderPanel(componentsLabel, componentsValueLabel);
    }

    public static JPanel createLabelsPanel(@NotNull JiraIssue issue) {
        JBLabel label = JiraLabelUtil.createBoldLabel("Labels: ");
        JBLabel valueLabel = JiraLabelUtil.createLabel(String.join(", ", issue.getLabels()));

        return createWhiteBorderPanel(label, valueLabel);
    }

    private static JPanel createPanelWithLine(boolean horizontal){
        return new JPanel(){
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                g.setColor(JBColor.border());
                if(horizontal){
                    g.drawLine(0,getHeight()/2, getWidth(), getHeight()/2);
                }else{
                    g.drawLine(getWidth()/2,0, getWidth()/2, getHeight());
                }
            }
        };
    }

    private static JPanel createWhiteBorderPanel(JLabel fieldName, JLabel fieldValue) {
        JPanel panel = createWhiteBorderPanel();
        panel.add(fieldName, LINE_START);
        panel.add(fieldValue, BorderLayout.CENTER);

        return panel;
    }

    private static JPanel createWhiteLeftFlowPanel(JLabel... labels) {
        JPanel panel = createWhitePanel(new FlowLayout(FlowLayout.LEFT, 0, 1));

        for (JLabel label : labels) {
            panel.add(label);
        }

        return panel;
    }

    private static String getVersionsNames(List<JiraProjectVersion> versions){
        if(versions.isEmpty()){
            return "None";
        }

        return versions.stream().map(JiraProjectVersion::getName)
                .collect(Collectors.joining(", "));
    }

    private static String getComponentNames(List<JiraIssueComponent> components){
        return components.stream()
                .map(JiraIssueComponent::getName)
                .collect(Collectors.joining(", "));
    }

}
