package com.intellij.jira.util;

import java.util.List;

public interface ListComparator<E> {

    /**
     * <p>Compares the two lists and fills arrays depending on whether it is a delete or an update or an insert</p>
     * <br>
     *
     * @param old the previous list
     * @param current the new list
     */
    Result<E> compare(List<E> old, List<E> current);

    interface Result<E> {

        Result<E> removed(E e);

        Result<E> edited(E e);

        Result<E> notEdited(E e);

        Result<E> added(E e);

        List<E> getRemoved();

        List<E> getEdited();

        List<E> getNotEdited();

        List<E> getAdded();
    }


}
