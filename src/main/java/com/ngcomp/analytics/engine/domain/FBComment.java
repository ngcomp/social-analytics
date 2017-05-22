package com.ngcomp.analytics.engine.domain;

import com.google.gson.Gson;
import com.ngcomp.analytics.engine.util.PortalUtils;
import com.restfb.types.Comment;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import org.apache.xmlrpc.XmlRpcException;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by rparashar on 9/19/13.
 */
public class FBComment extends Comment{

    private List<String> keywords      = new LinkedList<String>();
    private List<String> storyKeywords = new LinkedList<String>();
    private List<String> newKeywords   = new LinkedList<String>();

    //Story or Image..
    private String type;

    private String story;


    public Map<String, String[]> getHBaseRowMap(){

        Gson gson = new Gson();

        String[] quals = new String[16];
        String[] vals  = new String[16];

        quals[0] = "message"  ;  vals[0 ] = this.getMessage();
        quals[1] = "createdTime";vals[1 ] = String.valueOf(this.getCreatedTime().getTime());
        quals[2] = "likeCount";  vals[2 ] = String.valueOf(this.getLikeCount());
        quals[3] = "canRemove";  vals[3 ] = String.valueOf(this.getCanRemove());
        quals[4] = "userLikes";  vals[3 ] = String.valueOf(this.getUserLikes());

        Map<String, String[]> map = new HashMap<String, String[]>();
        map.put("quals", quals);
        map.put("vals" , vals);

        return map;
    }

    public List getKeywords() {
        return keywords;
    }


    public void setKeywords(String message) throws IOException, XmlRpcException, BoilerpipeProcessingException {
        this.keywords = PortalUtils.getKeywords(message);
        this.setType(message);
    }

    private void setType(String message){
        this.type = PortalUtils.getExtension(message);
        if(this.type!= null){
            if(this.type.equals("image")){
                //TODO Code for image download to S3
            }else{
                this.story = PortalUtils.getStory(message);
            }
        }
    }



    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<String> getStoryKeywords() {
        return storyKeywords;
    }

    public void setStoryKeywords(List<String> storyKeywords) {
        this.storyKeywords = storyKeywords;
    }

    public List<String> getNewKeywords() {
        return newKeywords;
    }

    public void setNewKeywords(List<String> newKeywords) {
        this.newKeywords = newKeywords;
    }

    public String getType() {
        return type;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }
}
