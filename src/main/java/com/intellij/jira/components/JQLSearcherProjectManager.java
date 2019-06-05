package com.intellij.jira.components;

import com.intellij.configurationStore.XmlSerializer;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializationException;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Tag;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

@State(name = "JQLSearcherProjectManager", storages = @Storage(StoragePathMacros.WORKSPACE_FILE))
public class JQLSearcherProjectManager implements ProjectComponent, PersistentStateComponent<JQLSearcherProjectManager.Config> {

    private final Project myProject;

    private List<JQLSearcher> projectSearchers = new ArrayList<>();
    private int mySelectedSearcher = 0;

    private Config myConfig = new Config();

    protected JQLSearcherProjectManager(Project project) {
        this.myProject = project;
    }

    @Nullable
    @Override
    public Config getState() {
        myConfig.selected = mySelectedSearcher;
        myConfig.searchers = XmlSerializer.serialize(getSearchersAsArray());
        return myConfig;
    }

    @Override
    public void loadState(@NotNull Config config) {
        XmlSerializerUtil.copyBean(config, myConfig);

        projectSearchers.clear();
        Element element = config.searchers;
        List<JQLSearcher> searchers = loadSearchers(element);
        projectSearchers.addAll(searchers);

        mySelectedSearcher = config.selected;
    }

    private List<JQLSearcher> loadSearchers(Element element) {
        List<JQLSearcher> searchers = new ArrayList<>();
        if(nonNull(element)){
            for(Element o : element.getChildren()){
                try{
                    JQLSearcher searcher = XmlSerializer.deserialize(o, JQLSearcher.class);
                    searchers.add(searcher);
                }catch (XmlSerializationException e) {
                    //LOG.error(e.getMessage(), e);
                }
            }
        }

        return searchers;
    }


    public List<JQLSearcher> getSearchers() {
        return projectSearchers;
    }

    public int getSelectedSearcherIndex(){
        return mySelectedSearcher;
    }

    private JQLSearcher[] getSearchersAsArray(){
        return getSearchers().toArray(new JQLSearcher[0]);
    }

    public boolean hasSelectedSearcher(){
        return mySelectedSearcher > -1;
    }

    public void setSearchers(List<JQLSearcher> searcherList, int selected) {
        this.projectSearchers = searcherList;
        this.mySelectedSearcher = selected;
        notifyObservers(searcherList);
    }

    public void notifyObservers(List<JQLSearcher> searchers){
        getJqlSearcherObserver().update(searchers);
    }

    private JQLSearcherObserver getJqlSearcherObserver(){
        return myProject.getComponent(JQLSearcherObserver.class);
    }


    public static class Config{
        @Tag("selected")
        public int selected;

        @Tag("searchers")
        public Element searchers;
    }

}