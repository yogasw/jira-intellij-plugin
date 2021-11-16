package com.intellij.jira.util;

import com.intellij.jira.rest.model.jql.JQLSearcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IssueSearcherListComparator implements ListComparator<JQLSearcher> {

    /**
     * <p>Compares the two lists and fills arrays depending on whether it is a delete or an update or an insert</p>
     * <br>
     *
     * @param old the previous list
     * @param current the new list
     */
    @Override
    public ListComparator.Result<JQLSearcher> compare(List<JQLSearcher> old, List<JQLSearcher> current) {
        JQLSearcherResult result = new JQLSearcherResult();

        for (JQLSearcher searcher : old) {
            Optional<JQLSearcher> matching = current.stream().filter(s -> s.getId().equals(searcher.getId())).findFirst();
            if (matching.isPresent()) {
                if (!searcher.equals(matching.get())) {
                    result.edited(matching.get());
                    current.remove(matching.get());
                } else {
                    result.notEdited(matching.get());
                }
            } else {
                result.removed(searcher);
            }
        }

        current.removeAll(old);
        current.forEach(result::added);

        return result;
    }

    public static class JQLSearcherResult implements Result<JQLSearcher> {

        private final List<JQLSearcher> myRemoved = new ArrayList<>();
        private final List<JQLSearcher> myEdited = new ArrayList<>();
        private final List<JQLSearcher> myNotEdited = new ArrayList<>();
        private final List<JQLSearcher> myAdded = new ArrayList<>();

        public Result<JQLSearcher> removed(JQLSearcher e) {
            myRemoved.add(e);
            return this;
        }

        public Result<JQLSearcher> edited(JQLSearcher e) {
            myEdited.add(e);
            return this;
        }

        public Result<JQLSearcher> notEdited(JQLSearcher e) {
            myNotEdited.add(e);
            return this;
        }

        public Result<JQLSearcher> added(JQLSearcher e) {
            myAdded.add(e);
            return this;
        }

        public List<JQLSearcher> getRemoved() {
            return myRemoved;
        }

        public List<JQLSearcher> getEdited() {
            return myEdited;
        }

        public List<JQLSearcher> getNotEdited() {
            return myNotEdited;
        }

        public List<JQLSearcher> getAdded() {
            return myAdded;
        }
    }

}
