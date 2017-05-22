package com.ngcomp.analytics.engine.web;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import org.apache.xmlrpc.XmlRpcException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * User: Ram Parashar
 * Date: 8/9/13
 * Time: 7:22 PM
 */

/*
{
     filter:{
          market:marketID,
          brand:brandID,
          keywords:[keyword1,keyword2]
     },
     sorting:sortingMethod,
     order:orderingMethod,
     page:pageNumber
     results:numberOfResults
}

Proposal for returned output:
{
     stories: [
          {
               id: the unique identifier of the story,
               url: the url to the original story or image
               ts: time of publishing as unix timestamp
               story: the text of the story
               media: the image or video of the story
               media_type: image, video or none
               attribution: the string to add as attribution to the original article
               score: the trend score
               owned: a flag that tells if the brand owns this content or it was found on the web
               topic: The topic from the sources table. This is the main keyword associated with the story
               keywords:[list of keywords for the story]
               stats:{list of responses ie. likes:x, comments:y, shares:z}
               market: the local market specified in the sources table.

          }
     ]
}
 */


@Controller
public class StoryController extends GenericExceptionHandler {

    @Autowired
    @Qualifier("storyManager")
    com.ngcomp.analytics.engine.service.StoryManager storyManager;

    /**
     * The user should be able to add a story to the collection manually.
     * The purpose of adding a story is to enable discovering a great story on the web and adding it to the system, like a bookmarking tool.
     * The system will receive a URL and run the usual fetching, extraction and scoring from there.
     * This way of adding should have a built in ”vote up”, as the user has already screened this as a good story.
     */
//    @RequestMapping(value = "/story", method = RequestMethod.POST)
//    @ResponseBody
//    public void addStory(@RequestParam String marketId,
//                         @RequestParam String brandId,
//                         @RequestParam String url){
//    }


    /**
     * Users can hide a story, meaning they don’t want to see it again.
     * This should affect the score of the story  and stories like this negatively.
     */
    @RequestMapping(value = "/story", method = RequestMethod.DELETE)
    @ResponseBody
    public void hideStory(@RequestParam(required = true) String sourceId,
                          @RequestParam(required = true) String storyId) throws IOException {
        storyManager.hideStory(sourceId, storyId);
    }

    /**
     * This is used to affect the trend score.
     * If we go through 1000 stories and either hide them or vote them up,
     * the Bayesian parts of the trend score should be affected each time and hopefully weigh the results better.
     * @param storyId
     */
    @RequestMapping(value = "/story/{storyId}/voteup", method = RequestMethod.PUT)
    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void voteUpStory(@PathVariable String storyId, @RequestParam String sourceId,
                            @RequestParam String weight) throws IOException {
        storyManager.voteUpStory(sourceId, storyId, weight);
    }


    /**
     * This is used to affect the trend score.
     * If we go through 1000 stories and either hide them or vote them up,
     * the Bayesian parts of the trend score should be affected each time and hopefully weigh the results better.
     * @param storyId
     */
    @RequestMapping(value = "/story/{storyId}/votedown", method = RequestMethod.PUT)
    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void voteDownStory(@PathVariable String storyId, @RequestParam String sourceId,
                              @RequestParam String weight) throws IOException {
        storyManager.voteDownStory(sourceId, storyId, weight);
    }


    /**
     * We should be able to get related images based on a keyword or keyword(s).
     * The purpose of the call is to return stories that seem to revolve around the same story,
     * so a user can investigate the topic a bit more.
     * The input and output structure is identical to Related Images, explained in the previous section.
     */

    @RequestMapping(value = "/story", method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public String relatedStories(@RequestParam(required = false) String sourceId,                  //This id corresponds to one row in sources table.
                                 @RequestParam(required = false) String keyWords,
                                 @RequestParam(required = false, defaultValue = "any") String match,
                                 @RequestParam(defaultValue = "score", required = false) String sort,
                                 @RequestParam(required = false) Boolean owned,
                                 @RequestParam(required = false) String active,
                                 @RequestParam(required = false) Long fromTime,
                                 @RequestParam(required = false) Long toTime,
                                 @RequestParam(required = false, defaultValue = "20") Integer results,
                                 @RequestParam(required = false) String startKey) throws DatatypeConfigurationException, IOException, XmlRpcException, BoilerpipeProcessingException, InvocationTargetException, IllegalAccessException {
        return storyManager.relatedStories(sourceId, keyWords, match, sort, owned,  fromTime, toTime, results, startKey).toString();
    }



    @RequestMapping(value = "/stories", method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public String allStories(@RequestParam(required = false) String brandId,
                             @RequestParam(required = false) String marketId,
                             @RequestParam(required = false) String keywords,
                             @RequestParam(required = false, defaultValue = "any") String match,
                             @RequestParam(defaultValue = "score", required = false) String sort,
                             @RequestParam(required = false) Boolean owned,
                             @RequestParam(required = false) String active,
                             @RequestParam(required = false) Long fromTime,
                             @RequestParam(required = false) Long toTime,
                             @RequestParam(required = false, defaultValue = "20") Integer results,
                             @RequestParam(required = false) String startKey) throws DatatypeConfigurationException, IOException {
        return storyManager.allStories(brandId, marketId, keywords, match, sort, owned, active, fromTime, toTime, results, startKey).toString();
    }

}
