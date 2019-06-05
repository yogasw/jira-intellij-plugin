package com.intellij.jira.components;

import com.intellij.configurationStore.XmlSerializer;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.*;
import com.intellij.util.xmlb.XmlSerializationException;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Tag;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

@State(name = "JQLSearcherApplicationManager", storages = @Storage(StoragePathMacros.CACHE_FILE))
public class JQLSearcherApplicationManager implements BaseComponent, PersistentStateComponent<JQLSearcherApplicationManager.Config> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JQLSearcherApplicationManager.class);
    private static final JQLSearcher DEFAULT_JQL = new JQLSearcher("Assigned to me", "assignee = currentUser()", true);

    private List<JQLSearcher> applicationSearchers = new ArrayList<>();
    private Config myConfig = new Config();

    public JQLSearcherApplicationManager() {
        this.applicationSearchers.add(DEFAULT_JQL);
    }

    @Nullable
    @Override
    public Config getState() {
        myConfig.searchers = XmlSerializer.serialize(getSearchersAsArray());
        return myConfig;
    }

    @Override
    public void loadState(@NotNull Config config) {
        XmlSerializerUtil.copyBean(config, myConfig);

        applicationSearchers.clear();
        Element element = config.searchers;
        List<JQLSearcher> searchers = loadSearchers(element);
        applicationSearchers.addAll(searchers);
    }


    private List<JQLSearcher> loadSearchers(Element element) {
        List<JQLSearcher> searchers = new ArrayList<>();
        if(nonNull(element)){
            for(Element o : element.getChildren()){
                try{
                    JQLSearcher searcher = XmlSerializer.deserialize(o, JQLSearcher.class);
                    searchers.add(searcher);
                }catch (XmlSerializationException e) {
                    LOGGER.error(e.getMessage());
                }
            }
        }

        return searchers;
    }

    public List<JQLSearcher> getSearchers() {
        return applicationSearchers;
    }

    public void setSearchers(List<JQLSearcher> applicationSearchers) {
        this.applicationSearchers = applicationSearchers;
    }

    private JQLSearcher[] getSearchersAsArray(){
        return getSearchers().toArray(new JQLSearcher[0]);
    }

    public static JQLSearcherApplicationManager getInstance() {
        return ApplicationManager.getApplication().getComponent(JQLSearcherApplicationManager.class);
    }


    public static class Config{
        @Tag("searchers")
        public Element searchers;
    }


}