package com.intellij.jira.server;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
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
import java.util.Objects;

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
        List<JiraServer> servers = config.servers;
        if (servers != null) {
            for (JiraServer server : servers) {
                CredentialAttributes credentialAttributes = new CredentialAttributes(server.getUrl());
                Credentials credentials = PasswordSafe.getInstance().get(credentialAttributes);
                if (Objects.nonNull(credentials)) {
                    server.setUsername(credentials.getUserName());
                    server.setPassword(credentials.getPasswordAsString());
                }
            }

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
        List<JiraServer> oldJiraServers = new ArrayList<>(this.myJiraServers.getItems());
        List<JiraServer> newJiraServers = servers.getItems();

        oldJiraServers.removeAll(newJiraServers);
        // Eliminamos las credenciales almacenadas que ya no sirven
        for (JiraServer server : oldJiraServers) {
            CredentialAttributes credentialAttributes = new CredentialAttributes(server.getUrl());
            PasswordSafe.getInstance().set(credentialAttributes, null);
        }

        // Almacenamos las nuevas credenciales
        storeCredentials(newJiraServers);

        this.myJiraServers = servers;
        onServersChanged();
    }

    private void storeCredentials(List<JiraServer> jiraServers) {
        for (JiraServer server : jiraServers) {
            CredentialAttributes credentialAttributes = new CredentialAttributes(server.getUrl());
            Credentials credentials = new Credentials(server.getUsername(), server.getPassword());

            PasswordSafe.getInstance().set(credentialAttributes, credentials);
        }
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
        repository.setUsername(jiraServer.getUsername());
        repository.setPassword(jiraServer.getPassword());

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
