package com.ngcomp.analytics.engine.connector.tumbler;

import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Blog;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.User;

import java.util.List;

/**
 * Documentation: https://github.com/tumblr/jumblr
 * Date: 8/2/13
 * Time: 11:37 AM
 */
public class Tumbler {

    public static void main(String...strings){

        JumblrClient client = new JumblrClient("consumer_key", "consumer_secret");
        client.setToken("oauth_token", "oauth_token_secret");

        // Write the user's name
        User user = client.user();
        System.out.println(user.getName());

        // And list their blogs
        for (Blog blog : user.getBlogs()) {
            System.out.println("\t" + blog.getTitle());
        }

        List<Post> posts = client.userDashboard();
        for(Post post : posts){
        }
        // Like the most recent "lol" tag
        client.tagged("lol").get(0).like();

    }

}


