package com.ngcomp.analytics.engine.service;

import net.sf.json.JSONObject;

import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;

/**
 * User: Ram Parashar
 * Date: 9/5/13
 * Time: 8:15 AM
 */
public interface ImagesManager {
    JSONObject relatedImages(String sourceId, String marketId, String brandId, String keyword, Integer results, String startKey) throws DatatypeConfigurationException, IOException;
}
