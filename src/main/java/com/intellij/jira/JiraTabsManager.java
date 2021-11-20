package com.intellij.jira;

import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.jql.JQLSearcherManager;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.jira.server.JiraServerManager;
import com.intellij.jira.ui.AbstractIssuesUi;
import com.intellij.jira.ui.JiraUi;
import com.intellij.jira.ui.JiraUiFactory;
import com.intellij.jira.ui.SearcherIssuesUi;
import com.intellij.jira.ui.panels.JiraServerNotConfiguredPanel;
import com.intellij.jira.ui.panels.JiraTabPanel;
import com.intellij.jira.util.JiraContentUtil;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.ui.content.TabDescriptor;
import com.intellij.ui.content.TabGroupId;
import com.intellij.util.ContentUtilEx;
import com.intellij.util.ContentsUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;

import static com.intellij.jira.server.JiraServerManager.JIRA_SERVER_CHANGED;
import static com.intellij.jira.server.JiraServerManager.JIRA_SERVER_REMOVED_ALL;
import static com.intellij.jira.ui.JiraToolWindowFactory.TOOL_WINDOW_ID;
import static com.intellij.jira.util.JiraContentUtil.getIssuesUi;

public class JiraTabsManager implements Disposable {

    public static final String TAB_ISSUES = "Issues";

    private final Project myProject;
    private final JiraIssuesData myIssueData;
    private boolean myServerConfigured;

    public JiraTabsManager(@NotNull Project project) {
        myProject = project;
        myIssueData = new JiraIssuesData(project, this);

        project.getMessageBus().connect().subscribe(JIRA_SERVER_REMOVED_ALL, this::openNotConfiguredServerTab);
        project.getMessageBus().connect().subscribe(JIRA_SERVER_CHANGED, () -> {
            if (myServerConfigured && JiraServerManager.getInstance().hasJiraServerConfigured(myProject)) {
                ApplicationManager.getApplication().invokeLater(() -> {
                        ContentManager manager = getContentManager();
                        JiraContentUtil.getAllJiraUis(manager).forEach(ui -> {
                            if (ui instanceof AbstractIssuesUi) {
                                ((AbstractIssuesUi) ui).refresh();
                            }
                        });
                });
            } else {
                openIssuesTab();
            }
        });

    }

    public void openIssuesTab() {
        // check if server is configured
        if (JiraServerManager.getInstance().hasJiraServerConfigured(myProject)) {
            ContentManager contentManager = getContentManager();
            SearcherIssuesUi searcherIssuesUi = new SearcherIssuesUi(myIssueData);

            Disposer.register(this, searcherIssuesUi);

            Content content = ContentFactory.SERVICE.getInstance().createContent(new JiraTabPanel(searcherIssuesUi), TAB_ISSUES, false);
            content.setCloseable(false);

            ContentsUtil.addContent(contentManager, content, true);
            myServerConfigured = true;
            searcherIssuesUi.refresh();
        } else {
            openNotConfiguredServerTab();
            myServerConfigured = false;
        }
    }

    public void openFilteredIssuesTab(JQLSearcher searcher) {
        AbstractIssuesUi filteredIssuesUi = createFilteredIssuesUi(searcher);
        openTab(filteredIssuesUi, new TabGroupId("FilteredIssuesGroup", () -> "Filter", false));
        filteredIssuesUi.refresh();
    }

    public void openDetailsIssueTab(String issueKey) {
        JiraUi detailsIssueUi = createDetailsIssueUi(issueKey);
        openTab(detailsIssueUi, new TabGroupId("DetailsIssueGroup", () -> issueKey, true));
    }

    public void openNotConfiguredServerTab() {
        ContentManager contentManager = getContentManager();
        contentManager.removeAllContents(true);

        JComponent component = new JiraServerNotConfiguredPanel(myProject);

        Content content = ContentFactory.SERVICE.getInstance().createContent(component, null, false);
        content.setCloseable(false);

        ContentsUtil.addContent(contentManager, content, true);
    }

    public void closeTab(@NotNull String name) {
        JiraContentUtil.closeTab(getContentManager(), name);
    }

    public void updateTabName(JiraUi ui) {
        ContentManager manager = getContentManager();
        JComponent component = ContentUtilEx.findContentComponent(manager, c -> ui == getIssuesUi(c));
        if (component == null) return;

        ContentUtilEx.updateTabbedContentDisplayName(manager, component);
    }

    private void openTab(@NotNull JiraUi jiraUi, @NotNull TabGroupId tabGroupId) {
        Disposer.register(this, jiraUi);
        ContentManager contentManager = getContentManager();

        TabDescriptor tabDescriptor = new TabDescriptor(new JiraTabPanel(jiraUi), jiraUi::getId, jiraUi);
        String tabName = tabGroupId.getDisplayName(tabDescriptor);
        Content content = contentManager.findContent(tabName);
        if (content == null) {
            content = JiraContentUtil.findTabbedContent(contentManager, jiraUi.getId());
        }

        if (content == null) {
            ContentUtilEx.addTabbedContent(contentManager, tabGroupId, tabDescriptor, true);
            return;
        }

        contentManager.setSelectedContent(content);
    }

    private AbstractIssuesUi createIssuesUi() {
        return JiraUiFactory.createIssuesUi(myIssueData);
    }

    private AbstractIssuesUi createFilteredIssuesUi(JQLSearcher searcher) {
        return JiraUiFactory.createFilteredIssuesUi(myIssueData, searcher);
    }

    private JiraUi createDetailsIssueUi(String issueKey) {
        return JiraUiFactory.createDetailsIssueUi(issueKey, myIssueData);
    }

    private JQLSearcher getSelectedJQLSearcher() {
        return JQLSearcherManager.getInstance().getSelectedSearcher(myProject);
    }

    private ContentManager getContentManager() {
        ToolWindow jiraToolWindow = ToolWindowManager.getInstance(myProject).getToolWindow(TOOL_WINDOW_ID);
        return jiraToolWindow.getContentManager();
    }

    public static JiraTabsManager getInstance(@NotNull Project project) {
        return project.getService(JiraTabsManager.class);
    }

    @Override
    public void dispose() {

    }
}
