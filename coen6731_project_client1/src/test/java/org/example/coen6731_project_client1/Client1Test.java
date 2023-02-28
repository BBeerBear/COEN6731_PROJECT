//package org.example.coen6731_project_client1;
//
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.equalTo;
//import static org.junit.Assert.assertTrue;
//
//import java.io.IOException;
//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//
//import org.example.model.LiftRide;
//import org.junit.Test;
//
//import com.google.gson.Gson;
//
///**
// * Unit test for client1starter.
// */
//public class Client1Test
//{
//    /**
//     * Rigorous Test :-)
//     */
//	// calls the API before proceeding, to establish that you have connectivity.
//		@Test
//		void simpleClientTest() {
//			
//			HttpClient client = HttpClient.newHttpClient();
//			
//			String serviceUrl =  "http://localhost:8080/coen6731/skiers/1/seasons/2022/days/281/skiers/1";
//			LiftRide liftRide = new LiftRide((short)217,(short)21);
//			String requestBody = new Gson().toJson(liftRide);
//			HttpRequest request = HttpRequest.newBuilder()
//					  .uri(URI.create(serviceUrl))
//					  .POST(HttpRequest.BodyPublishers.ofString(requestBody))
//					  .build();
//			
//			HttpResponse<String> response;
//			try {
//				response = client.send(request, HttpResponse.BodyHandlers.ofString());
//				assertThat(response.statusCode(), equalTo(201));
//			} catch (IOException | InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//}
