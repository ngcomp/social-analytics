package com.ngcomp.analytics.engine.domain;

/**
 * User: rparashar
 * Date: 10/6/13
 * Time: 3:31 PM
 */
public class SInfo implements Comparable<SInfo> {

    private String key;
    private Double score;

    public SInfo(String key, Double score){
        this.key = key;
        this.score = score;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SInfo)) return false;

        SInfo sInfo = (SInfo) o;

        if (key != null ? !key.equals(sInfo.key) : sInfo.key != null) return false;
        if (score != null ? !score.equals(sInfo.score) : sInfo.score != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (score != null ? score.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(SInfo sInfo) {
        return Double.compare(sInfo.getScore(), this.score);
    }

    @Override
    public String toString() {
        return "SInfo{" +
                "key='" + key + '\'' +
                ", score=" + score +
                '}';
    }
}
