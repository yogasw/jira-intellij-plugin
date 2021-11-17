package com.intellij.jira.data;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.progress.ProgressIndicator;
import org.jetbrains.annotations.NotNull;

public interface JiraProgress {

    void addProgressListener(JiraProgressListener listener, @NotNull Disposable parent);

    void removeProgressListener(JiraProgressListener listener);

    ProgressIndicator createProgressIndicator();

    interface JiraProgressListener {

        void start();

        void finish();
    }
}
