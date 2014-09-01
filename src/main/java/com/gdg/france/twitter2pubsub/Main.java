package com.gdg.france.twitter2pubsub;

import twitter4j.FilterQuery;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

public class Main {

    public static void main(String[] args) {
        if (args != null && args.length > 0) {
            TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
            StatusListener listener = new StatusListener();
            FilterQuery fq = new FilterQuery();
            fq.track(args);
            fq.language(new String[]{"en", "fr"});
            twitterStream.addListener(listener);
            twitterStream.filter(fq);
        }
    }
}
