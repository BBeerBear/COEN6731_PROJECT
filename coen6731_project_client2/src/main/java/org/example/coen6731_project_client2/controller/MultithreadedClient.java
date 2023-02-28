package org.example.coen6731_project_client2.controller;



import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.example.coen6731_project_client2.model.LiftRide;
import org.example.coen6731_project_client2.model.LiftRideEvent;
import org.example.coen6731_project_client2.util.CsvWriter;
import org.example.coen6731_project_client2.util.MedianCalculator;
import org.example.coen6731_project_client2.util.P99Calculator;

import com.google.gson.Gson;

public class MultithreadedClient extends Thread{
	private int num_of_thread;
	private int num_of_requests_each_thread;
	private final int MAX_RETRIES = 5;
	private CsvWriter writer = null;
	// Create a blocking queue for lift ride events
	private BlockingQueue<LiftRideEvent> queue = new LinkedBlockingQueue<>();
	// Create a CSV file
	String csvFilePath = "D:/Profiling Performance.csv";
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
	    try{
	    	writer = new CsvWriter(csvFilePath);
	    	String[] headers = {"start time", "request type", "latency(ms)", "response code"};
	    	writer.write(headers);
	        // Start the lift ride event generator thread
	        LiftRideEventGeneratorThread generatorThread = new LiftRideEventGeneratorThread(queue,num_of_thread,num_of_requests_each_thread);
	        generatorThread.start();
			for(int i = 0; i < num_of_thread; i++) {
				executorService.submit(()->{
					for(int j = 0; j < num_of_requests_each_thread; j++) {
						try {
							long requestStartTime = System.currentTimeMillis();
//							Date currentDate = new Date(requestStartTime);
//							SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//							String requestStartTimeString = dateFormat.format(currentDate);
							String requestStartTimeString = Long.toString(requestStartTime);
							
							LiftRideEvent liftRideEvent = queue.take();
							String url = "http://localhost:8080/coen6731/skiers/" + Integer.toString(liftRideEvent.getResortID()) + "/seasons/" + liftRideEvent.getSeasonID() + "/days/" + liftRideEvent.getDayID() + "/skiers/" + Integer.toString(liftRideEvent.getSkierID());
							String requestBody = new Gson().toJson(liftRideEvent.getLiftRide());
							
							
							HttpRequest request = HttpRequest.newBuilder()
									  .uri(URI.create(url))
									  .POST(HttpRequest.BodyPublishers.ofString(requestBody))
									  .build();
							
							HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
							long latency = System.currentTimeMillis() - requestStartTime;
							eachRequestTimes.add(latency);
							String latencyString = Long.toString(latency);
							
							int retries = 0;
							// Retry up to MAX_RETRIES times for 4XX and 5XX response codes
							while (response.statusCode() >= 400 && retries < MAX_RETRIES) {
								retries++;
								System.out.println("Request failed with status code " + response.statusCode() + ", retrying (attempt " + retries + ")");
								response = client.send(request, HttpResponse.BodyHandlers.ofString());
//								eachRequestTimes.add(System.currentTimeMillis() - requestStartTime);
							}
							
							String responseCode = Integer.toString(response.statusCode());
							String[] csvLine = {requestStartTimeString, "POST", latencyString, responseCode};
							writer.write(csvLine);
							// Check if the request was successful
							if (response.statusCode() >= 400) {
								System.out.println("Request failed after " + MAX_RETRIES + " retries with status code " + response.statusCode());
								successLatch.countDown();
							} else {
	//							assertThat(response.statusCode(), equalTo(201));
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
			
			generatorThread.join();
			executorService.shutdown();
			executorService.awaitTermination(10, TimeUnit.MINUTES);
			writer.close();
		} catch (InterruptedException | IOException e1) {
			e1.printStackTrace();
		} 
	    
	    int totalResponseTime = 0;
	    for(long time: eachRequestTimes) {
	    	totalResponseTime += time;
	    }
	    
	    double meanResponseTime = (double) totalResponseTime / eachRequestTimes.size();
	    double medianResponseTime = MedianCalculator.calculateMedian(eachRequestTimes);
		
	    long endTime = System.currentTimeMillis();
		long wallTime = endTime - startTime;
	    double throughput = (double) num_of_thread * num_of_requests_each_thread / (wallTime / 1000.0);
	    
	    long p99ResponseTime = P99Calculator.calculateP99(eachRequestTimes);
	    
	    long minResponseTime = Collections.min(eachRequestTimes);
	    long maxResponseTime = Collections.max(eachRequestTimes);
	    
	    
	    System.out.println("Mean response time: " + meanResponseTime + "ms");
	    System.out.println("Median response time: " + meanResponseTime + "ms");
	    System.out.println("Throughput: " + throughput + " requests/second");
	    System.out.println("P99 response time: " + p99ResponseTime + "ms");
	    System.out.println("Min response time: " + minResponseTime + "ms");
	    System.out.println("Max response time: " + maxResponseTime + "ms");
	    
////	    System.out.println(totalRequestTime + "ms");
////	    System.out.println(eachRequestTimes.size());
//	    System.out.println("Each request latency: " + w_averageTimeEachRequest + "ms");
////	    System.out.println();
//	    double λ_expectedThroughput = num_of_thread / (w_averageTimeEachRequest / 1000);
//	   
//        System.out.println("Number of successful requests sent: " + successfulRequests);
//        System.out.println("Number of unsuccessful requests: " + unsuccessfulRequests);
//        System.out.println("Total run time: " + wallTime + " ms");
//        System.out.println("According to Little's Law, expected throughput is: " + λ_expectedThroughput + " requests/second");
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
