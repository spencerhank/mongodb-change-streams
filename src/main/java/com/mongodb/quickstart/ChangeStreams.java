package com.mongodb.quickstart;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.quickstart.models.Grade;
import com.mongodb.quickstart.models.SalesOrder;
import com.solacesystems.jcsmp.*;
import org.bson.BsonDocument;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import java.util.List;
import java.util.function.Consumer;

import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;
import static com.mongodb.client.model.changestream.FullDocument.UPDATE_LOOKUP;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class ChangeStreams {

    private final static String mongoDBURI = "mongodb://admin:admin@localhost:27017";
    private final static String mongoDBName = "demo";
    private final static String mongoDBCollection = "transactions";
    // TODO: fetch lest event and get resume token value from LVQ
    private static BsonDocument resumeToken = null;
    private final static String topicString = "cdc/mongo/changestream/";
    private final static TextMessage message = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
    private static XMLMessageProducer producer = null;

    public static void main(String[] args) throws JCSMPException {
        ConnectionString connectionString = new ConnectionString(mongoDBURI);
        CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .codecRegistry(codecRegistry)
                .build();

        // Configure Solace Connection
        final JCSMPProperties properties = new JCSMPProperties();
        properties.setProperty(JCSMPProperties.HOST, "tcp://localhost:55554");
        properties.setProperty(JCSMPProperties.VPN_NAME, "default");
        properties.setProperty(JCSMPProperties.USERNAME, "connector");
        properties.setProperty(JCSMPProperties.PASSWORD, "connector");

        JCSMPChannelProperties channelProperties = new JCSMPChannelProperties();
        channelProperties.setReconnectRetries(20);
        channelProperties.setConnectRetriesPerHost(5);
        properties.setProperty(JCSMPProperties.CLIENT_CHANNEL_PROPERTIES, channelProperties);

        final JCSMPSession jcsmpSession;
        jcsmpSession = JCSMPFactory.onlyInstance().createSession(properties, null, new SessionEventHandler() {
            @Override
            public void handleEvent(SessionEventArgs sessionEventArgs) {
                System.out.printf("### Received a Session event: %s%n", sessionEventArgs);
            }
        });
        try {
            jcsmpSession.connect();

            producer = jcsmpSession.getMessageProducer(new JCSMPStreamingPublishCorrelatingEventHandler() {
                @Override
                public void responseReceivedEx(Object o) {

                }

                @Override
                public void handleErrorEx(Object key, JCSMPException cause, long l) {
                    System.out.printf("### Producer handleErrorEx() callback: %s%n", cause);
                    if (cause instanceof JCSMPErrorResponseException) {  // might have some extra info
                        JCSMPErrorResponseException e = (JCSMPErrorResponseException) cause;
                        System.out.println(JCSMPErrorResponseSubcodeEx.getSubcodeAsString(e.getSubcodeEx())
                                + ": " + e.getResponsePhrase());
                        System.out.println(cause);
                    }
                }
            });

            try (MongoClient mongoClient = MongoClients.create(clientSettings)) {
                MongoDatabase db = mongoClient.getDatabase(mongoDBName);
                MongoCollection<SalesOrder> salesOrderCollection = db.getCollection(mongoDBCollection, SalesOrder.class);
                List<Bson> pipeline = List.of(match(in("operationType", List.of("update", "insert", "delete"))));
                if (resumeToken != null) {
                    salesOrderCollection.watch(pipeline).resumeAfter(resumeToken).forEach(sendChangeStreamDocumentUpdate);
                } else {
                    salesOrderCollection.watch(pipeline).forEach(sendChangeStreamDocumentUpdate);
                }
            }
        } finally {
            if (!jcsmpSession.isClosed()) {
                jcsmpSession.closeSession();
            }
        }
    }

    private static final Consumer<ChangeStreamDocument<SalesOrder>> sendChangeStreamDocumentUpdate = event -> {
        message.setText(event.toString());
        try {
            producer.send(message, JCSMPFactory.onlyInstance().createTopic(topicString + "transactions/" + event.getFullDocument().getDistributionChannel() + "/" + event.getFullDocument().getSalesOrderNumber() ));
            message.reset();
        } catch (JCSMPException e) {
            System.out.printf("### Caught while trying to producer.send(): %s%n",e);
        }
    };

}
