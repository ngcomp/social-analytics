package com.ngcomp.analytics.engine.connector.instagram;

import com.google.gson.Gson;
import com.ngcomp.analytics.engine.connector.instagram.dto.InstagramDTO;

public class SampleInstagram {
	public static void main(String[]args){


        SearchInstagram in = new SearchInstagram("","");

        Gson gson = new Gson();

		InstagramDTO[] taggedMedia = in.getTaggedMedia("penguin",  false);

        for(InstagramDTO a : taggedMedia){
            System.out.println(gson.toJson(a));
        }

	}
}
