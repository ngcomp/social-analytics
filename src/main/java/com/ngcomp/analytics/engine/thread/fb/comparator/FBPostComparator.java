package com.ngcomp.analytics.engine.thread.fb.comparator;

import com.ngcomp.analytics.engine.domain.FBPost;

import java.util.Collections;
import java.util.Comparator;

/**
 * User: Ram Parashar
 * Date: 9/5/13
 * Time: 9:27 AM
 */
public class FBPostComparator implements Comparator<FBPost> {

    private String keyWord;


    public FBPostComparator(String keyWord){
        this.keyWord = keyWord;
    }

    @Override
    public int compare(FBPost post1, FBPost post2) {

        int t1Occurrences = 0;
        int t2Occurrences = 0;

        if(post1.getOriginalTokens() == null){
            t1Occurrences = 0;
        }else{
            t1Occurrences = Collections.frequency(post1.getOriginalTokens(), keyWord);
        }
        if(post2.getOriginalTokens() == null){
            t2Occurrences = 0;
        }else{
            t2Occurrences = Collections.frequency(post2.getOriginalTokens(), keyWord);
        }

        return Integer.compare(t1Occurrences, t2Occurrences);
    }

}
