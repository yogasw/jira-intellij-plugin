package com.intellij.jira.ui.table.column;

import com.intellij.openapi.components.Service;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JiraIssueColumnManager {

    private static final JiraIssueColumnManager INSTANCE = new JiraIssueColumnManager();

    private static final List<JiraIssueColumn<?, ?>> defaultColumns = List.of(IssueType.INSTANCE, Priority.INSTANCE, Key.INSTANCE,
                                                                            Summary.INSTANCE, Assignee.INSTANCE, Status.INSTANCE,
                                                                            ProjectKey.INSTANCE, Created.INSTANCE);

    private static final Map<Integer, JiraIssueColumn<?, ?>> currentColumns = new HashMap<>();
    private static final Map<String, Integer> currentColumnIndexes = new HashMap<>();
    private static final Map<String, String> myMaxColumnWidth = new HashMap<>();
    private static final Map<JiraIssueColumn<?, ?>, JiraIssueColumnProperties> currentColumnProperties = new LinkedHashMap<>();

    static {
        defaultColumns.forEach(column -> {
            int index = currentColumnIndexes.size();
            currentColumns.put(index, column);
            currentColumnIndexes.put(column.getId(), index);
            currentColumnProperties.put(column, new JiraIssueColumnProperties(column));
        });
    }

    @NotNull
    public static JiraIssueColumnManager getInstance() {
        return INSTANCE;
    }

    public int getColumnsCount() {
        return currentColumns.size();
    }

    public JiraIssueColumn<?, ?> getColumn(int columnIndex) {
        return currentColumns.get(columnIndex);
    }

    public Integer getColumnIndex(JiraIssueColumn<?, ?> column) {
        return currentColumnIndexes.get(column.getId());
    }

    public List<JiraIssueColumn<?, ?>> getCurrentColumns() {
        return new ArrayList<>(currentColumns.values());
    }

    public List<JiraIssueColumn<?, ?>> getHideableColumns() {
        return currentColumns.values().stream().filter(JiraIssueColumn::isHideable).collect(Collectors.toList());
    }

    public String getColumnWidth(String columnId) {
        return myMaxColumnWidth.getOrDefault(columnId, "");
    }

    public void setColumnWidth(String columnId, String value) {
        myMaxColumnWidth.put(columnId, value);
    }

    public JiraIssueColumnProperties getColumnProperties(JiraIssueColumn<?, ?> column) {
        return currentColumnProperties.get(column);
    }

}
