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
        MultithreadedClient multithreadedClient2 = new MultithreadedClient(100, 100);
        multithreadedClient2.start();
        try {
			multithreadedClient2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
}
