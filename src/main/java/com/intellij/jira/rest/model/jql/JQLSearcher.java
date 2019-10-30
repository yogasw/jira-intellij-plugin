package com.intellij.jira.rest.model.jql;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.xmlb.annotations.Attribute;
import com.intellij.util.xmlb.annotations.Tag;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static com.intellij.openapi.util.text.StringUtil.trim;

@Tag("JQLSearcher")
public class JQLSearcher {

    private String alias;
    private String jql;
    @Attribute("shared")
    private boolean shared;

    public JQLSearcher() {
        this.alias = "";
        this.jql = "";
    }

    public JQLSearcher(@Nullable String alias, String jql, boolean shared) {
        setAlias(alias);
        setJql(jql);
        setShared(shared);
    }

    public JQLSearcher(JQLSearcher other){
        this(other.getAlias(), other.getJql(), other.isShared());
    }


    @Attribute("alias")
    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = StringUtil.isEmpty(alias) ? "Undefined" : trim(alias);
    }

    @Attribute("jql")
    public String getJql() {
        return jql;
    }

    public void setJql(String jql) {
        this.jql = trim(jql);
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    @Override
    public String toString() {
        return alias;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JQLSearcher that = (JQLSearcher) o;
        return Objects.equals(alias, that.alias);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alias);
    }

    @Override
    public JQLSearcher clone(){
        return new JQLSearcher(this);
    }

}
