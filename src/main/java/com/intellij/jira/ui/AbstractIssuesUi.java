package com.intellij.jira.ui;

import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.jira.ui.highlighters.JiraIssueHighlighter;
import com.intellij.jira.ui.highlighters.JiraIssueHighlighterFactory;
import com.intellij.jira.ui.highlighters.JiraIssueHighlighterProperty;
import com.intellij.jira.ui.table.column.JiraIssueApplicationSettings;
import com.intellij.openapi.application.ApplicationManager;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.intellij.jira.actions.HighlightersActionGroup.JIRA_ISSUE_HIGHLIGHTER_FACTORY_EP;

public abstract class AbstractIssuesUi implements IssuesUi {

    private final String myId;
    protected final JiraIssuesData myIssuesData;
    protected final JQLSearcher mySearcher;
    protected final Map<String, JiraIssueHighlighter> myHighlighters = new HashMap<>();

    protected AbstractIssuesUi(String id, JiraIssuesData issuesData, JQLSearcher searcher) {
        myId = id;
        myIssuesData = issuesData;
        mySearcher = searcher;

        ApplicationManager.getApplication().getService(JiraIssueApplicationSettings.class)
                .addChangeListener(new MyPropertyChangeListener());
    }

    @NotNull
    @Override
    public String getId() {
        return myId;
    }

    @Override
    public void dispose() {
        myHighlighters.clear();
    }

    protected void updateHighlighters() {
        myHighlighters.forEach((id, highlighter) -> getTable().removeHighlighter(highlighter));
        myHighlighters.clear();

        for (JiraIssueHighlighterFactory factory : JIRA_ISSUE_HIGHLIGHTER_FACTORY_EP.getExtensionList()) {
            JiraIssueApplicationSettings properties = ApplicationManager.getApplication().getService(JiraIssueApplicationSettings.class);
            JiraIssueHighlighterProperty highlighterProperty = JiraIssueHighlighterProperty.get(factory.getId());
            if (properties.get(highlighterProperty)) {
                JiraIssueHighlighter highlighter = factory.createHighlighter(myIssuesData);
                myHighlighters.put(factory.getId(), highlighter);
                getTable().addHighlighter(highlighter);
            }
        }

        getTable().repaint();
    }

    private class MyPropertyChangeListener implements JiraIssueUiProperties.PropertyChangeListener {

        @Override
        public <T> void onChanged(JiraIssueUiProperties.@NotNull JiraIssueUiProperty<T> property) {
            updateHighlighters();
        }
    }

}
