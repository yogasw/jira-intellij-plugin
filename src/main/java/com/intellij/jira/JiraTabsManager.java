package com.intellij.jira;

import com.intellij.jira.jql.JQLSearcherManager;
import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.jira.ui.IssuesUi;
import com.intellij.jira.ui.IssuesUiFactory;
import com.intellij.openapi.project.Project;
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

import static com.intellij.jira.ui.JiraToolWindowFactory.TOOL_WINDOW_ID;

public class JiraTabsManager {

    public static final String TAB_ISSUES = "Issues";

    private final Project myProject;

    public JiraTabsManager(@NotNull Project myProject) {
        this.myProject = myProject;
    }

    public void openIssuesTab() {
        ContentManager contentManager = getContentManager();

        IssuesUi issuesUi = createIssuesUi();
        Content content = ContentFactory.SERVICE.getInstance().createContent(issuesUi.getMainComponent(), TAB_ISSUES, false);
        content.setCloseable(false);

        ContentsUtil.addContent(contentManager, content, true);
    }

    public void openFilteredIssuesTab() {
        IssuesUi filteredIssuesUi = createFilteredIssuesUi();
        openTab(filteredIssuesUi, new TabGroupId("FilteredIssuesGroup", () -> "Filter", false));
    }

    private void openTab(@NotNull IssuesUi issuesUi, @NotNull TabGroupId tabGroupId) {
        ContentManager contentManager = getContentManager();

        TabDescriptor tabDescriptor = new TabDescriptor(issuesUi.getMainComponent(), issuesUi::getId, issuesUi);
        Content content = contentManager.findContent(tabGroupId.getDisplayName(tabDescriptor));
        if (content == null) {
            ContentUtilEx.addTabbedContent(contentManager, tabGroupId, tabDescriptor, true);
            return;
        }

        contentManager.setSelectedContent(content);
    }

    private IssuesUi createIssuesUi() {
        return IssuesUiFactory.createDefaultUi(new JiraIssuesData(myProject));
    }

    private IssuesUi createFilteredIssuesUi() {
        return IssuesUiFactory.createFilteredUi(generateId() ,new JiraIssuesData(myProject));
    }

    private String generateId() {
        JQLSearcher selectedSearcher = JQLSearcherManager.getInstance().getSelectedSearcher(myProject);
        return selectedSearcher.getAlias();
    }

    private ContentManager getContentManager() {
        ToolWindow jiraToolWindow = ToolWindowManager.getInstance(myProject).getToolWindow(TOOL_WINDOW_ID);
        return jiraToolWindow.getContentManager();
    }

    public static JiraTabsManager getInstance(@NotNull Project project) {
        return project.getService(JiraTabsManager.class);
    }

}
