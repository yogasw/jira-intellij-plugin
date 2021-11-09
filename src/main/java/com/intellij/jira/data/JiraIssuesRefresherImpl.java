package com.intellij.jira.data;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.server.JiraServerManager;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.CoreProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.vcs.log.data.SingleTaskController;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class JiraIssuesRefresherImpl implements JiraIssuesRefresher, Disposable {

    private final Project myProject;
    private final JiraProgress myProgress;

    private final SingleTaskController<RefreshRequest, List<JiraIssue>> mySingleTaskController;

    private List<VisibleIssueChangeListener> myVisibleIssueChangeListeners = new ArrayList<>();

    public JiraIssuesRefresherImpl(@NotNull Project project, @NotNull JiraProgress progress) {
        myProject = project;
        myProgress = progress;

        mySingleTaskController = new SingleTaskController<>("", issues -> {
            for (VisibleIssueChangeListener l : myVisibleIssueChangeListeners) {
                l.onChange(issues);
            }
        }, this) {
            @Override
            protected @NotNull SingleTask startNewBackgroundTask() {
                return JiraIssuesRefresherImpl.this.startNewBackgroundTask(new MyRefreshTask());
            }
        };
    }

    protected SingleTaskController.SingleTask startNewBackgroundTask(@NotNull final Task.Backgroundable refreshTask) {
        ProgressIndicator indicator = myProgress.createProgressIndicator();
        Future<?> future = ((CoreProgressManager) ProgressManager.getInstance()).runProcessWithProgressAsynchronously(refreshTask, indicator,
                null);
        return new SingleTaskController.SingleTaskImpl(future, indicator);
    }

    @Override
    public void addVisibleIssueChangeListener(VisibleIssueChangeListener listener) {
        myVisibleIssueChangeListeners.add(listener);
    }

    @Override
    public void removeVisibleIssueChangeListener(VisibleIssueChangeListener listener) {
        myVisibleIssueChangeListeners.remove(listener);
    }

    @Override
    public void getIssues(String jql) {
        mySingleTaskController.request(new RefreshRequest(jql));
    }

    @Override
    public void dispose() {
        myVisibleIssueChangeListeners.clear();
    }

    public JiraProgress getProgress() {
        return myProgress;
    }

    public interface VisibleIssueChangeListener {
        void onChange(List<JiraIssue> issues);
    }

    @NotNull
    private JiraServerManager getJiraServerManager() {
        return JiraServerManager.getInstance();
    }

    private static class RefreshRequest {

        private final String jql;

        RefreshRequest(@NotNull String jql) {
            this.jql = jql;
        }

        public String getJql() {
            return jql;
        }
    }

    private class MyRefreshTask extends Task.Backgroundable {

        public MyRefreshTask() {
            super(JiraIssuesRefresherImpl.this.myProject, "Fetching");
        }

        @Override
        public void run(@NotNull ProgressIndicator indicator) {
            indicator.setIndeterminate(true);
            List<JiraIssue> issues = new ArrayList<>();
            while (true) {
                List<RefreshRequest> requests = mySingleTaskController.popRequests();
                List<String> jqls = requests.stream().map(RefreshRequest::getJql).collect(Collectors.toList());

                if (jqls.isEmpty()) {
                    mySingleTaskController.taskCompleted(issues);
                    break;
                }

                try {
                    issues = doRefresh(jqls);
                }
                catch (ProcessCanceledException e) {
                    mySingleTaskController.taskCompleted(null);
                    throw e;
                }
            }
        }

        private List<JiraIssue> doRefresh(List<String> jqls) {
            List<JiraIssue> issues = new ArrayList<>();
            for (String jql : jqls) {
                issues.addAll(getJiraServerManager().getJiraRestApi(myProject).getIssues(jql));
            }

            return issues;
        }
    }
}
