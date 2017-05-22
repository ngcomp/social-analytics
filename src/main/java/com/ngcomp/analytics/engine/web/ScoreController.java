package com.ngcomp.analytics.engine.web;

import com.ngcomp.analytics.engine.service.ScoreManager;
import com.ngcomp.analytics.engine.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * User: Ram Parashar
 * Date: 9/3/13
 * Time: 10:46 PM
 */
@Controller
public class ScoreController extends GenericExceptionHandler{

    @Autowired
    @Qualifier("scoreManager")
    ScoreManager scoreManager;

    /**
     *
     * @param sourceId
     * @param zScore
     * @param amplifierChannel
     * @param decay
     * @param avgStoryDecay
     * @param w1
     * @param w2
     * @param w3
     */
    @RequestMapping(value = "/score/{sourceId}", method = RequestMethod.PUT)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public void updateScore(@PathVariable String sourceId,
                            @RequestParam(required = false) String zScore,
                            @RequestParam(required = false) String amplifierChannel,
                            @RequestParam(required = false) String decay,
                            @RequestParam(required = false) String avgStoryDecay,
                            @RequestParam(required = false) String w1,
                            @RequestParam(required = false) String w2,
                            @RequestParam(required = false) String w3){

        scoreManager.updateScores(sourceId, zScore, amplifierChannel, decay, avgStoryDecay, w1, w2, w3);
    }


    /**
     *
     * @param sourceId
     * @return
     */
    @RequestMapping(value = "/score/{sourceId}", method = RequestMethod.GET, produces = Constants.JSON)
    @ResponseBody
    public String getScores(@PathVariable String sourceId){
        return scoreManager.getScores(sourceId).toString();
    }


    /**
     *
     * @param sourceId
     * @return
     */
    @RequestMapping(value = "/score/{sourceId}", method = RequestMethod.POST, produces = Constants.JSON)
    @ResponseBody
    public String resetScores(@PathVariable String sourceId){
        return scoreManager.resetScores(sourceId).toString();
    }


}
