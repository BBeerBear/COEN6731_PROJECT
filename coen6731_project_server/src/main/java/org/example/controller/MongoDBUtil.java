package org.example.controller;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.ArrayList;
import java.util.List;

import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.example.model.LiftRide;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoDBUtil {
    private MongoClient mongoClient;
    private MongoDatabase database;
	
	public MongoDBUtil() {
		String uri = "mongodb+srv://bigbear:dcWrbvoj83x1C1iH@cluster1.pvadida.mongodb.net/?retryWrites=true&w=majority";
		CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
		CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));
		try {
			mongoClient = MongoClients.create(uri);
			database = mongoClient.getDatabase("skier_rider").withCodecRegistry(pojoCodecRegistry);
		}catch(MongoException e) {
			throw new RuntimeException("Failed to connect to MongoDB", e);
		}
	}

	public MongoDatabase getDatabase() {
		return database;
	}

	public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
	
}
