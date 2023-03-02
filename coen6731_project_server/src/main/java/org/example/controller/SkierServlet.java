package org.example.controller;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.example.model.LiftRide;
import org.example.model.LiftRideEvent;

@WebServlet("skiers/*")
public class SkierServlet extends HttpServlet{
//	private MongoDBUtil mongoDBUtil;
//	private MongoDatabase database;
//	
//	@Override
//	public void init() throws ServletException {
//		mongoDBUtil = new MongoDBUtil();
//		database = mongoDBUtil.getDatabase();
//	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Gson gson = new Gson();
		JsonObject jsonObject;
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
//		MongoCollection collection;
//		try {
//			collection = database.getCollection("skier_lift_rides");
//		}catch(MongoException e) {
//        	response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//        	return;
//		}
		
		String[] pathParts = request.getRequestURI().split("/");
		// /skiers/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}
		// get path parameter
	    if (pathParts[pathParts.length - 2].equals("skiers") && pathParts[pathParts.length-4].equals("days") && pathParts[pathParts.length-6].equals("seasons")) {
	    	// path parameter
	        String resortIDString = pathParts[pathParts.length - 7];
	        String seasonID = pathParts[pathParts.length - 5]; 
	        String dayID = pathParts[pathParts.length - 3]; 
	        String skierIDString = pathParts[pathParts.length - 1];
	        
	        // validation
	        int resortID, dayIDInt, skierID;
	        try {
	        	resortID = Integer.parseInt(resortIDString);
	        	dayIDInt = Integer.parseInt(dayID);
	        	skierID = Integer.parseInt(skierIDString);
	        	if(skierID < 1 || skierID > 100000) {
	        		throw new Exception("skierID should be in range [1, 100000]");
	        	}
	        	if(resortID < 1 || resortID > 10) {
	        		throw new Exception("resortID should be in range [1, 10]");
	        	}
	        	if(dayIDInt < 1 || dayIDInt > 366) {
	        		throw new Exception("dayID should be in range [1, 366]");
	        	}
	        	if(seasonID.isEmpty() || seasonID == null) {
	        		throw new Exception("season ID should not be empty");
	        	}
	        } catch (Exception e) {
	        	// query value not valid
	        	jsonObject = new JsonObject();
	        	jsonObject.addProperty("message","Invalid input: " + e.getMessage());
	        	String jsonString = gson.toJson(jsonObject);
	        	
	        	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		        out.print(jsonString);
		        out.flush();
		        return;
	        }
	        
	        // get request body
	        //{
	        //	  "time": 217,
	        //	  "liftID": 21
	        //}
	        short liftID, time;
	        try {
		        StringBuilder sb = new StringBuilder();
		        BufferedReader reader = request.getReader();
		        String line;
		        while ((line = reader.readLine()) != null) {
		            sb.append(line);
		        }
		        String requestBody = sb.toString();
		        LiftRide liftRide= (LiftRide) gson.fromJson(requestBody, LiftRide.class);
		        liftID = liftRide.getLiftID();
		        time = liftRide.getTime();
		        
		        // validation
		        if(liftID < 1 || liftID > 40) {
		        	throw new Exception("liftID should be in range [1,40]");
		        }
		        if(time < 1 || time > 360) {
		        	throw new Exception("time should be in range [1,360]");
		        }
		        
		        // If the request is valid, return a 200/201 response code and some dummy data as a response body.
				response.setStatus(HttpServletResponse.SC_CREATED);
		        LiftRideEvent liftRideEvent = new LiftRideEvent(resortID,seasonID,dayID,skierID,new LiftRide(liftID,time));
				out.print(gson.toJson(liftRideEvent));
                out.flush();
			
	        } catch (Exception e) {
	        	// request body not valid
	        	jsonObject = new JsonObject();
	        	jsonObject.addProperty("message","Invalid input: " + e.getMessage());
	        	String jsonString = gson.toJson(jsonObject);
	        	
	        	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		        out.print(jsonString);
		        out.flush();
		        return;
	        }
	        
	        //send data to MongoDB
//	        LiftRideEvent liftRideEvent = new LiftRideEvent(resortID,seasonID,dayID,skierID,new LiftRide(liftID,time));
//			collection.insertOne(liftRideEvent);
//			// return all documents in the collection
//			List<LiftRideEvent> liftRides = new ArrayList<>();
//			collection.find().into(liftRides);
//			System.out.println(liftRides);
	        
	        response.setStatus(HttpServletResponse.SC_CREATED);
	    }
	}

//	@Override
//	public void destroy() {
//		mongoClient.close();
//	}

}
