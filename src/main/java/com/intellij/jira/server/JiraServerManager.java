package com.intellij.jira.server;

import com.intellij.jira.util.SimpleSelectableList;
import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.tasks.jira.JiraRepository;
import com.intellij.tasks.jira.JiraRepositoryType;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Tag;
import com.intellij.util.xmlb.annotations.XCollection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@State(name = "JiraServerManager", storages = @Storage(StoragePathMacros.WORKSPACE_FILE))
public class JiraServerManager implements PersistentStateComponent<JiraServerManager.Config> {

    private List<Runnable> myListeners = new ArrayList<>();
    private SimpleSelectableList<JiraServer> myJiraServers = new SimpleSelectableList<>();
    private Config myConfig = new Config();

    public static JiraServerManager getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, JiraServerManager.class);
    }

    @Nullable
    @Override
    public Config getState() {
        myConfig.selected = myJiraServers.getSelectedItemIndex();
        myConfig.servers = myJiraServers.getItems();
        return myConfig;
    }

    @Override
    public void loadState(@NotNull Config config) {
        XmlSerializerUtil.copyBean(config, myConfig);

        myJiraServers.clear();
        List<JiraServer> servers =  config.servers;
        if (servers != null) {
            myJiraServers.addAll(servers);
        }

        myJiraServers.selectItem(config.selected);
    }

    public void addConfigurationServerChangedListener(Runnable runnable){
        myListeners.add(runnable);
    }

    public List<JiraServer> getJiraServers() {
        return myJiraServers.getItems();
    }

    public int getSelectedJiraServerIndex(){
        return myJiraServers.getSelectedItemIndex();
    }

    public boolean hasJiraServerConfigured(){
        return myJiraServers.hasSelectedItem();
    }

    public JiraServer getCurrentJiraServer(){
        return myJiraServers.hasSelectedItem() ? myJiraServers.getItems().get(getSelectedJiraServerIndex()) : null;
    }

    public void setJiraServers(SimpleSelectableList<JiraServer> servers) {
        this.myJiraServers = servers;
        onServersChanged();
    }

    @Nullable
    public JiraRestApi getJiraRestApi(){
        return convertFrom(getCurrentJiraServer());
    }

    @NotNull
    public JiraRestApi getJiraRestApiFrom(@NotNull JiraServer jiraServer){
        return convertFrom(jiraServer);
    }

    @Nullable
    private JiraRestApi convertFrom(@Nullable JiraServer jiraServer){
        if(isNull(jiraServer)){
            return null;
        }

        JiraRepository repository = new JiraRepositoryType().createRepository();
        repository.setUrl(jiraServer.getUrl());
        repository.setUsername(jiraServer.hasUserAndPassAuth() ? jiraServer.getUsername() : jiraServer.getUseremail());
        repository.setPassword(jiraServer.hasUserAndPassAuth() ? jiraServer.getPassword() : jiraServer.getApiToken());

        return new JiraRestApi(repository);
    }

    private void onServersChanged(){
        myListeners.forEach(Runnable::run);
    }


    public static class Config{
        @Tag("selected")
        public int selected;


        @XCollection(propertyElementName = "servers")
        public List<JiraServer> servers;

    }

}
