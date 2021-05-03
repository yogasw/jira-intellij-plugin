package com.intellij.jira.ui.panels;

import com.intellij.jira.components.JQLSearcherManager;
import com.intellij.jira.components.JQLSearcherObserver;
import com.intellij.jira.events.JQLSearcherEventListener;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.jira.tasks.RefreshIssuesTask;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.CollectionComboBoxModel;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

public class JiraJQLSearcherPanel extends JiraPanel implements JQLSearcherEventListener {

    private final Project myProject;
    private final JQLSearcherManager myManager;

    private ComboBox<JQLSearcher> myComboBox;
    private CollectionComboBoxModel<JQLSearcher> myComboBoxItems;

    public JiraJQLSearcherPanel(@NotNull Project project) {
        super(new BorderLayout());
        this.myProject = project;
        this.myManager = JQLSearcherManager.getInstance();

        init();
        installListeners();
    }

    private void init() {
        setBorder(JBUI.Borders.empty(2, 4));

        myComboBoxItems = new CollectionComboBoxModel(new ArrayList());
        for(JQLSearcher searcher : myManager.getSearchers(myProject)){
            JQLSearcher clone = searcher.clone();
            myComboBoxItems.add(clone);
        }

        myComboBox = new ComboBox(myComboBoxItems, 300);
        if(myManager.hasSelectedSearcher(myProject)){
            myComboBox.setSelectedIndex(myManager.getSelectedSearcherIndex(myProject));
        }

        add(myComboBox, BorderLayout.WEST);
    }

    private void installListeners() {
        this.myComboBox.addActionListener(e -> {
            JQLSearcher selectedItem = (JQLSearcher) this.myComboBox.getSelectedItem();
            if(nonNull(selectedItem)){
                myManager.update(myProject, selectedItem.getAlias(), selectedItem, true);
                ApplicationManager.getApplication().invokeLater(() -> new RefreshIssuesTask(myProject).queue());
            }
        });

        getJQLSearcherObserver().addListener(this);

    }

    private JQLSearcherObserver getJQLSearcherObserver(){
        return JQLSearcherObserver.getInstance(myProject);
    }

    @Override
    public void update(List<JQLSearcher> searchers) {
        myComboBoxItems.removeAll();

        if(!searchers.isEmpty()){
            myComboBoxItems.add(searchers);
            int selectedSearcherIndex = myManager.getSelectedSearcherIndex(myProject);
            if(myComboBox.getSelectedIndex() != selectedSearcherIndex){
                myComboBox.setSelectedIndex(selectedSearcherIndex);
            }
        } else {
            myComboBoxItems.setSelectedItem(null);
        }

        myComboBoxItems.update();
    }

    @Override
    public void update(JQLSearcher jqlSearcher) {
        // do nothing
    }
}
