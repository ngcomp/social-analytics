package com.ngcomp.analytics.engine.service;

import net.sf.json.JSONObject;

import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;

/**
 * User: Ram Parashar
 * Date: 9/5/13
 * Time: 8:58 AM
 */
public interface KeyWordsManager {

    JSONObject getPopularKeywords(String sourceId, String marketId, String brandId, String keyWord, Long results, String startKey) throws DatatypeConfigurationException, IOException;

    void removeStoryKeyWord(String id, String storyId, String keyWord) throws IOException;

    void addStoryKeyWord(String sourceId, String storyId, String keyWord) throws IOException;
}
