package com.intellij.jira.ui.panels;

import com.intellij.jira.components.JiraIssueUpdater;
import com.intellij.jira.events.JiraIssueEventListener;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBSplitter;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;
import java.util.List;

public class JiraIssuePanel extends JBSplitter implements JiraIssueEventListener, Disposable {

    private Project myProject;
    private JiraIssue issue;


    public JiraIssuePanel(@NotNull Project project, JiraIssue issue) {
        super(false);
        this.myProject = project;
        this.issue = issue;
        init();
        addListeners();
    }

    private void init() {
        setFirstComponent(new First().getComponent());
        setSecondComponent(new Second().getComponent());
        setDividerWidth(1);
    }

    @Override
    public void update(List<JiraIssue> issues) {
        // Do nothing
    }

    @Override
    public void update(JiraIssue issue) {
        if (this.issue.getKey().equals(issue.getKey())) {
            this.issue = issue;
            init();
            revalidate();
            repaint();
        }
    }

    @Override
    public void dispose() {
        JiraIssueUpdater.getInstance(myProject).removeListener(this);
    }

    private void addListeners() {
        JiraIssueUpdater.getInstance(myProject).addListener(this);
    }

    private interface ComponentProvider {
        JComponent getComponent();
    }

    private class First implements ComponentProvider {

        @Override
        public JComponent getComponent() {
            return new JiraIssueDescriptionPanel(issue);
        }
    }

    private class Second implements ComponentProvider {

        @Override
        public JComponent getComponent() {
            JBSplitter splitter = new JBSplitter();
            splitter.setProportion(0.4f);
            splitter.setFirstComponent(new JiraIssueStatusPanel(myProject, issue));
            splitter.setSecondComponent(new JiraIssueActivityPanel(issue));
            splitter.setDividerWidth(1);

            return splitter;
        }
    }

}
