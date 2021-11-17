package com.intellij.jira.jql;

import com.intellij.jira.listener.JQLSearcherListener;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.jira.util.ListComparator;
import com.intellij.jira.util.SimpleSelectableList;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.Topic;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.intellij.jira.jql.JQLSearcherApplicationManager.DEFAULT_JQL;
import static java.util.Objects.isNull;

public class JQLSearcherManager {

    @Topic.AppLevel
    public static final Topic<JQLSearcherListener> JQL_SEARCHERS_CHANGE = Topic.create("JQL change", JQLSearcherListener.class);

    public static JQLSearcherManager getInstance(){
        return ApplicationManager.getApplication().getService(JQLSearcherManager.class);
    }

    public List<JQLSearcher> getSearchers(Project project) {
        return getSimpleSelectableList(project).getItems();
    }

    public JQLSearcher getSelectedSearcher(Project project){
        SimpleSelectableList<JQLSearcher> simpleSelectableList = getSimpleSelectableList(project);
        return simpleSelectableList.hasSelectedItem() ? simpleSelectableList.getSelectedItem() : DEFAULT_JQL;
    }

    public int getSelectedSearcherIndex(Project project){
        return getSimpleSelectableList(project).getSelectedItemIndex();
    }

    public boolean hasSelectedSearcher(Project project){
        return getSimpleSelectableList(project).hasSelectedItem();
    }

    public void setSearchers(Project project, ListComparator.Result<JQLSearcher> result) {
        // TODO: update apps and project searchers and notify changed and removed
        SimpleSelectableList<JQLSearcher> list = SimpleSelectableList.of(result.getEdited());
        list.addAll(result.getNotEdited());
        list.addAll(result.getAdded());

        updateApplicationSearchers(list);
        updateProjectSearchers(project, list);


        ApplicationManager.getApplication().getMessageBus().syncPublisher(JQL_SEARCHERS_CHANGE).onChange(result.getEdited());
        ApplicationManager.getApplication().getMessageBus().syncPublisher(JQL_SEARCHERS_CHANGE).onRemoved(result.getRemoved());

    }

    public void add(Project project, JQLSearcher searcher, boolean selected){
        SimpleSelectableList<JQLSearcher> simpleSelectableList = getSimpleSelectableList(project);
        simpleSelectableList.add(searcher, selected);

        updateApplicationSearchers(simpleSelectableList);
        updateProjectSearchers(project, simpleSelectableList);

        ApplicationManager.getApplication().getMessageBus().syncPublisher(JQL_SEARCHERS_CHANGE).onChange(Collections.singletonList(searcher));

    }

    public void update(Project project, String oldAliasSearcher, JQLSearcher updatedSearcher, boolean selected){
        JQLSearcher oldSearcher = findById(project, oldAliasSearcher);
        if(isNull(oldSearcher)){
            return;
        }

        SimpleSelectableList<JQLSearcher> simpleSelectableList = getSimpleSelectableList(project);
        simpleSelectableList.update(simpleSelectableList.getItems().indexOf(oldSearcher), updatedSearcher, selected);

        updateApplicationSearchers(simpleSelectableList);
        updateProjectSearchers(project, simpleSelectableList);

        ApplicationManager.getApplication().getMessageBus().syncPublisher(JQL_SEARCHERS_CHANGE).onChange(Collections.singletonList(updatedSearcher));
    }

    public void setSelectedSearcher(Project project, int selectedSearcherIndex) {
        getJqlSearcherProjectManager(project).setSelectedSearcher(selectedSearcherIndex);
    }

    @Nullable
    private JQLSearcher findById(Project project, String id){
        return getSimpleSelectableList(project).getItems().stream()
                .filter(searcher -> searcher.getId().equals(id))
                .findFirst().orElse(null);
    }

    private void updateProjectSearchers(Project project, SimpleSelectableList<JQLSearcher> searcherList){
        List<JQLSearcher> projectSearchers = searcherList.getItems().stream().filter(searcher -> !searcher.isShared()).collect(Collectors.toList());
        getJqlSearcherProjectManager(project).setSearchers(projectSearchers, searcherList.getSelectedItemIndex());
    }

    private void updateApplicationSearchers(SimpleSelectableList<JQLSearcher> searcherList){
        List<JQLSearcher> applicationSearchers = searcherList.getItems().stream().filter(JQLSearcher::isShared).collect(Collectors.toList());
        getJqlSearcherApplicationManager().setSearchers(applicationSearchers);
    }

    private JQLSearcherApplicationManager getJqlSearcherApplicationManager(){
        return JQLSearcherApplicationManager.getInstance();
    }

    private JQLSearcherProjectManager getJqlSearcherProjectManager(Project project){
        return JQLSearcherProjectManager.getInstance(project);
    }

    public SimpleSelectableList<JQLSearcher> getSimpleSelectableList(Project project){
        SimpleSelectableList<JQLSearcher> selectableList = SimpleSelectableList.of(getJqlSearcherApplicationManager().getSearchers());

        JQLSearcherProjectManager jqlSearcherProjectManager = getJqlSearcherProjectManager(project);
        selectableList.addAll(jqlSearcherProjectManager.getSearchers());
        if(jqlSearcherProjectManager.hasSelectedSearcher()){
            selectableList.selectItem(jqlSearcherProjectManager.getSelectedSearcherIndex());
        }

        return selectableList;
    }

}
