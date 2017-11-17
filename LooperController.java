package com.ibm.hybrid.cloud.sample.portfolio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LooperController implements Runnable {
	private int iteration = 0;
	private int count = 0;
	private String fullURL = null;
	private static int completed = 0;

	public static void main(String[] args) {
		if (args.length == 3) try {
			loop(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));
		} catch (Throwable t) {
			t.printStackTrace();
		} else {
			System.out.println("Usage:   loopctl <url> <count> <threads>");
			System.out.println("Example: loopctl http://looper-service:9080/looper 5000 20");
			System.out.println("Result:  Looper runs 100,000 times total (spread across 20 parallel threads)");
		}
	}

	public LooperController(String url, int index, int times) {
		fullURL = url+"?id=Looper"+index;
		iteration = index;
		count = times;
	}

	public static void loop(String url, int times, int threads) throws InterruptedException {
		for (int index=1; index<=threads; index++) {
			LooperController controller = new LooperController(url, index, times);
			Thread thread = new Thread(controller);
			thread.start(); //launch the run() method in a separate thread
		}

		//don't let the JVM exit until all the spawned threads complete
		while (completed<threads) Thread.sleep(1000);
	}

	private static String invokeREST(String verb, String uri) throws IOException {
		URL url = new URL(uri);

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setRequestMethod(verb);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setDoOutput(true);
		InputStream stream = conn.getInputStream();

		String response = stringFromStream(stream);

		stream.close();

		return response; //I use JsonStructure here so I can return a JsonObject or a JsonArray
	}

	private static String stringFromStream(InputStream in) throws IOException
	{
	    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	    StringBuilder out = new StringBuilder();
	    String line = null;
	    while ((line = reader.readLine()) != null) {
	        out.append(line+"\n");
	    }
	    return out.toString();
	}

	@Override
	public void run() {
		try {
			for (int index=1; index<=count; index++) {
				System.out.println("Thread #"+iteration+", iteration #"+index);

				long start = System.currentTimeMillis();

				System.out.println(invokeREST("GET", fullURL));

				long end = System.currentTimeMillis();

				System.out.println("Elapsed time for thread #"+iteration+", iteration #"+index+": "+(end-start)+" ms");
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		completed++;
	}
}
