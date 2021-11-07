package com.intellij.jira;

import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.jql.JQLSearcherManager;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.jira.server.JiraServerManager;
import com.intellij.jira.tasks.RefreshIssuesTask;
import com.intellij.jira.ui.JiraUi;
import com.intellij.jira.ui.JiraUiFactory;
import com.intellij.jira.ui.panels.JiraServerNotConfiguredPanel;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.ui.content.TabDescriptor;
import com.intellij.ui.content.TabGroupId;
import com.intellij.ui.content.TabbedContent;
import com.intellij.util.ContentUtilEx;
import com.intellij.util.ContentsUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;

import static com.intellij.jira.server.JiraServerManager.JIRA_SERVER_CHANGED;
import static com.intellij.jira.server.JiraServerManager.JIRA_SERVER_REMOVED_ALL;
import static com.intellij.jira.ui.JiraToolWindowFactory.TOOL_WINDOW_ID;

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
                ApplicationManager.getApplication().invokeLater(() -> new RefreshIssuesTask(myProject).queue());
            } else {
                openIssuesTab();
            }
        });

    }

    public void openIssuesTab() {
        // check if server is configured
        if (JiraServerManager.getInstance().hasJiraServerConfigured(myProject)) {
            ContentManager contentManager = getContentManager();
            JiraUi issuesUi = createIssuesUi();

            Content content = ContentFactory.SERVICE.getInstance().createContent(issuesUi.getMainComponent(), TAB_ISSUES, false);
            content.setCloseable(false);

            ContentsUtil.addContent(contentManager, content, true);
            myServerConfigured = true;
        } else {
            openNotConfiguredServerTab();
            myServerConfigured = false;
        }
    }

    public void openFilteredIssuesTab() {
        JiraUi filteredIssuesUi = createFilteredIssuesUi();
        openTab(filteredIssuesUi, new TabGroupId("FilteredIssuesGroup", () -> "Filter", false));
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

    private void openTab(@NotNull JiraUi jiraUi, @NotNull TabGroupId tabGroupId) {
        ContentManager contentManager = getContentManager();

        TabDescriptor tabDescriptor = new TabDescriptor(jiraUi.getMainComponent(), jiraUi::getId, jiraUi);
        String tabName = tabGroupId.getDisplayName(tabDescriptor);
        Content content = contentManager.findContent(tabName);
        if (content == null) {
            content = findTabbedContent(contentManager, jiraUi.getId());
        }

        if (content == null) {
            ContentUtilEx.addTabbedContent(contentManager, tabGroupId, tabDescriptor, true);
            return;
        }

        contentManager.setSelectedContent(content);
    }

    private JiraUi createIssuesUi() {
        return JiraUiFactory.createIssuesUi(myIssueData, getSelectedJQLSearcher());
    }

    private JiraUi createFilteredIssuesUi() {
        return JiraUiFactory.createFilteredIssuesUi(myIssueData, getSelectedJQLSearcher());
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

    private Content findTabbedContent(@NotNull ContentManager manager, @NotNull String name) {
        for (Content content : manager.getContents()) {
            if (content instanceof TabbedContent) {
                var tab = ((TabbedContent) content).getTabs().stream()
                        .filter(pair -> pair.getFirst().equals(name))
                        .findFirst();

                return tab.isPresent() ? content : null;
            }
        }

        return null;
    }

    @Override
    public void dispose() {

    }
}
