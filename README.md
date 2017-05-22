# Social Analytics Engine
Social Analytics with most popular and latest trends.

* HBase has been used to store social events (tweets, posts etc)
* We automatically pull out the most meaning ful image on page/post. 
* Redirects and bitly links are followed.
* Credentials are stored in MySQL. 
* Swagger is integrate for API's.
* KEA is used to extract keyphrases from text documents.

Social Networks 
* Facebook 
* Bing
* Instagram
* Pinterest
* RSS 
* Tumbler
* Twitter


Market        => Country
Brand/BrandId => {Carlsberg | Tuborg |Somersby | Kronenbourg 1664 | Penguin Juice}
Platform      => {facebook | bing }

###### Request:
```
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
```

##### Response:
```
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
```

