package com.mongodb.quickstart;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.quickstart.models.SalesOrder;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPFactory;
import com.solacesystems.jcsmp.TextMessage;
import com.solacesystems.jcsmp.XMLMessageProducer;
import org.bson.BsonDocument;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import java.util.List;
import java.util.function.Consumer;

import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Filters.in;
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
    private static CodecRegistry codecRegistry = null;
    private static final ObjectMapper objectMapper = new ObjectMapper();



    public static void main(String[] args) throws JCSMPException {
        ConnectionString connectionString = new ConnectionString(mongoDBURI);
        CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
        codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .codecRegistry(codecRegistry)
                .build();



        SolaceUtility.initializeAndConnectionSession();
        try {
            resumeToken = SolaceUtility.getResumeToken();
            producer = SolaceUtility.getMessageProducer();

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
            SolaceUtility.closeSession();
        }
    }

    private static final Consumer<ChangeStreamDocument<SalesOrder>> sendChangeStreamDocumentUpdate = event -> {
        try {
            SalesOrder salesOrder = event.getFullDocument();
            salesOrder.setResumeToken(event.getResumeToken().toJson());
            message.setText(objectMapper.writeValueAsString(salesOrder));
            producer.send(message, JCSMPFactory.onlyInstance().createTopic(topicString + "transactions/" + event.getFullDocument().getDistributionChannel() + "/" + event.getFullDocument().getSalesOrderNumber() ));
            message.reset();
        } catch (JCSMPException | JsonProcessingException e) {
            System.out.printf("### Caught while trying to producer.send(): %s%n",e);
        }
    };

}
