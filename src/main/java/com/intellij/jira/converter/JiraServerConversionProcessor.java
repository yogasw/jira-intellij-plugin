package com.intellij.jira.converter;

import com.intellij.conversion.CannotConvertException;
import com.intellij.conversion.ConversionProcessor;
import com.intellij.conversion.WorkspaceSettings;
import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.jira.server.auth.AuthType;
import com.intellij.openapi.util.PasswordUtil;
import org.jdom.Attribute;
import org.jdom.Element;

import java.util.Objects;

public class JiraServerConversionProcessor extends ConversionProcessor<WorkspaceSettings> {

    @Override
    public boolean isConversionNeeded(WorkspaceSettings workspaceSettings) {
        Element jiraServerManager = workspaceSettings.getComponentElement("JiraServerManager");
        if (Objects.nonNull(jiraServerManager)) {
            Element servers = jiraServerManager.getChild("servers");
            for (Element server : servers.getChildren()) {
                Attribute passwordAttribute = server.getAttribute("password");
                Attribute apiTokenAttribute = server.getAttribute("apiToken");
                if (Objects.nonNull(passwordAttribute) || Objects.nonNull(apiTokenAttribute)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void process(WorkspaceSettings workspaceSettings) throws CannotConvertException {
        Element jiraServerManager = workspaceSettings.getComponentElement("JiraServerManager");
        if (Objects.nonNull(jiraServerManager)) {
            Element servers = jiraServerManager.getChild("servers");
            for (Element server : servers.getChildren()) {
                storeCredentials(server);
                removeDeprecatedAttributes(server);
            }
        }
    }

    private void storeCredentials(Element element) {
        String username, password;
        String url = element.getAttributeValue("url");
        String type = element.getAttributeValue("type");
        AuthType authType = Objects.isNull(type) ? AuthType.USER_PASS : AuthType.valueOf(type);
        if (AuthType.USER_PASS == authType) {
            username = element.getAttributeValue("username");
            password = element.getAttributeValue("password");
        } else {
            username = element.getAttributeValue("useremail");
            password = element.getAttributeValue("apiToken");
        }

        CredentialAttributes credentialAttributes = new CredentialAttributes(url);
        Credentials credentials = new Credentials(username, PasswordUtil.decodePassword(password));

        PasswordSafe.getInstance().set(credentialAttributes, credentials);
    }

    private void removeDeprecatedAttributes(Element element) {
        element.removeAttribute("username");
        element.removeAttribute("useremail");
        element.removeAttribute("password");
        element.removeAttribute("apiToken");
    }

}
