package org.example.coen6731_project_client1.controller;

import java.util.Random;
import java.util.concurrent.BlockingQueue;

import org.example.coen6731_project_client1.model.LiftRide;
import org.example.coen6731_project_client1.model.LiftRideEvent;

public class LiftRideEventGeneratorThread extends Thread {
	
	private BlockingQueue<LiftRideEvent> queue;
	private int num_clients;
	private int num_post_requests_per_thread;
	
	public LiftRideEventGeneratorThread(BlockingQueue<LiftRideEvent> queue, int num_clients, int num_post_requests_per_thread) {
		this.queue = queue;
		this.num_clients = num_clients;
		this.num_post_requests_per_thread = num_post_requests_per_thread;
	}
	
	@Override
	public void run() {
		for (int i = 0; i < num_clients * num_post_requests_per_thread; i++) {
//		while(true) {
			LiftRideEvent liftRideEvent = dataGeneration();
//			System.out.println(liftRideEvent);
			queue.add(liftRideEvent);
		}
	}
	
	// create an object that generates a random skier lift ride event that can be used to form a POST request
	LiftRideEvent dataGeneration() {
		Random random = new Random();
		int resortID = random.nextInt(10) + 1;
		int skierID = random.nextInt(100000) + 1;
		String seasonID = "2022";
		String dayID = "1";
		short liftID = (short) (random.nextInt(40) + 1);
		short time = (short) (random.nextInt(360) + 1);
		LiftRide liftRide = new LiftRide(time, liftID);
		LiftRideEvent liftRideEvent = new LiftRideEvent(resortID, seasonID, dayID, skierID, liftRide);
		return liftRideEvent;
	}
}
