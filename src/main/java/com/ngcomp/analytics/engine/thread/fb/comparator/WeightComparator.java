package com.ngcomp.analytics.engine.thread.fb.comparator;

import com.ngcomp.analytics.engine.domain.FBPost;

import java.util.Comparator;

/**
 * User: Ram Parashar
 * Date: 9/5/13
 * Time: 9:27 AM
 */
public class WeightComparator implements Comparator<FBPost> {

    @Override
    public int compare(FBPost post1, FBPost post2) {

        Double s1 = 0.0;
        Double s2 = 0.0;

        int compareField1 = post1.getRelevanceScore().compareTo(post2.getRelevanceScore());

        if (compareField1 != 0) {
            return -compareField1;
        }

        int compareField2 = post1.getRelevanceScore().compareTo(post2.getRelevanceScore());

        if (compareField2 != 0) {
            return compareField1;
        }


        return post1.getRowKey().compareTo(post2.getRowKey());
    }

}
