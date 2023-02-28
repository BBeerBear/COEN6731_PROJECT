//package org.example.controller;
//
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import org.bson.Document;
//import org.example.model.Resort;
//
//import com.google.gson.Gson;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import com.mongodb.client.MongoCollection;
//import com.mongodb.client.MongoDatabase;
//
//@WebServlet("resorts/*")
//public class ResortServlet extends HttpServlet {
//
//	private static final long serialVersionUID = 1L;
//	private MongoDBUtil mongoDBUtil;
//	private MongoDatabase database;
//	private MongoCollection<Document> resortsCollection;
//	@Override
//	 public void init() throws ServletException {
//		mongoDBUtil = new MongoDBUtil();
//		database = mongoDBUtil.getDatabase();
//		resortsCollection = database.getCollection("resort");
//	 }
//	
//	@Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		PrintWriter out = response.getWriter();
//		response.setContentType("application/json");
//		response.setCharacterEncoding("UTF-8");
//		Gson gson = new Gson();
//		JsonObject jsonObject = new JsonObject();
//		
//		String[] pathParts = request.getRequestURI().split("/");	
//		
//		// /resorts
//		if(pathParts[pathParts.length - 1].equals("resorts")) {
//			MongoCollection<Document> collection = database.getCollection("resorts");
//			List<Resort> resorts = new ArrayList<>();
//			for (Document doc : collection.find()) {
//			    Resort resort = new Resort();
//			    resort.setResortName(doc.getString("resortName"));
//			    resort.setResortID(doc.getInteger("resortID"));
//			    resorts.add(resort);
//			}
////			System.out.println(resorts.get(0));
////			jsonObject = new JsonObject();
////        	jsonObject.addProperty("resorts",resorts.toString());
////			class ResortsResponse {
////				  private List<Resort> resorts;
////
////				  public ResortsResponse(List<Resort> resorts) {
////				    this.resorts = resorts;
////				  }
////			
////			}
////			ResortsResponse resortsResponse = new ResortsResponse(resorts);
////        	String jsonString = gson.toJson(resortsResponse);
//        	String jsonString = gson.toJson(resorts);
//        	response.setStatus(HttpServletResponse.SC_OK);
//	        out.print(jsonString);
//	        out.flush();
//	        return;
//		} 
////		/resorts/{resortID}/seasons/{seasonID}/day/{dayID}/skiers
//		else if (pathParts[pathParts.length - 1].equals("skiers") && pathParts[pathParts.length - 3].equals("day") && pathParts[pathParts.length - 5].equals("seasons")) {
//			
//		}
////		if (pathInfo == null) {
////			// MongoDB
////			List<Resort> resorts = new ArrayList<>();
////			resorts.add(new Resort(1,"adb"));
////			resorts.add(new Resort(2,"adf"));
////			// MongoDB
////		    JsonElement element = gson.toJsonTree(resorts);
////		    
////	        response.setStatus(HttpServletResponse.SC_OK);
////	        out.println(element.toString());
////	        out.flush(); 
////        } else {
////        	// get number of unique skiers at resort/season/day
////        	// url: /skiers/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}
////        	List<String> paths = Arrays.asList(pathInfo.split("/"));
////        	if(paths.contains("seasons") && paths.contains("days") && paths.contains("skiers")) {
////        		String resortID = paths.get(1);
////        		String seasonID = paths.get(3);
////        		String dayID = paths.get(5);
////        		// MongoDB
////        		ResortSkiers resortSkiers = new ResortSkiers("Mission Ridge",78999);
////        		// MongoDB
////    	        response.setStatus(HttpServletResponse.SC_OK);
////    	        out.print(gson.toJson(resortSkiers));
////    	        out.flush();
////        	}
////        	// get a list of seasons for the specified resort
////        	// /resorts/{resortID}/seasons:
////        	else if(paths.contains("seasons")) {
////        		String resortID = paths.get(1);
////        		// MongoDB
////        		SeasonsList seasonsList = new SeasonsList(Arrays.asList("string"));
////        		// MongoDB
////    	        response.setStatus(HttpServletResponse.SC_OK);
////    	        out.print(gson.toJson(seasonsList));
////    	        out.flush();
////        	}
////        }
//	}
////
////    @Override
////    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
////    	String pathInfo = request.getPathInfo();
////		response.setContentType("application/json");
////		response.setCharacterEncoding("UTF-8");
////		Gson gson = new Gson();
////		if (pathInfo != null) {
////			List<String> paths = Arrays.asList(pathInfo.split("/"));
////			// /resorts/{resortID}/seasons:
////			// Add a new season for a resort
////        	if(paths.contains("seasons")) {
////        		String resortID = paths.get(1);
////        		//requestBody
////        		StringBuilder sb = new StringBuilder();
////                String s;
////                while ((s = request.getReader().readLine()) != null) {
////                    sb.append(s);
////                }
////                JsonObject jsonObject = gson.fromJson(sb.toString(),JsonObject.class);
////                String year = jsonObject.get("year").getAsString();
////                System.out.println(year);
////        		//MongoDB
////                
////                //MongoDB
////    	        response.setStatus(HttpServletResponse.SC_CREATED);
////        	}
////		}
////    }
//}
////
////
