package com.intellij.jira.components;

import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.jira.util.SimpleSelectableList;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.BaseComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

public class JQLSearcherManager implements BaseComponent {

    public static JQLSearcherManager getInstance(){
        return ApplicationManager.getApplication().getComponent(JQLSearcherManager.class);
    }

    public List<JQLSearcher> getSearchers(Project project) {
        return getSimpleSelectableList(project).getItems();
    }

    public JQLSearcher getSelectedSearcher(Project project){
        SimpleSelectableList<JQLSearcher> simpleSelectableList = getSimpleSelectableList(project);
        return simpleSelectableList.hasSelectedItem() ? simpleSelectableList.getSelectedItem() : null;
    }

    public int getSelectedSearcherIndex(Project project){
        return getSimpleSelectableList(project).getSelectedItemIndex();
    }

    public boolean hasSelectedSearcher(Project project){
        return getSimpleSelectableList(project).hasSelectedItem();
    }

    public void setSearchers(Project project, SimpleSelectableList<JQLSearcher> searcherList) {
        updateApplicationSearchers(searcherList);
        updateProjectSearchers(project, searcherList);
    }

    public void add(Project project, JQLSearcher searcher, boolean selected){
        SimpleSelectableList<JQLSearcher> simpleSelectableList = getSimpleSelectableList(project);
        simpleSelectableList.add(searcher, selected);

        updateApplicationSearchers(simpleSelectableList);
        updateProjectSearchers(project, simpleSelectableList);
    }

    public void update(Project project, String oldAliasSearcher, JQLSearcher updatedSearcher, boolean selected){
        JQLSearcher oldSearcher = findByAlias(project, oldAliasSearcher);
        if(isNull(oldSearcher)){
            return;
        }

        SimpleSelectableList<JQLSearcher> simpleSelectableList = getSimpleSelectableList(project);
        simpleSelectableList.update(simpleSelectableList.getItems().indexOf(oldSearcher), updatedSearcher, selected);

        updateApplicationSearchers(simpleSelectableList);
        updateProjectSearchers(project, simpleSelectableList);
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

        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        for(Project p : openProjects){
            getJqlSearcherProjectManager(p).notifyObservers(getSimpleSelectableList(p).getItems());
        }
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
        SimpleSelectableList<JQLSearcher> selectableList = SimpleSelectableList.of(getJqlSearcherApplicationManager().getSearchers());

        JQLSearcherProjectManager jqlSearcherProjectManager = getJqlSearcherProjectManager(project);
        selectableList.addAll(jqlSearcherProjectManager.getSearchers());
        if(jqlSearcherProjectManager.hasSelectedSearcher()){
            selectableList.selectItem(jqlSearcherProjectManager.getSelectedSearcherIndex());
        }

        return selectableList;
    }

}
