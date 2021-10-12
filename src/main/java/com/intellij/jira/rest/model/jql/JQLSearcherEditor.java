package com.intellij.jira.rest.model.jql;

import com.intellij.jira.ui.editors.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.tasks.jira.jql.JqlLanguage;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.LanguageTextField;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import static com.intellij.openapi.util.text.StringUtil.isEmpty;
import static com.intellij.openapi.util.text.StringUtil.trim;

public class JQLSearcherEditor implements Editor {

    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 24;

    private final Project myProject;
    private final JQLSearcher mySearcher;
    private boolean mySelectedSearcher;
    private boolean mySharedSearcher;

    private JBLabel myAliasLabel;
    private JBTextField myAliasField;

    private JBLabel mySearchLabel;
    private EditorTextField mySearchQueryField;

    private JCheckBox myDefaultSearcherCheckBox;
    private JCheckBox mySharedSearcherCheckBox;

    public JQLSearcherEditor(@NotNull Project project, @NotNull JQLSearcher searcher, boolean selected) {
        this.myProject = project;
        this.mySearcher = searcher;
        this.mySelectedSearcher = selected;
        this.mySharedSearcher = searcher.isShared();
    }

    public void apply(){
        this.mySearcher.setAlias(trim(myAliasField.getText()));
        this.mySearcher.setJql(trim(mySearchQueryField.getText()));
    }

    @Override
    public JComponent createPanel() {
        this.myAliasLabel = new JBLabel("Alias:", 4);
        this.myAliasField = new JBTextField(mySearcher.getAlias());
        this.myAliasField.setPreferredSize(JBUI.size(DEFAULT_WIDTH, DEFAULT_HEIGHT));

        this.mySearchLabel = new JBLabel("Search:", 4);
        this.mySearchQueryField = new LanguageTextField(JqlLanguage.INSTANCE, this.myProject, mySearcher.getJql());
        this.mySearchQueryField.setPreferredSize(JBUI.size(DEFAULT_WIDTH, 30));

        this.myDefaultSearcherCheckBox = new JCheckBox("Set Default");
        this.myDefaultSearcherCheckBox.setBorder(JBUI.Borders.emptyRight(4));
        this.myDefaultSearcherCheckBox.setSelected(mySelectedSearcher);

        this.mySharedSearcherCheckBox = new JCheckBox("Share");
        this.mySharedSearcherCheckBox.setBorder(JBUI.Borders.emptyRight(4));
        this.mySharedSearcherCheckBox.setSelected(mySharedSearcher);


        return FormBuilder.createFormBuilder()
                .addLabeledComponent(this.myAliasLabel, this.myAliasField)
                .addLabeledComponent(this.mySearchLabel, this.mySearchQueryField)
                .addComponent(myDefaultSearcherCheckBox)
                .addComponent(mySharedSearcherCheckBox)
                .getPanel();
    }

    @Nullable
    public ValidationInfo validate(){
        if(isEmpty(trim(myAliasField.getText()))){
            return new ValidationInfo("Alias field is required");
        }

        if(isEmpty(trim(mySearchQueryField.getText()))){
            return new ValidationInfo("JQL field is required");
        }

        return null;
    }

    public JBTextField getAliasField() {
        return myAliasField;
    }

    public boolean isSelectedSearcher(){
        return myDefaultSearcherCheckBox.isSelected();
    }

    public boolean isSharedSearcher(){
        return mySharedSearcherCheckBox.isSelected();
    }
}