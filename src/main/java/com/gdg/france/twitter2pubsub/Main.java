package com.gdg.france.twitter2pubsub;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.pubsub.Pubsub;
import com.google.api.services.pubsub.PubsubScopes;
import twitter4j.FilterQuery;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    private final static String SERVER_URL = "https://give-your-feedback.appspot.com/_ah/api/giveyourfeedback/v2/conference";
    static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static final JsonFactory JSON_FACTORY = new JacksonFactory();
    // Suggested format for application names is MyCompany-MyProject/Version
    private static final String APPLICATION_NAME = "MY_APPLICATION";

    public static void main(String[] args) throws IOException, GeneralSecurityException, URISyntaxException {
        Map<String, String> hashTags = getHashTags();
//        hashTags.put("fake", "#iphone6");
//        hashTags.put("otherfake", "#skype");


        if (hashTags != null && hashTags.size() > 0) {
            Pubsub pubsub = createPubsubClient();

            for (String key : hashTags.keySet()) {
                TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
                StatusListener listener = new StatusListener(key, pubsub);
                FilterQuery fq = new FilterQuery();
                fq.track(new String[]{hashTags.get(key)});
                fq.language(new String[]{"en", "fr"});
                twitterStream.addListener(listener);
                twitterStream.filter(fq);
            }
        }
    }

    private static Map<String, String> getHashTags() throws IOException {
        HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(request -> request.setParser(new JsonObjectParser(JSON_FACTORY)));
        HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(SERVER_URL));
        Map<String, List<Map<String, String>>> response = request.execute().parseAs(Map.class);
        return response.get("items").stream().collect(Collectors.toMap(map -> map.get("id"), map -> map.get("name")));
    }

    private static Pubsub createPubsubClient() throws GeneralSecurityException, IOException, URISyntaxException {
        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(HTTP_TRANSPORT)
                .setJsonFactory(JSON_FACTORY)
                .setServiceAccountId("700903555117-ovhb9nroo944p2ppu6vv6106cebq8j0j@developer.gserviceaccount.com")
                .setServiceAccountScopes(PubsubScopes.all())
                .setServiceAccountPrivateKeyFromP12File(new File(Main.class.getClassLoader().getResource("give-your-feedback-674540f6ff9e.p12").toURI()))
                .build();

        Pubsub client = new Pubsub.Builder(HTTP_TRANSPORT, JSON_FACTORY, null)
                .setHttpRequestInitializer(credential)
                .setApplicationName(APPLICATION_NAME)
                .build();

        return client;
    }
}
