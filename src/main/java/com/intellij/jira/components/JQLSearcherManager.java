package com.intellij.jira.components;

import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.jira.util.SimpleSelectableList;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.BaseComponent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

public class JQLSearcherManager implements BaseComponent {

    private SimpleSelectableList<JQLSearcher> mySearchers = new SimpleSelectableList<>();


    public static JQLSearcherManager getInstance(){
        return ApplicationManager.getApplication().getComponent(JQLSearcherManager.class);
    }

    public List<JQLSearcher> getSearchers(Project project) {
        return getSimpleSelectableList(project).getItems();
    }

    public JQLSearcher getSelectedSearcher(Project project){
        return getSimpleSelectableList(project).hasSelectedItem() ? getSimpleSelectableList(project).getSelectedItem() : null;
    }

    public int getSelectedSearcherIndex(Project project){
        return getSimpleSelectableList(project).getSelectedItemIndex();
    }


    public boolean hasSelectedSearcher(){
        return mySearchers.hasSelectedItem();
    }

    public void setSearchers(Project project, SimpleSelectableList<JQLSearcher> searcherList) {
        updateProjectSearchers(project, searcherList);
        updateApplicationSearchers(searcherList);
    }

    public void add(Project project, JQLSearcher searcher, boolean selected){
        SimpleSelectableList<JQLSearcher> simpleSelectableList = getSimpleSelectableList(project);
        simpleSelectableList.add(searcher, selected);
        updateProjectSearchers(project, simpleSelectableList);
        updateApplicationSearchers(simpleSelectableList);
    }

    public void update(Project project, String oldAliasSearcher, JQLSearcher updatedSearcher, boolean selected){
        JQLSearcher oldSearcher = findByAlias(project, oldAliasSearcher);
        if(isNull(oldSearcher)){
            return;
        }

        SimpleSelectableList<JQLSearcher> simpleSelectableList = getSimpleSelectableList(project);
        simpleSelectableList.update(simpleSelectableList.getItems().indexOf(oldSearcher), updatedSearcher, selected);
        updateProjectSearchers(project, simpleSelectableList);
        updateApplicationSearchers(simpleSelectableList);
    }

    @Nullable
    private JQLSearcher findByAlias(Project project, String alias){
        return getSimpleSelectableList(project).getItems().stream()
                .filter(searcher -> searcher.getAlias().equals(alias))
                .findFirst().orElse(null);
    }

    private void updateProjectSearchers(Project project, SimpleSelectableList<JQLSearcher> searcherList){
        List<JQLSearcher> projectSearchers = searcherList.getItems().stream().filter(searcher -> !searcher.isShared()).collect(Collectors.toList());
        getJqlSearcherProjectManager(project).setSearchers(projectSearchers, searcherList.getSelectedItemIndex());
        getJqlSearcherProjectManager(project).notifyObservers(searcherList.getItems());
    }

    private void updateApplicationSearchers(SimpleSelectableList<JQLSearcher> searcherList){
        List<JQLSearcher> applicationSearchers = searcherList.getItems().stream().filter(JQLSearcher::isShared).collect(Collectors.toList());
        getJqlSearcherApplicationManager().setSearchers(applicationSearchers);
    }

    private JQLSearcherApplicationManager getJqlSearcherApplicationManager(){
        return JQLSearcherApplicationManager.getInstance();
    }

    private JQLSearcherProjectManager getJqlSearcherProjectManager(Project project){
        return project.getComponent(JQLSearcherProjectManager.class);
    }

    private SimpleSelectableList<JQLSearcher> getSimpleSelectableList(Project project){
        SimpleSelectableList<JQLSearcher> selectableList = new SimpleSelectableList<>();
        selectableList.addAll(getJqlSearcherApplicationManager().getSearchers());
        selectableList.addAll(getJqlSearcherProjectManager(project).getSearchers());
        selectableList.selectItem(getJqlSearcherProjectManager(project).getSelectedSearcherIndex());

        return selectableList;
    }

}
