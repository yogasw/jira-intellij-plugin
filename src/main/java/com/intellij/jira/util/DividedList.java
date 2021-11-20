package com.intellij.jira.util;

import com.intellij.openapi.util.Condition;

import java.util.List;

public interface DividedList<E> {

    List<E> getFirstList();

    List<E> getSecondList();

    List<E> getAll();

    int getSelectedIndex();

    E getSelected();

    void setSelected(E e);

    void setSelected(int index);

    boolean isSelected(E e);

    void add(E e, Condition<E> condition);

    void moveToFirst(E e);

    void moveToSecond(E e);

    boolean remove(E e);

    void update(E e);

}
