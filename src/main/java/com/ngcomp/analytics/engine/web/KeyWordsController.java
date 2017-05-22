package com.ngcomp.analytics.engine.web;

import com.ngcomp.analytics.engine.service.KeyWordsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;

/**
 * User: Ram Parashar
 * Date: 8/9/13
 * Time: 7:25 PM
 */
@Controller
public class KeyWordsController extends GenericExceptionHandler{


    @Autowired
    @Qualifier("keyWordsManager")
    KeyWordsManager keyWordsManager;

    /**
     * This will remove keyword globally.
     * This word will be added in global ignore list.
     */
//    @RequestMapping(value = "/keyword/{keyWord}", method = RequestMethod.DELETE)
//    public void removeGlobalKeyWord(@PathVariable String keyWord){
//        Jedis jedis = new Jedis("localhost");
//        jedis.hset("IGNORE_KEYWORDS", keyWord, keyWord);
//        jedis.disconnect();
//    }



    /**
     * This is the same as related keywords but without any keywords as input.
     * The input and output structure is identical to Related Images, explained in the previous section.
     * @param sourceId
     //* @param match keyword == null ? "POPULAR KEYWORDS" : "RELATED KEYWORDS"
     //* @param sort
     //* @param fromTime
     //* @param toTime
     * @param results
     * @param startKey
     */

    //http://localhost:8083/api/keyword/Carlsberg?id=1&match=POPULAR

    @RequestMapping(value = "/keyword", method = RequestMethod.GET)
    @ResponseBody
    public String popularKeywords(
                                @RequestParam(required = false) String sourceId,
                                @RequestParam(required = false) String marketId,
                                @RequestParam(required = false) String brandId,
                                @RequestParam(required = false) String keyWord,
//                                @RequestParam(required = false) String match,
//                                @RequestParam(defaultValue = "score", required = false) String sort,
//                                @RequestParam(required = false) Long fromTime,
//                                @RequestParam(required = false) Long toTime,
                                @RequestParam(required = false, defaultValue = "20") Long results,
                                @RequestParam(required = false) String startKey) throws DatatypeConfigurationException, IOException {
        //return keyWordsManager.getPopularKeywords(sourceId, marketId, brandId, keyWord, match, fromTime, toTime, results, startKey).toString();
        return keyWordsManager.getPopularKeywords(sourceId, marketId, brandId, keyWord, results, startKey).toString();
    }

//    We should be able to get related images based on a keyword or keyword(s).
//
//    The purpose of the call is to get popular keywords within the same domain space. Sometimes memes or trends show up in the keywords around a topic and this is what we are trying to accommodate.
//
//    The input and output structure is identical to Related Images, explained in an earlier section.


    /**
     * Users can remove a keyword from a story.
     * This is used mostly when a wrong keyword has been added and other managerial tasks.
     * @param storyId
     */
    @RequestMapping(value = "/story/keyword", method = RequestMethod.DELETE)
    @ResponseBody
    public void removeStoryKeyWord(@RequestParam(required = true) String storyId,
                                   @RequestParam(required = true) String sourceId,
                                   @RequestParam(required = true) String keyWord) throws IOException {  //Id is required to go to a particular table.
        keyWordsManager.removeStoryKeyWord(sourceId, storyId, keyWord);
    }

    /**
     * Users can add a keyword to a story, using the URL as unique identifier.
     * The purpose is to enrich stories with contextual keywords.
     * We use this mostly for content coming from the brand’s social channels,
     * so we know what their prior stories were about and get the ability to search through it more effectively.
     * @param storyId
     * @param keyWord
     */
    @RequestMapping(value = "/story/keyword", method = RequestMethod.PUT)
    @ResponseBody
    public void addStoryKeyWord(@RequestParam(required = true) String storyId,
                                @RequestParam(required = true) String sourceId,
                                @RequestParam(required = true) String keyWord) throws IOException {
        keyWordsManager.addStoryKeyWord(sourceId, storyId, keyWord);
    }



}
