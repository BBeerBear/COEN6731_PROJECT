package org.example.coen6731_project_client1;


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

import org.example.coen6731_project_client1.controller.LiftRideEventGeneratorThread;
import org.example.coen6731_project_client1.controller.MultithreadedClient;
import org.example.coen6731_project_client1.model.LiftRide;
import org.example.coen6731_project_client1.model.LiftRideEvent;

import com.google.gson.Gson;



/**
 * Hello world!
 *
 */
public class Client1Starter
{
	//	private static final int NUM_THREADS = 32;
	//	private static final int NUM_POST_REQUESTS_PER_THREAD = 1000;
	
    public static void main( String[] args )
    {
        
        // At startup, you must create 32 threads that each send 1000 POST requests and terminate.
        MultithreadedClient multithreadedClient = new MultithreadedClient(32, 1000);
        multithreadedClient.start();
        try {
			multithreadedClient.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        System.out.println();
        
        // Once any of these have completed you are free to create as few or as many threads as you like until all the 10K POSTS have been sent.
        MultithreadedClient multithreadedClient2 = new MultithreadedClient(100, 100);
        multithreadedClient2.start();
        try {
			multithreadedClient2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        System.out.println();
        
        // Once any of these have completed you are free to create as few or as many threads as you like until all the 10K POSTS have been sent.
        MultithreadedClient multithreadedClient3 = new MultithreadedClient(1, 500);
        multithreadedClient3.start();
        try {
			multithreadedClient3.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

    }
    
}
