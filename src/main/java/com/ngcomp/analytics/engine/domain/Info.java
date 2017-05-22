package com.ngcomp.analytics.engine.domain;

import com.google.common.base.Strings;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by rparashar on 9/23/13.
 */
public class Info implements Comparable<Info> {

    private String sourceId;
    private String marketId;
    private String brandId;
    private Double relevanceScore;
    private String storyId;
    private Set<String> originalTokens = new HashSet<String>();
    private Long createdAt;
    private String id;

    /**
     *
     * @param sourceId
     * @param marketId
     * @param brandId
     * @param relevanceScore
     * @param storyId
     * @param originalTokens
     */
    public Info(String sourceId, String marketId, String brandId, Double relevanceScore, String storyId, Set<String> originalTokens){
        this.sourceId = sourceId;
        this.marketId = marketId;
        this.brandId  = brandId;
        this.relevanceScore = relevanceScore;
        this.storyId        = storyId;
        this.originalTokens = originalTokens;
    }

    public Info(String sourceId, String marketId, String brandId, Double relevanceScore, String storyId, Set<String> originalTokens, String id){
        this.sourceId = sourceId;
        this.marketId = marketId;
        this.brandId  = brandId;
        this.relevanceScore = relevanceScore;
        this.storyId        = storyId;
        this.originalTokens = originalTokens;
        this.id = id;
    }

    public void setOriginalTokens(String tokes){
        if(!Strings.isNullOrEmpty(tokes)){
            Collections.addAll(this.originalTokens, tokes.split(","));
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Set<String> getOriginalTokens() {
        return originalTokens;
    }

    public void setOriginalTokes(Set<String> originalTokes) {
        this.originalTokens = originalTokes;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getMarketId() {
        return marketId;
    }

    public void setMarketId(String marketId) {
        this.marketId = marketId;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public Double getRelevanceScore() {
        return relevanceScore;
    }

    public void setRelevanceScore(Double relevanceScore) {
        this.relevanceScore = relevanceScore;
    }

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Info)) return false;

        Info info = (Info) o;

        if (brandId != null ? !brandId.equals(info.brandId) : info.brandId != null) return false;
        if (createdAt != null ? !createdAt.equals(info.createdAt) : info.createdAt != null) return false;
        if (id != null ? !id.equals(info.id) : info.id != null) return false;
        if (marketId != null ? !marketId.equals(info.marketId) : info.marketId != null) return false;
        if (originalTokens != null ? !originalTokens.equals(info.originalTokens) : info.originalTokens != null)
            return false;
        if (relevanceScore != null ? !relevanceScore.equals(info.relevanceScore) : info.relevanceScore != null)
            return false;
        if (sourceId != null ? !sourceId.equals(info.sourceId) : info.sourceId != null) return false;
        if (storyId != null ? !storyId.equals(info.storyId) : info.storyId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = sourceId != null ? sourceId.hashCode() : 0;
        result = 31 * result + (marketId != null ? marketId.hashCode() : 0);
        result = 31 * result + (brandId != null ? brandId.hashCode() : 0);
        result = 31 * result + (relevanceScore != null ? relevanceScore.hashCode() : 0);
        result = 31 * result + (storyId != null ? storyId.hashCode() : 0);
        result = 31 * result + (originalTokens != null ? originalTokens.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Info compareTrend) {
        if(compareTrend.getRelevanceScore() ==  this.relevanceScore){
            return Long.compare(compareTrend.getCreatedAt(), this.createdAt);
        }else{
            return Double.compare(compareTrend.getRelevanceScore(), this.relevanceScore);
        }
    }
}
