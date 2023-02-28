package org.example.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import org.apache.commons.io.IOUtils;
//import org.eclipse.jetty.client.HttpClient;
//import org.eclipse.jetty.client.HttpResponse;
//import org.eclipse.jetty.client.api.ContentResponse;
//import org.eclipse.jetty.client.api.Request;
//import org.eclipse.jetty.client.util.StringContentProvider;
//import org.eclipse.jetty.http.HttpHeader;
//import org.eclipse.jetty.http.HttpMethod;
//import org.eclipse.jetty.http.MimeTypes;
//import org.eclipse.jetty.util.ssl.SslContextFactory;
//import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.example.model.LiftRide;
import org.example.model.LiftRideEvent;


class MultiClientsTest {

	
	// calls the API before proceeding, to establish that you have connectivity.
	@Test
	void simpleClientTest() {
		
		HttpClient client = HttpClient.newHttpClient();
		
		String serviceUrl =  "http://localhost:8080/coen6731/skiers/1/seasons/2022/days/281/skiers/1";
		LiftRide liftRide = new LiftRide((short)217,(short)21);
		String requestBody = new Gson().toJson(liftRide);
		HttpRequest request = HttpRequest.newBuilder()
				  .uri(URI.create(serviceUrl))
				  .POST(HttpRequest.BodyPublishers.ofString(requestBody))
				  .build();
		
		HttpResponse<String> response;
		try {
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
			assertThat(response.statusCode(), equalTo(201));
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// create 32 threads that each send 1000 POST requests and terminate.
	@Test
	void test32ThreadsEach1000POST() throws Exception {
		final int NUM_CLIENTS = 32;
		final int NUM_POST_REQUESTS_PER_CLIENT = 1000;
		final int MAX_RETRIES = 5;
		
		CountDownLatch successLatch = new CountDownLatch(NUM_CLIENTS * NUM_POST_REQUESTS_PER_CLIENT);
		CountDownLatch failureLatch = new CountDownLatch(NUM_CLIENTS * NUM_POST_REQUESTS_PER_CLIENT);

        // Create a blocking queue for lift ride events
        BlockingQueue<LiftRideEvent> queue = new LinkedBlockingQueue<>();

        List<Long> eachRequestTimes = Collections.synchronizedList(new ArrayList<>());

        ExecutorService executorService = Executors.newFixedThreadPool(NUM_CLIENTS);
        
        long startTime = System.currentTimeMillis();
        
        // Start the lift ride event generator thread
        LiftRideEventGeneratorThread generatorThread = new LiftRideEventGeneratorThread(queue,NUM_CLIENTS,NUM_POST_REQUESTS_PER_CLIENT);
        generatorThread.start();
		for(int i = 0; i < NUM_CLIENTS; i++) {
			executorService.submit(()->{
				HttpClient client = HttpClient.newHttpClient();

//				ThreadLocal<Long> threadStartTime = ThreadLocal.withInitial(System::currentTimeMillis);
				for(int j = 0; j < NUM_POST_REQUESTS_PER_CLIENT; j++) {
					long requestStartTime = System.currentTimeMillis();
					try {
						LiftRideEvent liftRideEvent = queue.take();
						String url = "http://localhost:8080/coen6731/skiers/" + Integer.toString(liftRideEvent.getResortID()) + "/seasons/" + liftRideEvent.getSeasonID() + "/days/" + liftRideEvent.getDayID() + "/skiers/" + Integer.toString(liftRideEvent.getSkierID());
						String requestBody = new Gson().toJson(liftRideEvent.getLiftRide());
						
						
						HttpRequest request = HttpRequest.newBuilder()
								  .uri(URI.create(url))
								  .POST(HttpRequest.BodyPublishers.ofString(requestBody))
								  .build();
						
						HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
						eachRequestTimes.add(System.currentTimeMillis() - requestStartTime);
//						assertThat(response.statusCode(), equalTo(201));
						
						int retries = 0;
						// Retry up to MAX_RETRIES times for 4XX and 5XX response codes
						while (response.statusCode() >= 400 && retries < MAX_RETRIES) {
							retries++;
							System.out.println("Request failed with status code " + response.statusCode() + ", retrying (attempt " + retries + ")");
							response = client.send(request, HttpResponse.BodyHandlers.ofString());
//							eachRequestTimes.add(System.currentTimeMillis() - requestStartTime);
						}
						
						// Check if the request was successful
						if (response.statusCode() >= 400) {
							System.out.println("Request failed after " + MAX_RETRIES + " retries with status code " + response.statusCode());
							successLatch.countDown();
						} else {
							assertThat(response.statusCode(), equalTo(201));
//				            System.out.println("Request successful with status code " + response.statusCode());
							failureLatch.countDown();
						}
						
					} catch (IOException | InterruptedException e) {
						successLatch.countDown();
						System.out.println("Exception in thread: " + e.getMessage());
					}
//					long threadRunTime = System.currentTimeMillis() - threadStartTime.get();
//		            System.out.println("Client finished in " + threadRunTime + " ms");
				}
			});
		}
		generatorThread.join();
		executorService.shutdown();
		executorService.awaitTermination(10, TimeUnit.MINUTES);
		
		long endTime = System.currentTimeMillis();
		long wallTime = endTime - startTime;
		int successfulRequests = NUM_CLIENTS * NUM_POST_REQUESTS_PER_CLIENT - (int)failureLatch.getCount();
	    int unsuccessfulRequests = (int)failureLatch.getCount();
	    double throughput = (double) NUM_CLIENTS * NUM_POST_REQUESTS_PER_CLIENT / (wallTime / 1000.0);
	    
	    int totalRequestTime = 0;
	    for(long time: eachRequestTimes) {
	    	totalRequestTime += time;
	    }
	    
	    // expected throughput λ = L / W
	    double w_averageTimeEachRequest = (double) totalRequestTime / eachRequestTimes.size();
//	    System.out.println(totalRequestTime + "ms");
//	    System.out.println(eachRequestTimes.size());
	    System.out.println("Each request latency: " + w_averageTimeEachRequest + "ms");
//	    System.out.println();
	    double λ_expectedThroughput = NUM_CLIENTS / (w_averageTimeEachRequest / 1000);
	   
        System.out.println("Number of successful requests sent: " + successfulRequests);
        System.out.println("Number of unsuccessful requests: " + unsuccessfulRequests);
        System.out.println("Total run time: " + wallTime + " ms");
        System.out.println("Total throughput: " + throughput + " requests/second");
        System.out.println("According to Little's Law, expected throughput is: " + λ_expectedThroughput + " requests/second");
	}
	
	// You should test how long a single request takes to estimate this latency. 
	// Run a simple test and send eg 500 requests from a single thread to do this.
	@Test
	void singleThreadWith500Requests() throws Exception {
        List<Long> eachRequestTimes = Collections.synchronizedList(new ArrayList<>());
		
		HttpClient client = HttpClient.newHttpClient();
		
		String serviceUrl =  "http://localhost:8080/coen6731/skiers/1/seasons/2022/days/281/skiers/1";
		LiftRide liftRide = new LiftRide((short)217,(short)21);
		String requestBody = new Gson().toJson(liftRide);
		HttpRequest request = HttpRequest.newBuilder()
				  .uri(URI.create(serviceUrl))
				  .POST(HttpRequest.BodyPublishers.ofString(requestBody))
				  .build();
		
		for (int i = 0; i < 500; i++) {
			long requestStartTime = System.currentTimeMillis();
			HttpResponse<String> response;
			try {
				response = client.send(request, HttpResponse.BodyHandlers.ofString());
				assertThat(response.statusCode(), equalTo(201));
				eachRequestTimes.add(System.currentTimeMillis() - requestStartTime);
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		int totalRequestTime = 0;
	    for(long time: eachRequestTimes) {
	    	totalRequestTime += time;
	    }
	    
	    // each request latency
	    double averageTimeEachRequest = (double) totalRequestTime / eachRequestTimes.size();
	    System.out.println("Simple test of each request latency: " + averageTimeEachRequest + "ms");
	}
}






