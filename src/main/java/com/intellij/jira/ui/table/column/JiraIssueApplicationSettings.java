package com.intellij.jira.ui.table.column;

import com.intellij.jira.ui.JiraIssueUiProperties;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.EventDispatcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;


@State(name = "Jira.Issues.App.Settings", storages = @Storage("issues.xml"))
public class JiraIssueApplicationSettings implements PersistentStateComponent<JiraIssueApplicationSettings.State>, JiraIssueUiProperties {

    private final EventDispatcher<PropertyChangeListener> myEventDispatcher = EventDispatcher.create(PropertyChangeListener.class);

    private State myState = new State();

    @Override
    public @Nullable State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.myState = state;
    }

    @Override
    public <T> @NotNull T get(@NotNull JiraIssueUiProperty<T> property) {
        if (property instanceof JiraIssueColumnProperties.TableColumnVisibilityProperty) {
            JiraIssueColumnProperties.TableColumnVisibilityProperty visibilityProperty = (JiraIssueColumnProperties.TableColumnVisibilityProperty)property;
            Boolean isVisible = myState.COLUMN_ID_VISIBILITY.get(visibilityProperty.getName());
            if (isVisible != null) {
                return (T)isVisible;
            }

            if (JiraIssueColumnManager.getInstance().getCurrentColumns().contains(visibilityProperty.getColumn())) {
                return (T)Boolean.TRUE;
            }

        }

        return (T)Boolean.FALSE;
    }

    @Override
    public <T> void set(@NotNull JiraIssueUiProperty<T> property, @NotNull T value) {
        if (property instanceof JiraIssueColumnProperties.TableColumnVisibilityProperty) {
            myState.COLUMN_ID_VISIBILITY.put(property.getName(), (Boolean)value);
        }

        myEventDispatcher.getMulticaster().onChanged(property);
    }

    @Override
    public <T> boolean exists(@NotNull JiraIssueUiProperty<T> property) {
        return property instanceof JiraIssueColumnProperties.TableColumnVisibilityProperty;
    }

    @Override
    public void addChangeListener(@NotNull PropertyChangeListener listener) {
        myEventDispatcher.addListener(listener);
    }

    @Override
    public void removeChangeListener(@NotNull PropertyChangeListener listener) {
        myEventDispatcher.removeListener(listener);
    }

    public static class State {
        public Map<String, Boolean> COLUMN_ID_VISIBILITY = new HashMap<>();
    }

}
