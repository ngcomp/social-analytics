package com.ngcomp.analytics.engine.connector.twitter;

import twitter4j.*;

/**
 * Reference: https://github.com/yusuke/twitter4j/tree/master/twitter4j-examples/src/main/java/twitter4j/examples
 * User: Ram Parashar
 * Date: 8/2/13
 * Time: 12:50 PM
 */
public class Trend {
    public static void main(String[] args) {
        try {
            Twitter twitter = new TwitterFactory().getInstance();
            ResponseList<Location> locations;
            locations = twitter.getAvailableTrends();
            System.out.println("Showing available trends");
            for (Location location : locations) {
                System.out.println(location.getName() + " (woeid:" + location.getWoeid() + ")");
            }
            System.out.println("done.");
            System.exit(0);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get trends: " + te.getMessage());
            System.exit(-1);
        }
    }
}
