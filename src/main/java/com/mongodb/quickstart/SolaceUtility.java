package com.mongodb.quickstart;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.quickstart.models.SalesOrder;
import com.solacesystems.common.util.TopicSubscription;
import com.solacesystems.jcsmp.*;
import org.bson.BsonDocument;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class SolaceUtility {

    public static JCSMPSession jcsmpSession = null;
    private static final ObjectMapper objectMapper = new ObjectMapper();


    public static void initializeAndConnectionSession() {
        // Configure Solace Connection
        // TODO move values to properties
        final JCSMPProperties properties = new JCSMPProperties();
        properties.setProperty(JCSMPProperties.HOST, "tcp://localhost:55554");
        properties.setProperty(JCSMPProperties.VPN_NAME, "default");
        properties.setProperty(JCSMPProperties.USERNAME, "connector");
        properties.setProperty(JCSMPProperties.PASSWORD, "connector");

        JCSMPChannelProperties channelProperties = new JCSMPChannelProperties();
        channelProperties.setReconnectRetries(20);
        channelProperties.setConnectRetriesPerHost(5);
        properties.setProperty(JCSMPProperties.CLIENT_CHANNEL_PROPERTIES, channelProperties);

        try {
            jcsmpSession = JCSMPFactory.onlyInstance().createSession(properties, null, sessionEventArgs -> System.out.printf("### Received a Session event: %s%n", sessionEventArgs));
            jcsmpSession.connect();
        } catch (JCSMPException e) {
            throw new RuntimeException(e);
        }
    }

    public static void closeSession() {
        if (jcsmpSession != null && !jcsmpSession.isClosed()) {
            jcsmpSession.closeSession();
        }
    }

    public static XMLMessageProducer getMessageProducer() {
        if (jcsmpSession == null || jcsmpSession.isClosed()) {
            initializeAndConnectionSession();
        }

        try {
            return jcsmpSession.getMessageProducer(new JCSMPStreamingPublishCorrelatingEventHandler() {
                @Override
                public void responseReceivedEx(Object o) {

                }

                @Override
                public void handleErrorEx(Object key, JCSMPException cause, long l) {
                    System.out.printf("### Producer handleErrorEx() callback: %s%n", cause);
                    if (cause instanceof JCSMPErrorResponseException e) {  // might have some extra info
                        System.out.println(JCSMPErrorResponseSubcodeEx.getSubcodeAsString(e.getSubcodeEx())
                                + ": " + e.getResponsePhrase());
                        System.out.println(cause);
                    }
                }
            });
        } catch (JCSMPException e) {
            // TODO: clean up error handling
            throw new RuntimeException(e);
        }
    }

    public static BsonDocument getResumeToken() {
        if (jcsmpSession == null || jcsmpSession.isClosed()) {
            initializeAndConnectionSession();
        }

        Queue lvq = JCSMPFactory.onlyInstance().createQueue("Q.MONGODB.CHANGESTREAM.LVQ");
        BrowserProperties browserProperties = new BrowserProperties();
        browserProperties.setEndpoint(lvq);
        browserProperties.setTransportWindowSize(1);
        browserProperties.setWaitTimeout(1000);
        BsonDocument resumeToken = null;
        try {
            Browser lvqBrowser = jcsmpSession.createBrowser(browserProperties);
            BytesXMLMessage message = lvqBrowser.getNext();
            if (message != null) {
                String messagePayload = ((TextMessage) message).getText();
                SalesOrder salesOrder = objectMapper.readValue(messagePayload, SalesOrder.class);
                resumeToken = BsonDocument.parse(salesOrder.getResumeToken());
            }
            System.out.println(message);
            lvqBrowser.close();
            return resumeToken;
        } catch (JCSMPException e) {
            // todo: clean up error handling
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


    }
}
