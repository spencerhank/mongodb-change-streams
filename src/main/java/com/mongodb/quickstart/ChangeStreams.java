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
// TODO: Implement production readiness recommendations: https://medium.com/expedia-group-tech/mongo-change-streams-in-production-97a07c7c0420
// 1. Include timestamp of document so we can restart based on timestamp in case oplog is deleted or resume token is not present in the current oplog
// 2. Start new change stream if there is an issue with the old one
// 3. make properties configurable
// 4. Add ability to configure and start multiple change streams on separate threads
// Note: Performance will be impacted if oplog becomes too large. Should be able to process ~6,0000 documents per second
public class ChangeStreams {

    private final static String mongoDBURI = "mongodb://admin:admin@localhost:27017";
    private final static String mongoDBName = "demo";
    private final static String mongoDBCollection = "transactions";
    private final static String topicString = "cdc/mongo/changestream/";
    private final static TextMessage message = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
    private static XMLMessageProducer producer = null;
    private static final ObjectMapper objectMapper = new ObjectMapper();



    public static void main(String[] args) throws JCSMPException {
        ConnectionString connectionString = new ConnectionString(mongoDBURI);
        CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .codecRegistry(codecRegistry)
                .build();



        SolaceUtility.initializeAndConnectionSession();
        try {
            BsonDocument resumeToken = SolaceUtility.getResumeToken();
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
