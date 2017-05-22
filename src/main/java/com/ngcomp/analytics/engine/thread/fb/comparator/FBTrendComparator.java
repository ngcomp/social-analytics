package com.ngcomp.analytics.engine.thread.fb.comparator;

import com.ngcomp.analytics.engine.model.Trend;

import java.util.Collections;
import java.util.Comparator;

/**
 * User: Ram Parashar
 * Date: 9/5/13
 * Time: 9:27 AM
 */
public class FBTrendComparator implements Comparator<Trend> {

    private String keyWord;


    public FBTrendComparator(String keyWord){
        this.keyWord = keyWord;
    }

    @Override
    public int compare(Trend trend1, Trend trend2) {

        int t1Occurrences = 0;
        int t2Occurrences = 0;

        if(trend1.getOriginalTokens() == null){
            t1Occurrences = 0;
        }else{
            t1Occurrences = Collections.frequency(trend1.getOriginalTokens(), keyWord);
        }
        if(trend2.getOriginalTokens() == null){
            t2Occurrences = 0;
        }else{
            t2Occurrences = Collections.frequency(trend1.getOriginalTokens(), keyWord);
        }

        return Integer.compare(t1Occurrences, t2Occurrences);
    }

}
