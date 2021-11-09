package com.intellij.jira.ui.panels;

import com.intellij.jira.jql.JQLSearcherManager;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.jira.util.SimpleSelectableList;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.CollectionComboBoxModel;
import org.jetbrains.annotations.NotNull;

import java.awt.BorderLayout;
import java.util.ArrayList;

import static com.intellij.jira.jql.JQLSearcherManager.JQL_SEARCHERS_CHANGE;

public class JiraJQLSearcherPanel extends JiraPanel {

    private final Project myProject;
    private final JQLSearcherManager myManager;
    private final Runnable myRefresher;

    private ComboBox<JQLSearcher> myComboBox;
    private CollectionComboBoxModel<JQLSearcher> myComboBoxItems;

    public JiraJQLSearcherPanel(@NotNull Project project, @NotNull Runnable refresher) {
        super(new BorderLayout());
        myProject = project;
        myManager = JQLSearcherManager.getInstance();
        myRefresher = refresher;

        init();
        installListeners();
    }

    private void init() {
        myComboBoxItems = new CollectionComboBoxModel(new ArrayList<>());
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
            int selectedSearcherIndex =  this.myComboBox.getSelectedIndex();
            if(selectedSearcherIndex >= 0){
                myManager.setSelectedSearcher(myProject, selectedSearcherIndex);
                ApplicationManager.getApplication().invokeLater(myRefresher);
            }
        });

        myProject.getMessageBus().connect().subscribe(JQL_SEARCHERS_CHANGE, () -> {
            myComboBoxItems.removeAll();

            SimpleSelectableList<JQLSearcher> searchers = myManager.getSimpleSelectableList(myProject);

            if(!searchers.isEmpty()){
                myComboBoxItems.add(searchers.getItems());
                int selectedSearcherIndex = searchers.getSelectedItemIndex();
                if(myComboBox.getSelectedIndex() != selectedSearcherIndex){
                    myComboBox.setSelectedIndex(selectedSearcherIndex);
                }
            } else {
                myComboBoxItems.setSelectedItem(null);
            }

            myComboBoxItems.update();
        });

    }

}
