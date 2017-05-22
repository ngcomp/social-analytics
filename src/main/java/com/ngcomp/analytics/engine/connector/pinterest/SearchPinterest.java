package com.ngcomp.analytics.engine.connector.pinterest;

import com.google.gson.Gson;
import com.ngcomp.analytics.engine.connector.common.model.FBStat;
import com.ngcomp.analytics.engine.connector.common.model.Story;
import com.ngcomp.analytics.engine.connector.common.util.ConstructStory;
import com.ngcomp.analytics.engine.connector.common.util.ImageDownloader;
import com.ngcomp.analytics.engine.connector.common.util.JSONTransformer;
import com.ngcomp.analytics.engine.connector.common.util.RESTPosterClient;
import com.ngcomp.analytics.engine.connector.pinterest.auth.UriConstructor;
import com.ngcomp.analytics.engine.connector.pinterest.auth.UriFactory;
import com.ngcomp.analytics.engine.connector.pinterest.model.Board;
import com.ngcomp.analytics.engine.connector.pinterest.model.PinObject;
import com.ngcomp.analytics.engine.connector.pinterest.model.UserBoardResponse;
import com.ngcomp.analytics.engine.connector.pinterest.model.UserPinResponse;
import com.ngcomp.analytics.engine.connector.rss.FBStatFeedMessage;
import com.ngcomp.analytics.engine.util.PortalUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class SearchPinterest {

    private static final Logger logger = Logger.getLogger(SearchPinterest.class);

    /**
     * Method to return pins when a URL is given.
     *
     * @param constructStory        flag which allows/bars the system from constructing a story
     * @param downloadAndSaveImages flag which allows/bars the system from downloading and saving images
     * @param url                   for which the pin has to be fetched
     * @return an array yof PinObject.
     */
    public PinObject[] getPinsForURL(boolean constructStory, boolean downloadAndSaveImages, String url) {

        PinObject[] pins = null;
        try {
            Document document = Jsoup.connect(url).ignoreContentType(true).get();
            ListIterator<Element> links = document.select("a[href]").listIterator();

            PinStat pinStat = new PinStat();
            List<PinStat> pinStats = new ArrayList<PinStat>();

            while (links.hasNext()) {
                Element e = links.next();
//                System.out.println(e.toString());
                if (e.toString().contains("/pin/")) {

                    String link = e.attr("href");

                    Document doc = Jsoup.parse(e.toString(), "UTF-8");

                    Elements images = doc.select("img[src]");

                    String image = null;
                    String pinId = null;
                    String description = null;
                    int likes = 0;
                    int repins = 0;
                    int comments = 0;

                    String sTemp = link.contains("/pin/") ? link.substring(link.indexOf("/pin/") + 5) : "";
                           pinId = sTemp.equals("") ? "" : sTemp.substring(0, sTemp.indexOf("/"));

                    if (!(pinStat.getPinId() != null && pinStat.getPinId().equals(pinId))) {
                        pinStat = new PinStat();
                        pinStats.add(pinStat);
                        pinStat.setPinId(pinId);
                    }

                    for (Element element : images) {
                        image = element.attr("src");
                        pinStat.setImage(image);
                    }

                    Elements alts = doc.select("img[alt]");

                    for (Element element : alts) {
                        description = element.attr("alt");
                        pinStat.setDescription(description);
                    }

                    String attr = e.attr("class");
                    if (link.contains(pinId) && "socialItem".equals(attr)) {
                        String text = e.text();
                        repins = getNumberInString(text);
                        pinStat.setRepins(repins);
                    } else if (link.contains(pinId) && "socialItem likes".equals(attr)) {
                        String text = e.text();
                        likes = getNumberInString(text);
                        pinStat.setLikes(likes);

                    } else if (link.contains(pinId) && "socialItem comments".equals(attr)) {
                        String text = e.text();
                        comments = getNumberInString(text);
                        pinStat.setComments(comments);
                    }
                }else if(e.toString().contains("creditName")){
                    Document doc = Jsoup.parse(e.toString(), "UTF-8");
                    Element credit = doc.select("span.creditName").first();
                    pinStats.get(pinStats.size() -1).setOwner(credit.text());
                }
            }

            int k = 0;
            pins = new PinObject[pinStats.size()];

            for (PinStat stat : pinStats) {
                PinObject o = new PinObject();
                String pinUrl = UriFactory.GET_PIN;
                pinUrl = pinUrl.replace("{pinId}", stat.getPinId());
                o.setId(stat.getPinId());
                o.setPlatformId(stat.getPinId());
                o.setPinUrl(pinUrl);
                o.setHref(stat.getImage());
                o.setDesc(stat.getDescription());
                o.setOwner(stat.getOwner());
                o.setPinterestLikes("" + stat.getLikes());
                o.setCommentCount  ("" + stat.getComments());
                o.setPinterestRepin("" + stat.getRepins());

                if (downloadAndSaveImages) {
                    String newLinkForMedia = ImageDownloader.downloadAndSaveImage(o.getHref());
                    o.setHref(newLinkForMedia);
                }

                if (constructStory) {
                    Story story = ConstructStory.construct(constructStory, o.getPinUrl(), downloadAndSaveImages);
                    o.setStory(story);
                }

                // setting FB Stat
                FBStat fbStat = FBStatFeedMessage.getFBStats(o.getPinUrl());
                o.setFbStat(fbStat);

                pins[k++] = o;
            }

        } catch (IOException e) {
            logger.info(PortalUtils.exceptionAsJson(e));
            e.printStackTrace();
        }
        return pins;
    }

    /**
     * Method to return pins when a keyword is given is given.
     *
     * @param constructStory        flag which allows/bars the system from constructing a story
     * @param downloadAndSaveImages flag which allows/bars the system from downloading and saving images
     * @param query                 for which the pin has to be fetched
     * @return an array of PinObject.
     */
    public PinObject[] getPins(boolean constructStory, boolean downloadAndSaveImages, String query) {
        String q = UriFactory.GET_PINS + query;
        System.out.println(q);
        PinObject[] pinsForURL = getPinsForURL(constructStory, downloadAndSaveImages, q);
       /*for(PinObject o : pinsForURL)
		   o.setOwned(false);*/
        return pinsForURL;
    }


    private int getNumberInString(String text) {
        if (text != null && text.length() > 0) {
            String[] split = text.split(" ");
            for (String s : split) {
                try {
                    int parseInt = Integer.parseInt(s);
                    return parseInt;
                } catch (Exception e) {
                    continue;
                }
            }
        }
        return 0;
    }

    public PinObject[] getUserPins(String user, boolean downloadAndSaveImages, String destinationDirectory) {
        UriConstructor construct = new UriConstructor();
        Map<String, String> map = new HashMap<String, String>();
        map.put("username", user);
        String url = construct.url(UriFactory.PINS_FOR_USER, map);
        String response = RESTPosterClient.requestRESTGet(url);
        Gson gson = new Gson();
        UserPinResponse userPins = gson.fromJson(response, UserPinResponse.class);
        PinObject[] pins = new PinObject[userPins.getMeta().getCount()];
        int k = 0;
        for (PinObject pin : userPins.getBody()) {

            pin.setPinUrl(UriFactory.PINTEREST_URL + pin.getHref());

            pins[k++] = pin;
            if (downloadAndSaveImages) {
                String newImageURL = ImageDownloader.downloadAndSaveImage(pin.getSrc());
                pin.setSrc(newImageURL);
            }
        }
        return pins;
    }


    public Board[] getUserBoards(boolean constructStory, String user, boolean downloadAndSaveImages, String newPath) {

        UriConstructor construct = new UriConstructor();
        Map<String, String> map = new HashMap<String, String>();
        map.put("username", user);

        String url = construct.url(UriFactory.BOARDS_FOR_USER, map);
        String response = RESTPosterClient.requestRESTGet(url);
        Gson gson = new Gson();
        UserBoardResponse userBoards = gson.fromJson(response, UserBoardResponse.class);
        Board[] boards = new Board[userBoards.getMeta().getCount()];
        int k = 0;
        for (Board board : userBoards.getBody()) {

            board.setBoardURL(UriFactory.PINTEREST_URL + board.getHref());
            boards[k++] = board;
            board.setStory(ConstructStory.construct(constructStory, board.getBoardURL(), downloadAndSaveImages));
            board.setFbStat(FBStatFeedMessage.getFBStats(board.getBoardURL()));
            if (downloadAndSaveImages) {
                String newImageURL = ImageDownloader.downloadAndSaveImage(board.getSource());
                board.setSource(newImageURL);
            }

        }


        return boards;
    }

    public String[] getUserPinsAsJSON(String user, boolean downloadAndSaveImages, String destinationDirectory) {
        UriConstructor construct = new UriConstructor();
        Map<String, String> map = new HashMap<String, String>();
        map.put("username", user);
        String url = construct.url(UriFactory.PINS_FOR_USER, map);
        String response = RESTPosterClient.requestRESTGet(url);
        Gson gson = new Gson();
        UserPinResponse userPins = gson.fromJson(response, UserPinResponse.class);
        String[] pins = new String[userPins.getMeta().getCount()];
        int k = 0;
        for (PinObject pin : userPins.getBody()) {

            pin.setPinUrl(UriFactory.PINTEREST_URL + pin.getHref());
            pins[k++] = JSONTransformer.getJSONString(pin);

            if (downloadAndSaveImages) {
                String newImageURL = ImageDownloader.downloadAndSaveImage(pin.getSrc());
                pin.setSrc(newImageURL);
            }
        }
        return pins;
    }


    public String[] getUserBoardsAsJSON(String user, boolean downloadAndSaveImages, String destinationDirectory) {
        UriConstructor construct = new UriConstructor();
        Map<String, String> map = new HashMap<String, String>();
        map.put("username", user);
        String url = construct.url(UriFactory.BOARDS_FOR_USER, map);
        String response = RESTPosterClient.requestRESTGet(url);
        Gson gson = new Gson();
        UserBoardResponse userBoards = gson.fromJson(response, UserBoardResponse.class);
        String[] boards = new String[userBoards.getMeta().getCount()];
        int k = 0;
        for (Board board : userBoards.getBody()) {

            board.setBoardURL(UriFactory.PINTEREST_URL + board.getHref());
            boards[k++] = JSONTransformer.getJSONString(board);

            if (downloadAndSaveImages) {
                String newImageURL = ImageDownloader.downloadAndSaveImage(board.getSource());
                board.setSource(newImageURL);
            }
        }
        return boards;
    }

}


class PinStat {
    int likes;
    int comments;
    int repins;
    String owner;

    String getOwner() {
        return owner;
    }

    void setOwner(String owner) {
        this.owner = owner;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public int getRepins() {
        return repins;
    }

    public void setRepins(int repins) {
        this.repins = repins;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPinId() {
        return pinId;
    }

    public void setPinId(String pinId) {
        this.pinId = pinId;
    }

    String image;
    String description;
    String pinId;
}