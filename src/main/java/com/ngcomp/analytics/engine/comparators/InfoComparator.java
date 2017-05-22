package com.ngcomp.analytics.engine.comparators;

import com.ngcomp.analytics.engine.domain.Info;

import java.util.Comparator;

/**
 * User: rparashar
 * Date: 9/25/13
 * Time: 9:42 PM
 */
public class InfoComparator  implements Comparator<Info> {

    @Override
    public int compare(Info info1, Info info2) {

        Double score1 = 0.0;
        Double score2 = 0.0;

        if(info1.getRelevanceScore() != null && !info1.getRelevanceScore().isNaN()){
            score1 = info1.getRelevanceScore();
        }

        if(info2.getRelevanceScore() != null && !info2.getRelevanceScore().isNaN()){
            score2 = info2.getRelevanceScore();
        }

        if(score1 != score2){
            return Double.compare(score2, score1);
        }else if(info1.getCreatedAt()!= info2.getCreatedAt()){
            return Long.compare(info2.getCreatedAt(), info1.getCreatedAt());
        }else{
            System.out.println("sdfsdf");
            return info2.getStoryId().compareTo(info1.getStoryId());
        }
    }
}
