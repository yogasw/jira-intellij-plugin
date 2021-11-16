package com.intellij.jira.util;

import com.intellij.jira.ui.JiraUi;
import com.intellij.jira.ui.panels.JiraTabPanel;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import com.intellij.ui.content.TabDescriptor;
import com.intellij.ui.content.TabGroupId;
import com.intellij.ui.content.TabbedContent;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import java.awt.Component;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class JiraContentUtil {

    private JiraContentUtil() { }

    public static Content findTabbedContent(@NotNull ContentManager manager, @NotNull String name) {
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

    public static void closeTab(@NotNull ContentManager manager, @NotNull String name) {
        for (Content content : manager.getContents()) {
            if (content instanceof TabbedContent) {
                var tab = ((TabbedContent) content).getTabs().stream()
                        .filter(pair -> pair.getFirst().equals(name))
                        .findFirst();

                tab.ifPresent(pair -> ((TabbedContent) content).removeContent(pair.getSecond()));

                if (((TabbedContent) content).getTabs().isEmpty()) {
                    manager.removeContent(content, true);
                }
            }
        }

    }


    public static void updateTabName(@NotNull ContentManager manager, String previousName, String newName) {
        for (Content content : manager.getContents()) {
            if (content instanceof TabbedContent) {
                var tab2 = ((TabbedContent) content).getTabs().stream()
                        .filter(pair -> pair.getFirst().equals(previousName))
                        .findFirst();

                tab2.ifPresent(pair -> {
                    TabGroupId groupId = content.getUserData(Content.TAB_GROUP_ID_KEY);
                    TabDescriptor tab = content.getUserData(Content.TAB_DESCRIPTOR_KEY);
                    if (groupId != null && tab != null) {
                        content.setDisplayName(newName);
                    }
                });

            }
        }
    }

    @Nullable
    public static JiraUi getIssuesUi(@NotNull JComponent c) {
        return ContainerUtil.getFirstItem(getIssuesUis(c));
    }

    public static List<JiraUi> getIssuesUis(@NotNull JComponent c) {
        Set<JiraTabPanel> panels = new HashSet<>();
        collectIssuesPanelInstances(c, panels);

        return ContainerUtil.map(panels, JiraTabPanel::getUi);
    }

    private static void collectIssuesPanelInstances(@NotNull JComponent component, @NotNull Set<JiraTabPanel> result) {
        if (component instanceof JiraTabPanel) {
            result.add((JiraTabPanel)component);
            return;
        }
        for (Component childComponent : component.getComponents()) {
            if (childComponent instanceof JComponent) {
                collectIssuesPanelInstances((JComponent)childComponent, result);
            }
        }
    }
}
