package com.ngcomp.analytics.engine.util;

import com.ngcomp.analytics.engine.domain.Info;

import java.util.Collections;
import java.util.Comparator;

/**
 * Created by rparashar on 9/23/13.
 */
public class InfoComparator  implements Comparator<Info> {

    private String keyWord;


    public InfoComparator(String keyWord){
        this.keyWord = keyWord;
    }

    @Override
    public int compare(Info trend1, Info trend2) {

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
