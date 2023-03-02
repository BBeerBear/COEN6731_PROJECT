package org.example.coen6731_project_client1.controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.example.coen6731_project_client1.model.LiftRide;
import org.example.coen6731_project_client1.model.LiftRideEvent;

import com.google.gson.Gson;

public class MultithreadedClient extends Thread{
	private int num_of_thread;
	private int num_of_requests_each_thread;
	private final int MAX_RETRIES = 5;
	// Create a blocking queue for lift ride events
	private BlockingQueue<LiftRideEvent> queue = new LinkedBlockingQueue<>();

    public MultithreadedClient(int num_of_thread, int num_of_requests_each_thread) {
		this.num_of_thread = num_of_thread;
		this.num_of_requests_each_thread = num_of_requests_each_thread;
    }
    
	@Override
	public void run() {
		HttpClient client = HttpClient.newHttpClient();
		if(simpleClientTest(client)) {
			System.out.println("You have connectivity to call the API...\n");
		}else {
			System.out.println("You don't have connectivity to call the API...\n");
			return;		
		}
		
		CountDownLatch successLatch = new CountDownLatch(num_of_thread * num_of_requests_each_thread);
		CountDownLatch failureLatch = new CountDownLatch(num_of_thread * num_of_requests_each_thread);

        List<Long> eachRequestTimes = Collections.synchronizedList(new ArrayList<Long>());

        ExecutorService executorService = Executors.newFixedThreadPool(num_of_thread);
        long startTime = System.currentTimeMillis();
        // Start the lift ride event generator thread
        LiftRideEventGeneratorThread generatorThread = new LiftRideEventGeneratorThread(queue,num_of_thread,num_of_requests_each_thread);
        generatorThread.start();
		for(int i = 0; i < num_of_thread; i++) {
			executorService.submit(()->{
				for(int j = 0; j < num_of_requests_each_thread; j++) {
					try {
						long requestStartTime = System.currentTimeMillis();
						LiftRideEvent liftRideEvent = queue.take();
						String url = "http://localhost:8080/coen6731/skiers/" + Integer.toString(liftRideEvent.getResortID()) + "/seasons/" + 
								liftRideEvent.getSeasonID() + "/days/" + liftRideEvent.getDayID() + "/skiers/" + Integer.toString(liftRideEvent.getSkierID());
						String requestBody = new Gson().toJson(liftRideEvent.getLiftRide());
						
						
						HttpRequest request = HttpRequest.newBuilder()
								  .uri(URI.create(url))
								  .POST(HttpRequest.BodyPublishers.ofString(requestBody))
								  .build();
						
						HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
						eachRequestTimes.add(System.currentTimeMillis() - requestStartTime);
						
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
//				            System.out.println("Request successful with status code " + response.statusCode());
							failureLatch.countDown();
						}
						
					} catch (IOException | InterruptedException e) {
						successLatch.countDown();
						System.out.println("Exception in thread: " + e.getMessage());
					}
				}
			});
		}
		try {
			generatorThread.join();
			executorService.shutdown();
			executorService.awaitTermination(10, TimeUnit.MINUTES);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		long endTime = System.currentTimeMillis();
		long wallTime = endTime - startTime;
		int successfulRequests = num_of_thread * num_of_requests_each_thread - (int)failureLatch.getCount();
	    int unsuccessfulRequests = (int)failureLatch.getCount();
	    double throughput = (double) num_of_thread * num_of_requests_each_thread / (wallTime / 1000.0);
	    
	    int totalRequestTime = 0;
	    for(long time: eachRequestTimes) {
	    	totalRequestTime += time;
	    }
	    
	    System.out.println("Number or threads used: " + num_of_thread + ", number of requests each thread: " + num_of_requests_each_thread);
	    // expected throughput λ = L / W
	    double w_averageTimeEachRequest = (double) totalRequestTime / eachRequestTimes.size();
	    double λ_expectedThroughput = num_of_thread / (w_averageTimeEachRequest / 1000);
	   
	    System.out.println("Each request latency: " + w_averageTimeEachRequest + "ms");
        System.out.println("Number of successful requests sent: " + successfulRequests);
        System.out.println("Number of unsuccessful requests: " + unsuccessfulRequests);
        System.out.println("Total run time: " + wallTime + " ms");
        System.out.println("Total throughput: " + throughput + " requests/second");
        System.out.println("According to Little's Law, expected throughput is: " + λ_expectedThroughput + " requests/second");
	}
    
	// calls the API before proceeding, to establish that you have connectivity.
    private boolean simpleClientTest(HttpClient client) {
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
			if(response.statusCode() == 201) {
				return true;
//				System.out.println("You have connectivity to call the API...\n");
			}
		} catch (IOException | InterruptedException e) {
				return false;
//			e.printStackTrace();
		}
		return false;
	}

}
