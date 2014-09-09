package com.gdg.france.twitter2pubsub;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import twitter4j.FilterQuery;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    private final static String SERVER_URL = "https://give-your-feedback.appspot.com/_ah/api/giveyourfeedback/v2/conference";
    static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static final JsonFactory JSON_FACTORY = new JacksonFactory();

    public static void main(String[] args) throws IOException {
        Map<String, String> hashTags = getHashTags();
        if (hashTags != null && hashTags.size() > 0) {
            TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
            StatusListener listener = new StatusListener();
            FilterQuery fq = new FilterQuery();
            fq.track(hashTags.values().toArray(new String[]{}));
            fq.language(new String[]{"en", "fr"});
            twitterStream.addListener(listener);
            twitterStream.filter(fq);
        }
    }

    private static Map<String, String> getHashTags() throws IOException {
        HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(request -> request.setParser(new JsonObjectParser(JSON_FACTORY)));
        HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(SERVER_URL));
        Map<String, List<Map<String, String>>> response = request.execute().parseAs(Map.class);
        return response.get("items").stream().collect(Collectors.toMap(map -> map.get("id"), map -> map.get("name")));
    }
}
