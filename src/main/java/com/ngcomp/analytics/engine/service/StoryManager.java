package com.ngcomp.analytics.engine.service;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import net.sf.json.JSONObject;
import org.apache.xmlrpc.XmlRpcException;

import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * User: Ram Parashar
 * Date: 9/4/13
 * Time: 11:39 PM
 */
public interface StoryManager {

    void hideStory(String storyId, String id) throws IOException;

    void unHideStory(String storyId, String id) throws IOException;

    void voteUpStory(String storyId, String id, String weight) throws IOException;

    void voteDownStory(String storyId, String id, String weight) throws IOException;


    JSONObject relatedStories(String sourceId, String keyWords, String match, String sort, Boolean owned, Long fromTime, Long toTime, Integer results, String page) throws DatatypeConfigurationException, IOException, XmlRpcException, BoilerpipeProcessingException, InvocationTargetException, IllegalAccessException;

    JSONObject allStories (String brandId, String marketId, String keywords, String match, String sort, Boolean owned, String active, Long fromTime, Long toTime, Integer results, String startKey) throws IOException;
}
