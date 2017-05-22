package com.ngcomp.analytics.engine.web;

import com.ngcomp.analytics.engine.service.ImagesManager;
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
 * Time: 7:24 PM
 */

/*
    FILTERS:
    There are 3 filter options and a call can include one or more of these filters:
    Market: Means we return: show stories matching the marketID from the sources table
    Brand: Means we show: stories matching the brandID from the sources table
    Keywords: Means we will return: stories matching the keyword list.
    If there are no keywords sent, it means all stories are eligible.
*/
@Controller
public class ImagesController extends GenericExceptionHandler{



    @Autowired
    @Qualifier("imagesManager")
    ImagesManager imagesManager;


    /**
     * We should be able to get related images based on a keyword or keyword(s).
     * This call should only return the brand's owned media for now. We will add curated images from Flickr later,
     * so the system should be prepared to handle this.
     * The purpose of the call is to propose images that would go great with a story, so this is the success criteria.
     */
    @RequestMapping(value = "/image", method = RequestMethod.GET)
    @ResponseBody
    public String relatedImages(@RequestParam(required = false) String sourceId,
                                @RequestParam(required = false) String marketId,
                                @RequestParam(required = false) String brandId,
                                @RequestParam(required = false) String keyword,
//                                   @RequestParam(defaultValue = "score", required = false) String sort,
                                @RequestParam(required = false, defaultValue = "20") Integer results,
                                @RequestParam(required = false) String startKey
    ) throws DatatypeConfigurationException, IOException {
        return imagesManager.relatedImages(sourceId, marketId, brandId, keyword, results, startKey).toString();
    }

}
