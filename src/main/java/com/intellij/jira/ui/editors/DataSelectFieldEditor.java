package com.intellij.jira.ui.editors;

import java.util.List;


public abstract class DataSelectFieldEditor<T> extends SelectFieldEditor<T> {

    protected List<T> myItems;
    protected List<T> selectedItems;

    public DataSelectFieldEditor(String issueKey, String fieldName, Object fieldValue, boolean required, boolean isMultiSelect, List<T> items) {
        super(issueKey, fieldName, fieldValue, required, isMultiSelect);
        myItems = items;
        initSelectedItems();
    }

    protected abstract void initSelectedItems();

}
