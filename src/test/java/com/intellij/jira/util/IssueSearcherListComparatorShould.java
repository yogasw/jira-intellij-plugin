package com.intellij.jira.util;

import com.intellij.jira.rest.model.jql.JQLSearcher;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class IssueSearcherListComparatorShould {

    @Test
    public void test() {
        List<JQLSearcher> searchersOld = new ArrayList<>();
        List<JQLSearcher> searchersNew = new ArrayList<>();

        JQLSearcher searcherA = new JQLSearcher("A", "JQL_A", false);
        JQLSearcher searcherB = new JQLSearcher("B", "JQL_B", false);
        JQLSearcher searcherC = new JQLSearcher("C", "JQL_C", false);
        JQLSearcher searcherD = new JQLSearcher("D", "JQL_D", false);

        JQLSearcher cloneA = searcherA.clone();
        cloneA.setAlias("A'");

        searchersOld.add(searcherA);
        searchersOld.add(searcherB);
        searchersOld.add(searcherC);

        searchersNew.add(cloneA);
        searchersNew.add(searcherB);
        searchersNew.add(searcherD);

        IssueSearcherListComparator comparator = new IssueSearcherListComparator();

        ListComparator.Result<JQLSearcher> result = comparator.compare(searchersOld, searchersNew);

        Assert.assertEquals(1, result.getRemoved().size());
        Assert.assertEquals(1, result.getEdited().size());
        Assert.assertEquals(1, result.getNotEdited().size());
        Assert.assertEquals(1, result.getAdded().size());

    }


}