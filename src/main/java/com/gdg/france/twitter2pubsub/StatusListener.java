package com.gdg.france.twitter2pubsub;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.pubsub.Pubsub;
import com.google.api.services.pubsub.model.ListTopicsResponse;
import com.google.api.services.pubsub.model.PublishRequest;
import com.google.api.services.pubsub.model.PubsubMessage;
import com.google.api.services.pubsub.model.Topic;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;

import java.io.IOException;


public class StatusListener implements twitter4j.StatusListener {

    private final Pubsub pubsub;
    private final String topicName;

    public StatusListener(final String conferenceKey, final Pubsub pubsub) throws IOException {
        this.pubsub = pubsub;
        this.topicName = "/topics/give-your-feedback/" + conferenceKey;

//        Pubsub.Topics.List list = pubsub.topics().list().setQuery("cloud.googleapis.com/project in (/projects/give-your-feedback)");
//
//        String nextPageToken = null;
//
//        do {
//            if(nextPageToken != null) {
//                list.setPageToken(nextPageToken);
//            }
//
//            ListTopicsResponse response = list.execute();
//
//            // Process each topic
//            for (Topic myTopic : response.getTopic()) {
//                System.out.println(myTopic.getName());
//            }
//            nextPageToken = response.getNextPageToken();
//        } while (nextPageToken != null);



        try {
            pubsub.topics().get(topicName).execute();
        } catch (IOException ex) {
            ex.printStackTrace();
            Topic topic = new Topic().setName(topicName);
            pubsub.topics().create(topic).execute();
        }

    }

    @Override
    public void onStatus(Status status) {
        System.out.println(topicName + ": @" + status.getUser().getScreenName() + " - " + status.getUser().getFollowersCount() + " - " + status.getText());
        publish(status);
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
        System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
    }

    @Override
    public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
        System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
    }

    @Override
    public void onScrubGeo(long userId, long upToStatusId) {
        System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
    }

    @Override
    public void onStallWarning(StallWarning warning) {
        System.out.println("Got stall warning:" + warning);
    }

    @Override
    public void onException(Exception ex) {
        ex.printStackTrace();
    }

    public void publish(Status status) {
        String content = "@" + status.getUser().getScreenName() + " - " + status.getUser().getFollowersCount() + " - " + status.getText();

        PubsubMessage message = new PubsubMessage();
        try {
            message.encodeData(content.getBytes("UTF-8"));
            PublishRequest request = new PublishRequest();
            request.setTopic(topicName).setMessage(message);
            pubsub.topics().publish(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
