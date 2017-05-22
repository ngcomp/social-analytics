package com.ngcomp.analytics.engine.thread.fb.comparator;

import com.ngcomp.analytics.engine.domain.FBPost;

import java.util.Collections;
import java.util.Comparator;

/**
 * User: Ram Parashar
 * Date: 9/5/13
 * Time: 9:27 AM
 */
public class KeyWordComparator implements Comparator<FBPost> {

    private String keyword;

    public KeyWordComparator(String keyword){
        this.keyword = keyword;
    }

    @Override
    public int compare(FBPost post1, FBPost post2) {

        int t1Occurrences = 0;
        int t2Occurrences = 0;

        if(post1.getKeyWords() == null){
            t1Occurrences = 0;
        }else{
            t1Occurrences = Collections.frequency(post1.getKeyWords(), this.keyword);
        }
        if(post2.getKeyWords() == null){
            t2Occurrences = 0;
        }else{
            t2Occurrences = Collections.frequency(post2.getKeyWords(), this.keyword);
        }

        if(t1Occurrences == t2Occurrences){
            return Long.compare(post1.getRowKey(), post2.getRowKey());
        }else{
            return Integer.compare(t1Occurrences, t2Occurrences);
        }
    }

}
