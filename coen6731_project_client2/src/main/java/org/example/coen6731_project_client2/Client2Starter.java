package org.example.coen6731_project_client2;

import org.example.coen6731_project_client2.controller.MultithreadedClient;

/**
 * Hello world!
 *
 */
public class Client2Starter 
{
    public static void main( String[] args )
    {
    	 // Once any of these have completed you are free to create as few or as many threads as you like until all the 10K POSTS have been sent.
        MultithreadedClient multithreadedClient2 = new MultithreadedClient(100, 100);
        multithreadedClient2.start();
        try {
			multithreadedClient2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
}
